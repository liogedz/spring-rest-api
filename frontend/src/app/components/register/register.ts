import {Component, computed, signal} from '@angular/core';
import {Role} from '@common/role';
import {form, FormField, required, email, minLength, maxLength, validate} from '@angular/forms/signals';
import {FormsModule} from '@angular/forms';
import {RegData} from '@common/reg-data';
import {AuthService} from '@services/auth-service';
import {HttpErrorResponse} from '@angular/common/http';
import {Router} from '@angular/router';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-register',
  imports: [FormField, FormsModule],
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
    required(fieldPath.name, {message: 'name is required'});
    minLength(fieldPath.name, 3, {message: 'minimum 3 characters'});
    required(fieldPath.email, {message: 'e-mail is required'});
    email(fieldPath.email, {message: 'enter a valid email address'});
    required(fieldPath.role, {message: 'role is required'});
    required(fieldPath.password, {message: 'password is required'});
    required(fieldPath.confirm_password, {message: 'confirm password'});
    minLength(fieldPath.password, 8, {message: 'must be at least 8 characters'});
    maxLength(fieldPath.password, 100, {message: 'password is too long'})
    validate(fieldPath.confirm_password, ({value, valueOf}) => {
      const confirmPassword = value();
      const password = valueOf(fieldPath.password);
      if (confirmPassword !== password) {
        return {
          kind: 'passwordMismatch',
          message: 'passwords do not match',
        };
      }
      return null;
    });
  });

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

  onSubmit(event: Event) {
    event.preventDefault();
    if (this.hasRegFormErrors()) return;

    this.authService.registerUser(this.regModel())
      .subscribe({
        next: (response) => {
          const {data, message} = response;
          this.snackBar.open(
            `Congratulations ${data.name}! ${message}`,
            'ok',
            {
              duration: 3000,
              panelClass: ['success-snackbar']
            });
          this.router.navigate(["/login"]);
        },
        error: (error: HttpErrorResponse) => {
          const errorMsg = error.status === 409
            ? "Username already exist!"
            : "User registration failed!";
          this.snackBar.open(
            errorMsg,
            'Close',
            {
              duration: 3000,
              panelClass: ['error-snackbar']
            });
        }
      });
  }
}
