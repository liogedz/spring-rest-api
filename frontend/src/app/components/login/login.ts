import {Component, computed, signal} from '@angular/core';
import {LoginData} from '@common/login-data';
import {form, FormField, required} from '@angular/forms/signals';
import {AuthService} from '@services/auth-service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Router, RouterLink} from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [FormField, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {

  constructor(
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
  }

  loginModel = signal<LoginData>({
    identifier: '',
    password: ''
  });

  loginForm = form(this.loginModel, (fieldPath) => {
    required(fieldPath.identifier, {message: 'Identifier is required'});
    required(fieldPath.password, {message: 'Password is required'});
  });

  onLoginSubmit(event: Event) {
    event.preventDefault();

    if (this.hasLoginErrors()) return;

    this.authService.login(this.loginModel())
      .subscribe({
        next: (response) => {
          this.router.navigate(['/verify'], {
            queryParams: {
              identifier: this.loginModel().identifier
            }
          });
          this.snackBar.open(
            response.message,
            'ok',
            {
              duration: 4000,
              panelClass: ['success-snackbar']
            });
        },
        error: (err) => {
          let errorMsg = 'Login failed.';
          if (typeof err.error === 'string') {
            errorMsg = err.error;
          } else if (err.error?.message) {
            errorMsg = err.error.message;
          } else if (err.status === 0) {
            errorMsg = 'Server unreachable.';
          }

          this.snackBar.open(
            errorMsg,
            'close', {
              duration: 3000,
              panelClass: ['error-snackbar']
            });
        }
      });
  }

  hasLoginErrors = computed(() =>
    this.loginForm.identifier().invalid() ||
    this.loginForm.password().invalid()
  );

  loginTouched = computed(() =>
    this.loginForm.identifier().touched() ||
    this.loginForm.password().touched()
  );

  isLoginDisabled = computed(() =>
    this.hasLoginErrors() && this.loginTouched()
  );
}
