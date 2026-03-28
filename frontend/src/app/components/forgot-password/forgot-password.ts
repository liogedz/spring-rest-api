import {Component, computed, signal} from '@angular/core';
import {
  email,
  form,
  FormField, FormRoot,
  maxLength,
  minLength,
  readonly,
  required,
  validate
} from '@angular/forms/signals';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ActivatedRoute, Router} from '@angular/router';
import {ForgotData} from '@common/forgot-data';
import {ResetPasswordData} from '@common/reset-password-data';
import {AuthService} from '@services/auth-service';
import {firstValueFrom} from 'rxjs';

@Component({
  selector: 'app-forgot-password',
  imports: [
    FormField,
    FormRoot
  ],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.css',
})
export class ForgotPassword {
  resetPasswordActive = signal<boolean>(false);
  waitForEmailLink = signal<boolean>(false);

  constructor(
    private snackBar: MatSnackBar,
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {
    this.route.queryParams.subscribe(params => {
      if (params['token']) {
        this.validateToken(params['token']);
      }
    });
  }

  forgotModel = signal<ForgotData>({
    email: ''
  });

  forgotForm = form(this.forgotModel, (fieldPath) => {
      required(fieldPath.email, {message: 'Email is required'});
      email(fieldPath.email, {message: 'Enter a valid email address'});
    },
    {
      submission: {
        action: async f => {
          const value = f().value();
          try {
            await firstValueFrom(this.authService.forgotPassword(value));
            this.waitForEmailLink.set(true);
            this.snackBar.open(
              'Link is sent to entered e-mail address, please follow to continue ...',
              'ok',
              {
                duration: 0,
                panelClass: ['snackbar-success']
              });

          } catch (err: any) {
            this.showError(err.error.message);
          }
        }
      }
    }
  );

  forgotSubmitDisabled = computed(() =>
    this.forgotForm.email().invalid() && this.forgotForm.email().touched());

  resetPasswordModel = signal<ResetPasswordData>({
    token: '',
    password: '',
    confirm_password: ''
  });

  resetPasswordForm = form(this.resetPasswordModel, (fieldPath) => {
      readonly(fieldPath.token);
      minLength(fieldPath.password, 8, {message: 'Must be at least 8 characters'});
      maxLength(fieldPath.password, 100, {message: 'Password is too long'})
      required(fieldPath.password, {message: 'Password is required'});
      required(fieldPath.confirm_password, {message: 'Confirm password is required'});
      validate(fieldPath.confirm_password, ({value, valueOf}) => {
        const confirmPassword = value();
        const password = valueOf(fieldPath.password);
        if (confirmPassword !== password) {
          return {
            kind: 'passwordMismatch',
            message: 'Passwords do not match',
          };
        }
        return null;
      });
    },
    {
      submission: {
        action: async f => {
          const value = f().value();
          try {
            await firstValueFrom(this.authService.resetPassword(value));
            this.snackBar.open(
              'Password is re-set, please login',
              'ok',
              {
                duration: 3000,
                panelClass: ['success-snackbar']
              }
            );
          } catch (err: any) {
            this.showError(err.error.message);
          } finally {
            this.router.navigate(['/login']);
          }
        }
      }
    }
  );

  hasResetPasswordErrors = computed(() =>
    this.resetPasswordForm.password().invalid() ||
    this.resetPasswordForm.confirm_password().invalid());

  passwordResetFormTouched = computed(() =>
    this.resetPasswordForm.password().touched() ||
    this.resetPasswordForm.confirm_password().touched());

  passwordSubmitDisabled = computed(() =>
    this.hasResetPasswordErrors() && this.passwordResetFormTouched());

  validateToken(token: string) {
    this.authService.validateToken(token)
      .subscribe({
        next: () => {
          this.resetPasswordActive.set(true);
          this.resetPasswordForm.token().value.set(token);
        },
        error: (err: any) => {
          this.showError(err.error.message);
          this.router.navigate(['/login']);
        }
      });
  }

  showError(message: string) {
    this.snackBar.open(message, 'Close', {
      duration: 0,
      panelClass: ['error-snackbar']
    });
  }
}
