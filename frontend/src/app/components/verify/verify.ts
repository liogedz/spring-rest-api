import {Component, computed, signal} from '@angular/core';
import {form, FormField, readonly, required} from '@angular/forms/signals';
import {VerifyData} from '@common/verify-data';
import {AuthService} from '@services/auth-service';
import {ActivatedRoute} from '@angular/router';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-verify',
  imports: [
    FormField
  ],
  templateUrl: './verify.html',
  styleUrl: './verify.css',
})
export class Verify {
  verifyModel = signal<VerifyData>({
    identifier: '',
    code: ''
  });

  verifyForm = form(this.verifyModel, (fieldPath) => {
    readonly(fieldPath.identifier);
    required(fieldPath.code, {message: 'Code is required'});
  });

  constructor(
    private authService: AuthService,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar
  ) {
    this.route.queryParams.subscribe(params => {
      if (params['identifier']) {
        this.verifyForm.identifier().value.set(params['identifier']);
      }
    });
  }

  onTwoFASubmit(event: Event) {
    event.preventDefault();
    if (this.isVerifyDisabled()) return;

    this.authService.verify2FA(this.verifyModel())
      .subscribe({
        next: (response) =>
          this.authService.completeLogin(response.data),
        error: () =>
          this.snackBar.open('Invalid verification code', 'close', {
            duration: 3000
          })
      });
  }

  isVerifyDisabled = computed(() =>
    this.verifyForm.code().invalid() && this.verifyForm.code().touched()
  );
}
