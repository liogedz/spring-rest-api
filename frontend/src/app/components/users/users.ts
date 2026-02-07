import {Component, inject} from '@angular/core';
import {UserService} from '@services/user-service';
import {RouterLink} from '@angular/router';
import {AuthService} from '@services/auth-service';

@Component({
  selector: 'app-users',
  imports: [
    RouterLink
  ],
  templateUrl: './users.html',
  styleUrl: './users.css',
})
export class Users {
  private userService = inject(UserService);
  users = this.userService.users;

  constructor(private authService: AuthService) {
  }

  protected confirmAndDelete(id: number) {
    const adminId = this.authService.currentUser().id;
    if (confirm(id === adminId
      ? 'Are you sure you want to permanently delete your own account?'
      : 'Are you sure you want to delete this user?'
    )) {
      this.removeUser(id);
    }
  }

  private removeUser(id: number) {
    this.userService.deleteUser(id);
  }
}
