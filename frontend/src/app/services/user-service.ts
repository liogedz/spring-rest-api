import {Injectable} from '@angular/core';
import {ENVIRONMENT} from "@common/environment";
import {HttpClient} from '@angular/common/http';
import {ProfileData} from '@common/profile-data';
import {Role} from '@common/role';


@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = `${ENVIRONMENT.apiUrl}/users`

  constructor(private http: HttpClient) {
  }

  getCurrentUser(): ProfileData {
    const userStr = localStorage.getItem("currentUser");
    return userStr ? JSON.parse(userStr) : {
      id: 0,
      name: "",
      email: "",
      role: Role.USER,
      password: "",
      confirm_password: "",
    };
  }


}
