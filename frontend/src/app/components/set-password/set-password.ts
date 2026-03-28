import {Component, computed, inject, signal} from '@angular/core';
import {form, FormField, FormRoot, maxLength, minLength, required, validate} from '@angular/forms/signals';
import {PasswordData} from '@common/password-data';
import {AuthService} from '@services/auth-service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Router} from '@angular/router';
import {firstValueFrom} from 'rxjs';

@Component({
  selector: 'app-set-password',
  imports: [
    FormField,
    FormRoot
  ],
  templateUrl: './set-password.html',
  styleUrl: './set-password.css',
})
export class SetPassword {
  private authService: AuthService = inject(AuthService);

  constructor(
    private snackBar: MatSnackBar,
    private router: Router
  ) {
  }

  passwordModel = signal<PasswordData>({
    password: '',
    confirm_password: ''
  });

  passwordForm = form(this.passwordModel, (fieldPath) => {
      required(fieldPath.password, {message: 'Password is required'});
      required(fieldPath.confirm_password, {message: 'Confirm password is required'});
      minLength(fieldPath.password, 8, {message: 'Must be at least 8 characters'});
      maxLength(fieldPath.confirm_password, 100, {message: 'Password is too long'});
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
            const response = await firstValueFrom(this.authService.savePassword(value));
            this.snackBar.open(
              response.message,
              'ok',
              {
                duration: 3000,
                panelClass: ['success-snackbar']
              });
            this.setConfirmed();
            this.router.navigate(['/home']);
          } catch (err: any) {
            this.snackBar.open(
              err.error.message,
              'Close',
              {
                duration: 0,
                panelClass: ['error-snackbar']
              });
          }
        }
      }
    }
  );

  hasSetPasswordErrors = computed(() =>
    this.passwordForm.password().invalid() ||
    this.passwordForm.confirm_password().invalid());

  passwordFormTouched = computed(() =>
    this.passwordForm.password().touched() ||
    this.passwordForm.confirm_password().touched());

  passwordSubmitDisabled = computed(() =>
    this.hasSetPasswordErrors() && this.passwordFormTouched());

  setConfirmed() {
    this.authService.currentUser.update(user => {
      if (user) {
        return {...user, confirmed: true};
      }
      return null;
    });
  }
}
