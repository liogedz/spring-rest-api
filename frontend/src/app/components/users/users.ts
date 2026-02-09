import {Component, inject, OnInit} from '@angular/core';
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
export class Users implements OnInit {

  private userService = inject(UserService);
  private authService: AuthService = inject(AuthService);
  users = this.userService.users;
  currentUser = this.authService.currentUser;

  ngOnInit(): void {
    this.userService.getAllUsers();
  }

  protected confirmAndDelete(id: number) {
    const adminId = this.currentUser()?.id;
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
