import {Component, computed, OnInit, signal} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {ProfileData} from '@common/profile-data';
import {Role} from '@common/role';
import {email, form, FormField, maxLength, minLength, readonly, required, validate} from '@angular/forms/signals';
import {UserService} from '@services/user-service';

@Component({
  selector: 'app-profile',
  imports: [
    FormsModule,
    FormField
  ],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile implements OnInit {
  changeUser = signal(false);
  userRoles: string[] = Object.values(Role);
  originalProfile = signal<ProfileData | null>(null);

  constructor(
    private userService: UserService
  ) {
  }

  ngOnInit(): void {
    const user = this.userService.getCurrentUser()
    this.setProfile(user);
    this.originalProfile.set(structuredClone(user));
  }

  profileModel = signal<ProfileData>({
    id: 0,
    name: '',
    email: "",
    password: '',
    confirm_password: '',
    role: Role.USER
  });

  profileForm = form(this.profileModel, (fieldPath) => {
    readonly(fieldPath.id);
    minLength(fieldPath.password, 8, {message: 'must be at least 8 characters'});
    maxLength(fieldPath.password, 100, {message: 'password is too long'})
    validate(fieldPath.confirm_password, ({value, valueOf}) => {
      const confirmPassword = value();
      const password = valueOf(fieldPath.password);
      if (confirmPassword !== password) {
        return {
          kind: 'passwordMismatch',
          message: 'passwords do not match',
        };
      }
      return null;
    });
  });

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
      original.role !== current.role || this.modifyingPassword()
    );
  });


  updateUser(event: Event) {
    event.preventDefault();
  }


  modifyingUser() {
    if (!this.changeUser()) {
      this.changeUser.set(true);
    } else {
      this.changeUser.set(false);
    }
  }
}
