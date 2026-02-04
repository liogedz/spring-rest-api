import {Component, computed} from '@angular/core';
import {DarkThemeSelectorService} from '@services/dark-theme-selector-service';
import {AuthService} from '@services/auth-service';
import {MatTooltip} from '@angular/material/tooltip';
import {Role} from '@common/role';


@Component({
  selector: 'app-nav-bar',
  imports: [
    MatTooltip
  ],
  templateUrl: './nav-bar.html',
  styleUrl: './nav-bar.css',
})
export class NavBar {
  constructor(
    protected darkThemeSelectorService: DarkThemeSelectorService,
    private authService: AuthService
  ) {
  }

  userRole = computed(() =>
    this.authService.userRole()
  );

  protected logout() {
    this.authService.logout();
  }

  protected readonly Role = Role;

  protected manageUsers() {

  }
}
