import {Component, computed, OnInit, signal} from '@angular/core';
import {ProfileData} from '@common/profile-data';
import {Role} from '@common/role';
import {
  email,
  form,
  FormField,
  FormRoot,
  maxLength,
  minLength,
  readonly,
  required,
  validate
} from '@angular/forms/signals';
import {UserService} from '@services/user-service';
import {AuthService} from '@services/auth-service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ActivatedRoute} from '@angular/router';
import {firstValueFrom} from 'rxjs';

@Component({
  selector: 'app-profile',
  imports: [
    FormField,
    FormRoot
  ],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile implements OnInit {
  changeUser = signal(false);
  userRoles: string[] = Object.values(Role);
  originalProfile = signal<ProfileData | null>(null);
  isSelf = signal(false);

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private route: ActivatedRoute,
  ) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = Number(params.get('id'));
      this.loadProfileUser(id);
    });
  }

  profileModel = signal<ProfileData>({
    id: 0,
    name: '',
    email: "",
    password: '',
    confirm_password: '',
    role: Role.USER,
    confirmed: false
  });

  profileForm = form(this.profileModel, (fieldPath) => {
      readonly(fieldPath.id);
      minLength(fieldPath.name, 3, {message: 'Minimum 3 characters'});
      required(fieldPath.name, {message: 'Name is required'});
      email(fieldPath.email, {message: 'Enter a valid email address'});
      required(fieldPath.email, {message: 'Email is required'});
      required(fieldPath.role, {message: 'Role is required'});
      minLength(fieldPath.password, 8, {message: 'Must be at least 8 characters'});
      maxLength(fieldPath.password, 100, {message: 'Password is too long'})
      validate(fieldPath.confirm_password, ({value, valueOf}) => {
        const confirmPassword = value();
        const password = valueOf(fieldPath.password);
        if (confirmPassword !== password) {
          return {
            kind: 'passwordMismatch',
            message: 'Passwords do not match',
          };
        }
        return null;
      });
    },
    {
      submission: {
        action: async f => {
          const value = f().value();
          try {
            const response = await firstValueFrom(this.userService.patchUser(value));
            const {data, message} = response;
            this.snackBar.open(
              `Congratulations ${data.name}! ${message}`,
              'ok',
              {
                duration: 3000,
                panelClass: ['success-snackbar']
              });
            if (this.isSelf()) {
              this.authService.logout();
            }
          } catch (err: any) {
            this.showError(err.error.message);
          }
        }
      }
    }
  );

  setProfile(profileData: ProfileData) {
    this.profileForm.id().value.set(profileData.id);
    this.profileForm.name().value.set(profileData.name);
    this.profileForm.email().value.set(profileData.email);
    this.profileForm.role().value.set(profileData.role);
  }

  modifyingPassword = computed(() => {
    const password = this.profileForm.password().value();
    const confirm = this.profileForm.confirm_password().value();
    return !!password || !!confirm;
  });

  isProfileDirty = computed(() => {
    const original = this.originalProfile();
    if (!original) return false;

    const current = this.profileModel();

    return (
      original.name !== current.name ||
      original.email !== current.email ||
      original.role !== current.role ||
      this.modifyingPassword()
    );
  });

  anyRegFormErrors = computed(() => {
    return (
      this.profileForm.name().invalid() ||
      this.profileForm.email().invalid() ||
      this.profileForm.role().invalid() ||
      this.profileForm.password().invalid() ||
      this.profileForm.confirm_password().invalid()
    );
  });

  submitDisabled = computed(() =>
    this.anyRegFormErrors() || !this.isProfileDirty());

  modifyingUser() {
    this.changeUser.update(v => !v);
  }

  private loadProfileUser(id: number) {
    const currentUser = this.authService.currentUser();
    if (id === currentUser?.id) {
      this.setProfile(currentUser);
      this.originalProfile.set(structuredClone(currentUser));
      this.isSelf.set(true);
      return;
    }
    this.isSelf.set(false);
    const cached = this.userService.getUserSnapshot(id);
    if (cached) {
      this.setProfile(cached);
      this.originalProfile.set(structuredClone(cached));
    } else {
      this.userService.getUserById(id).subscribe({
        next: (response) => {
          this.setProfile(response.data);
          this.originalProfile.set(structuredClone(response.data));
        },
        error: (err: any) => {
          this.showError(err.error.message);
        }
      });
    }
  }

  protected confirmAndDelete(id: number) {
    if (confirm(
      this.isSelf()
        ? 'Are you sure you want to permanently delete your own account?'
        : 'Are you sure you want to delete this user?'
    )) {
      this.removeUser(id);
    }
  }

  private removeUser(id: number) {
    this.userService.deleteUser(id)
      .subscribe({
        next: (response) => {
          this.snackBar.open(
            response.message,
            'ok',
            {
              duration: 3000,
              panelClass: ['success-snackbar']
            });
          this.authService.logout();
        },
        error: (err: any) => {
          this.showError(err.error.message);
        }
      });
  }

  showError(message: string) {
    this.snackBar.open(message, 'Close', {
      duration: 0,
      panelClass: ['error-snackbar']
    });
  }
}
