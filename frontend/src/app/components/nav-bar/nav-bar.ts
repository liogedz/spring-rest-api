import {Component, computed, inject} from '@angular/core';
import {DarkThemeSelectorService} from '@services/dark-theme-selector-service';
import {AuthService} from '@services/auth-service';
import {MatTooltip} from '@angular/material/tooltip';
import {Role} from '@common/role';
import {Router, RouterLink} from '@angular/router';

@Component({
  selector: 'app-nav-bar',
  imports: [
    MatTooltip,
    RouterLink
  ],
  templateUrl: './nav-bar.html',
  styleUrl: './nav-bar.css',
})
export class NavBar {
  protected readonly Role = Role;
  private authService: AuthService = inject(AuthService);
  isAuthenticated = this.authService.isAuthenticated;

  constructor(
    protected darkThemeSelectorService: DarkThemeSelectorService,
    private router: Router
  ) {
  }

  currentUser = computed(() =>
    this.authService.currentUser()
  );

  protected logout() {
    this.authService.logout();
  }

  protected manageUsers() {
    this.router.navigate(['/users'])
  }
}
