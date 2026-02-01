import {Injectable} from '@angular/core';
import {ENVIRONMENT} from '@common/environment';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {RegData} from '@common/reg-data';
import {ApiResponse} from '@common/api-response';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Router} from '@angular/router';
import {LoginData} from '@common/login-data';
import {User} from '@common/user';
import {VerifyData} from '@common/verify-data';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = `${ENVIRONMENT.apiUrl}/auth`;

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
  }

  registerUser(regData: RegData) {
    return this.http.post<ApiResponse>(`${this.apiUrl}/signup`, regData);
  }

  login(loginData: LoginData) {
    return this.http.post<ApiResponse>(`${this.apiUrl}/login`, loginData);
  }

  verify2FA(verifyData: VerifyData) {
    console.log()
    return this.http.post<ApiResponse>(`${this.apiUrl}/verify`, verifyData);
  }

  completeLogin(user: { authToken: any; }): void {
    const token = user.authToken ? `Bearer ${user.authToken}` : '';
    if (token) {
      localStorage.setItem('authToken', token);
    }
    localStorage.setItem('currentUser', JSON.stringify(user));
    this.router.navigate(['/profile']);
  }

}
