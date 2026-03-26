import {Component, computed, inject} from '@angular/core';
import {DarkThemeSelectorService} from '@services/dark-theme-selector-service';
import {AuthService} from '@services/auth-service';
import {Role} from '@common/role';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-nav-bar',
  imports: [
    RouterLink
  ],
  templateUrl: './nav-bar.html',
  styleUrl: './nav-bar.css',
  standalone: true
})
export class NavBar {
  protected readonly Role = Role;
  private authService: AuthService = inject(AuthService);
  isAuthenticated = this.authService.isAuthenticated;

  constructor(
    protected darkThemeSelectorService: DarkThemeSelectorService,
  ) {
  }

  currentUser = computed(() =>
    this.authService.currentUser()
  );

  protected logout() {
    this.authService.logout();
  }
}
