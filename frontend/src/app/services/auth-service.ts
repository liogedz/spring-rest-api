import {Injectable, signal} from '@angular/core';
import {ENVIRONMENT} from '@common/environment';
import {HttpClient} from '@angular/common/http';
import {RegData} from '@common/reg-data';
import {ApiResponse} from '@common/api-response';
import {Router} from '@angular/router';
import {LoginData} from '@common/login-data';
import {VerifyData} from '@common/verify-data';
import {Role} from '@common/role';
import {ProfileData} from '@common/profile-data';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = `${ENVIRONMENT.apiUrl}/auth`;

  private _isAuthenticated = signal<boolean>(false);
  isAuthenticated = this._isAuthenticated.asReadonly();

  currentUser = signal<ProfileData>({
    id: 0,
    name: '',
    email: "",
    password: '',
    confirm_password: '',
    role: Role.USER
  });

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    const token = localStorage.getItem('authToken');
    this._isAuthenticated.set(!!token);
  }

  logout(): void {
    localStorage.clear();
    this._isAuthenticated.set(false);
    this.currentUser.set({

      id: 0,
      name: '',
      email: "",
      password: '',
      confirm_password: '',
      role: Role.USER

    });
    this.router.navigate(["/login"]);
  }

  registerUser(regData: RegData) {
    return this.http.post<ApiResponse<any>>(`${this.apiUrl}/signup`, regData);
  }

  login(loginData: LoginData) {
    return this.http.post<ApiResponse<any>>(`${this.apiUrl}/login`, loginData);
  }

  verify2FA(verifyData: VerifyData) {
    console.log()
    return this.http.post<ApiResponse<any>>(`${this.apiUrl}/verify`, verifyData);
  }

  completeLogin(user: any): void {
    const token: string = user.authToken ? user.authToken : '';
    if (token) {
      localStorage.setItem('authToken', token);
    }
    this.currentUser.set(user);
    this._isAuthenticated.set(true);
    localStorage.setItem('currentUser', JSON.stringify(user));
    this.router.navigate(['/home']);
  }
}
