import {Component, OnInit, signal} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {ProfileData} from '@common/profile-data';
import {Role} from '@common/role';
import {email, form, FormField, maxLength, minLength, readonly, required} from '@angular/forms/signals';
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
  private originalProfile!: ProfileData;


  constructor(
    private userService: UserService
  ) {
  }

  ngOnInit(): void {
    const user = this.userService.getCurrentUser()
    this.setProfile(user);
    this.originalProfile = structuredClone(user);
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
  });

  setProfile(profileData: ProfileData) {
    this.profileForm.id().value.set(profileData.id);
    this.profileForm.name().value.set(profileData.name);
    this.profileForm.email().value.set(profileData.email);
    this.profileForm.role().value.set(profileData.role);
  }

  updateUser() {

  }

  isEditFormDisabled() {
    return false;
  }

  modifyingUser() {
    if (!this.changeUser()) {
      this.changeUser.set(true);
    } else {
      this.changeUser.set(false);
    }
  }
}
