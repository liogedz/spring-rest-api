import {Injectable, signal, effect} from '@angular/core';
import {ENVIRONMENT} from '@common/environment';
import {HttpClient} from '@angular/common/http';
import {RegData} from '@common/reg-data';
import {ApiResponse} from '@common/api-response';
import {Router} from '@angular/router';
import {LoginData} from '@common/login-data';
import {ProfileData} from '@common/profile-data';
import {VerifyData} from '@common/verify-data';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = `${ENVIRONMENT.apiUrl}/auth`;

  private _isAuthenticated = signal<boolean>(false);
  isAuthenticated = this._isAuthenticated.asReadonly();
  currentUser = signal<ProfileData | null>(null);

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    // Rehydrate
    const token = localStorage.getItem('authToken');
    const storedUser = localStorage.getItem('currentUser');

    if (token && storedUser) {
      this._isAuthenticated.set(true);
      this.currentUser.set(JSON.parse(storedUser));
    }
    // Persist reactively
    effect(() => {
      const user = this.currentUser();
      if (user) {
        localStorage.setItem('currentUser', JSON.stringify(user));
      } else {
        localStorage.removeItem('currentUser');
      }
    });
  }


  logout(): void {
    localStorage.clear();
    this._isAuthenticated.set(false);
    this.currentUser.set(null);

    this.router.navigate(["/login"]);
  }

  registerUser(regData: RegData) {
    return this.http.post<ApiResponse>(`${this.apiUrl}/signup`, regData);
  }

  login(loginData: LoginData) {
    return this.http.post<ApiResponse>(`${this.apiUrl}/login`, loginData);
  }

  verify2FA(verifyData: VerifyData) {
    return this.http.post<ApiResponse>(`${this.apiUrl}/verify`, verifyData);
  }

  completeLogin(user: ProfileData & { authToken: string }): void {
    localStorage.setItem('authToken', user.authToken);
    localStorage.setItem('currentUser', JSON.stringify(user));

    this.currentUser.set(user);
    this._isAuthenticated.set(true);

    this.router.navigate(['/home']);
  }
}
