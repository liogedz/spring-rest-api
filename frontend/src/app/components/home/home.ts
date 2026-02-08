import {Component, computed, inject} from '@angular/core';
import {AuthService} from '@services/auth-service';

@Component({
  selector: 'app-home',
  imports: [],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  private authService: AuthService = inject(AuthService);
  currentUser = computed(() =>
    this.authService.currentUser()
  );

}
