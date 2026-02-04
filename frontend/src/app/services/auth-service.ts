import {Injectable, signal} from '@angular/core';
import {ENVIRONMENT} from '@common/environment';
import {HttpClient} from '@angular/common/http';
import {RegData} from '@common/reg-data';
import {ApiResponse} from '@common/api-response';
import {Router} from '@angular/router';
import {LoginData} from '@common/login-data';
import {VerifyData} from '@common/verify-data';
import {Role} from '@common/role';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = `${ENVIRONMENT.apiUrl}/auth`;

  userRole = signal(Role.USER);

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
  }

  logout(): void {
    localStorage.clear();
    this.userRole.set(Role.USER);
    this.router.navigate(["/login"]);
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

  completeLogin(user: any): void {
    const token: string = user.authToken ? user.authToken : '';
    const role: Role = user.role ? user.role : user.role.USER
    if (token) {
      localStorage.setItem('authToken', token);
    }
    this.userRole.set(role);
    localStorage.setItem('currentUser', JSON.stringify(user));
    this.router.navigate(['/profile']);
  }

}
