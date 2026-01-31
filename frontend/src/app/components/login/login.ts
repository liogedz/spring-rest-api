import {Component, computed, signal} from '@angular/core';
import {LoginData} from '@common/login-data';
import {form, FormField, required} from '@angular/forms/signals';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-login',
  imports: [FormField, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {

  loginModel = signal<LoginData>({
    identifier: "",
    password: ""
  });

  loginForm = form(this.loginModel, (fieldPath) => {
    required(fieldPath.identifier, {message: 'identifier is required'});
    required(fieldPath.password, {message: 'password is required'});
  });

  onSubmit() {
    const loginData = this.loginModel();
    if (this.hasLoginErrors()) return;
    console.log(loginData);
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
