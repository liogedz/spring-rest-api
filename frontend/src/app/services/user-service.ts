import {Injectable} from '@angular/core';
import {ENVIRONMENT} from "@common/environment";
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = `${ENVIRONMENT.apiUrl}/users`

  constructor(private http: HttpClient) {
  }
}
