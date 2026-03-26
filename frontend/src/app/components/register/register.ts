import {Component, computed, signal} from '@angular/core';
import {Role} from '@common/role';
import {form, FormField, required, email, minLength, maxLength, validate, FormRoot} from '@angular/forms/signals';
import {RegData} from '@common/reg-data';
import {AuthService} from '@services/auth-service';
import {Router} from '@angular/router';
import {MatSnackBar} from '@angular/material/snack-bar';
import {firstValueFrom} from 'rxjs';

@Component({
  selector: 'app-register',
  imports: [FormField, FormRoot],
  templateUrl: './register.html',
  styleUrl: './register.css',
})

export class Register {
  userRoles: string[] = Object.values(Role);

  constructor(
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
  }

  regModel = signal<RegData>({
    name: "",
    email: "",
    password: "",
    confirm_password: "",
    role: Role.USER,
    remember: false
  });

  regForm = form(this.regModel, (fieldPath) => {
      required(fieldPath.name, {message: 'Name is required'});
      minLength(fieldPath.name, 3, {message: 'Minimum 3 characters'});
      required(fieldPath.email, {message: 'e-mail is required'});
      email(fieldPath.email, {message: 'Enter a valid email address'});
      required(fieldPath.role, {message: 'Role is required'});
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
          if (this.hasRegFormErrors()) return;
          const value = f().value();
          try {
            const response = await firstValueFrom(this.authService.registerUser(value));
            const {data, message} = response;
            this.snackBar.open(
              `Congratulations ${data.name}! ${message}`,
              'ok',
              {
                duration: 3000,
                panelClass: ['success-snackbar']
              });
            this.router.navigate(["/login"]);
          } catch (err: any) {
            this.snackBar.open(
              err.error.error,
              'Close',
              {
                duration: 3000,
                panelClass: ['error-snackbar']
              });
          }
        }
      }
    }
  );

  hasRegFormErrors = computed(() =>
    this.regForm.name().invalid() ||
    this.regForm.email().invalid() ||
    this.regForm.password().invalid() ||
    this.regForm.role().invalid() ||
    this.regForm.confirm_password().invalid()
  );

  regFormTouched = computed(() =>
    this.regForm.name().touched() ||
    this.regForm.email().touched() ||
    this.regForm.password().touched() ||
    this.regForm.role().touched() ||
    this.regForm.confirm_password().touched()
  );

  isRegDisabled = computed(() =>
    this.hasRegFormErrors() && this.regFormTouched()
  );
}
