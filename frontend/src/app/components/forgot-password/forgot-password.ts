import {Component, computed, signal} from '@angular/core';
import {
  email,
  form,
  FormField,
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
import {HttpErrorResponse} from '@angular/common/http';
import {finalize} from 'rxjs';

@Component({
  selector: 'app-forgot-password',
  imports: [
    FormField
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
  });

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
  });

  hasResetPasswordErrors = computed(() =>
    this.resetPasswordForm.password().invalid() ||
    this.resetPasswordForm.confirm_password().invalid());

  passwordResetFormTouched = computed(() =>
    this.resetPasswordForm.password().touched() ||
    this.resetPasswordForm.confirm_password().touched());

  passwordSubmitDisabled = computed(() =>
    this.hasResetPasswordErrors() && this.passwordResetFormTouched());

  protected onForgotPasswordSubmit(event: SubmitEvent) {
    event.preventDefault()
    this.authService.forgotPassword(this.forgotModel())
      .subscribe({
        next: () => {
          this.waitForEmailLink.set(true);
          this.snackBar.open(
            'Link is sent to entered e-mail address, please follow to continue ...',
            'ok',
            {
              duration: 4000,
              panelClass: ['snackbar-success']
            });
        },
        error: (error: HttpErrorResponse) => {
          this.snackBar.open(
            error.message,
            'close',
            {
              duration: 3000,
              panelClass: ['error-snackbar']
            });
        }
      });
  }

  validateToken(token: string) {
    this.authService.validateToken(token)
      .subscribe({
        next: () => {
          this.resetPasswordActive.set(true);
          this.resetPasswordForm.token().value.set(token);
        },
        error: () => {
          this.snackBar.open(
            'Something went wrong, try to reset password again',
            'close',
            {
              duration: 4000,
              panelClass: ['error-snackbar']
            });
          this.router.navigate(['/login']);
        }
      });
  }

  protected submitNewPassword(event: SubmitEvent) {
    event.preventDefault();
    this.authService.resetPassword(this.resetPasswordModel())
      .pipe(
        finalize(() => {
          this.router.navigate(['/login']);
        })
      )
      .subscribe({
        next: () => {
          this.snackBar.open(
            'Password is re-set, please login',
            'ok',
            {
              duration: 3000,
              panelClass: ['success-snackbar']
            });
        },
        error: () => {
          this.snackBar.open(
            'Password reset failed, try again',
            'close',
            {
              duration: 3000,
              panelClass: ['error-snackbar']
            });
        }
      });

  }
}
