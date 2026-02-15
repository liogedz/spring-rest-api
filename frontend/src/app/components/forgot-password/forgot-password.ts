import {Component, computed, signal} from '@angular/core';
import {email, form, FormField, required} from '@angular/forms/signals';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Router} from '@angular/router';
import {ForgotData} from '@common/forgot-data';

@Component({
  selector: 'app-forgot-password',
  imports: [
    FormField
  ],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.css',
})
export class ForgotPassword {

  constructor(
    private snackBar: MatSnackBar,
    private router: Router
  ) {
  }

  forgotModel = signal<ForgotData>({
    email: ''
  })

  forgotForm = form(this.forgotModel, (fieldPath) => {
    required(fieldPath.email, {message: 'Email is required'});
    email(fieldPath.email, {message: 'Enter a valid email address'});
  })

  forgotSubmitDisabled = computed(() =>
    this.forgotForm.email().invalid() && this.forgotForm.email().touched());

  protected onSubmit(event: SubmitEvent) {
    event.preventDefault()
  }
}
