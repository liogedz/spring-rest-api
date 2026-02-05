import {Component, computed} from '@angular/core';
import {AuthService} from '@services/auth-service';

@Component({
  selector: 'app-home',
  imports: [],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  currentUser = computed(() =>
    this.authService.currentUser()
  );

  constructor(private authService: AuthService) {
  }
}
