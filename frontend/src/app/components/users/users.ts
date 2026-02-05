import {Component, inject} from '@angular/core';
import {UserService} from '@services/user-service';
import {MatTooltip} from '@angular/material/tooltip';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-users',
  imports: [
    MatTooltip,
    RouterLink
  ],
  templateUrl: './users.html',
  styleUrl: './users.css',
})
export class Users {
  private userService = inject(UserService);
  users = this.userService.users;

  protected confirmAndDelete() {
  }


}
