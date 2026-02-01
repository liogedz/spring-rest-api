import {Component, computed, signal} from '@angular/core';
import {LoginData} from '@common/login-data';
import {form, FormField, required, readonly} from '@angular/forms/signals';
import {FormsModule} from '@angular/forms';
import {AuthService} from '@services/auth-service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {VerifyData} from '@common/verify-data';

@Component({
  selector: 'app-login',
  imports: [FormField, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  show2FA = signal(false);

  constructor(
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {
  }

  loginModel = signal<LoginData>({
    identifier: '',
    password: ''
  });

  verifyModel = signal<VerifyData>({
    identifier: '',
    code: ''
  })

  loginForm = form(this.loginModel, (fieldPath) => {
    required(fieldPath.identifier, {message: 'identifier is required'});
    required(fieldPath.password, {message: 'password is required'});
  });

  verifyForm = form(this.verifyModel, (fieldPath) => {
    readonly(fieldPath.identifier);
    required(fieldPath.code, {message: 'code is required'});
  })

  onLoginSubmit() {
    if (this.hasLoginErrors()) return;

    this.authService.login(this.loginModel())
      .subscribe({
        next: (response) => {
          this.show2FA.set(true);
          this.verifyForm.identifier().value.set(this.loginForm.identifier().value());
          this.snackBar.open(
            response.message,
            'ok',
            {
              duration: 4000,
              panelClass: ['success-snackbar']
            });
        },
        error: (err) => {
          const errorMsg = err.error.message || 'Login failed.';
          this.snackBar.open(
            errorMsg,
            'close', {
              duration: 3000,
              panelClass: ['error-snackbar']
            });
        }
      });
  }

  onTwoFASubmit() {
    if (this.isVerifyDisabled()) return;

    this.authService.verify2FA(this.verifyModel())
      .subscribe({
        next: (response) => this.authService.completeLogin(response.data),
        error: () => {
          this.snackBar.open(
            'Invalid verification code.',
            'close',
            {duration: 3000, panelClass: ['error-snackbar']}
          )
        }
      })
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

  isVerifyDisabled = computed(() =>
    this.verifyForm.code().invalid() && this.verifyForm.code().touched()
  );
}
