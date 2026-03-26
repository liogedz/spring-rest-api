import {Component, computed, signal} from '@angular/core';
import {LoginData} from '@common/login-data';
import {form, FormField, FormRoot, required} from '@angular/forms/signals';
import {AuthService} from '@services/auth-service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Router, RouterLink} from '@angular/router';
import {firstValueFrom} from 'rxjs';

@Component({
  selector: 'app-login',
  imports: [FormField, RouterLink, FormRoot],
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
    },
    {
      submission: {
        action: async f => {
          if (this.hasLoginErrors()) return;
          const value = f().value();

          try {
            const response = await firstValueFrom(
              this.authService.login(value)
            );
            this.router.navigate(['/verify'], {
              queryParams: {
                identifier: value.identifier
              }
            });
            this.snackBar.open(
              response.message,
              'ok',
              {
                duration: 4000,
                panelClass: ['success-snackbar']
              }
            );
          } catch (err: any) {
            this.snackBar.open(
              err.error.error,
              'close',
              {
                duration: 3000,
                panelClass: ['error-snackbar']
              }
            );
          }
        }
      }
    });

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
