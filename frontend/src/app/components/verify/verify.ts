import {Component, computed, signal} from '@angular/core';
import {form, FormField, FormRoot, readonly, required} from '@angular/forms/signals';
import {VerifyData} from '@common/verify-data';
import {AuthService} from '@services/auth-service';
import {ActivatedRoute} from '@angular/router';
import {MatSnackBar} from '@angular/material/snack-bar';
import {firstValueFrom} from 'rxjs';

@Component({
  selector: 'app-verify',
  imports: [
    FormField,
    FormRoot
  ],
  templateUrl: './verify.html',
  styleUrl: './verify.css',
})
export class Verify {

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

  verifyModel = signal<VerifyData>({
    identifier: '',
    code: ''
  });

  verifyForm = form(this.verifyModel, (fieldPath) => {
      readonly(fieldPath.identifier);
      required(fieldPath.code, {message: 'Code is required'});
    },
    {
      submission: {
        action: async f => {
          if (this.isVerifyDisabled()) return;
          const value = f().value();

          try {
            const response = await firstValueFrom(
              this.authService.verify2FA(value)
            );
            this.authService.completeLogin(response.data)
          } catch (err: any) {
            this.snackBar.open(
              err.error.message,
              'close',
              {
                duration: 0,
                panelClass: ['error-snackbar']
              }
            );
          }
        }
      }
    });

  isVerifyDisabled = computed(() =>
    this.verifyForm.code().invalid() && this.verifyForm.code().touched()
  );
}
