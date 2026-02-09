import {Injectable, signal} from '@angular/core';
import {ENVIRONMENT} from "@common/environment";
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {ProfileData} from '@common/profile-data';
import {ApiResponse} from '@common/api-response';
import {MatSnackBar} from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = `${ENVIRONMENT.apiUrl}/user`

  private usersSignal = signal<ProfileData[]>([]);
  users = this.usersSignal.asReadonly();


  constructor(
    private http: HttpClient,
    private snackBar: MatSnackBar
  ) {
  }

  getAllUsers() {
    this.http.get<ApiResponse>(`${this.apiUrl}s`).subscribe({
      next: (response) =>
        this.usersSignal.set(response.data),
      error: (err: HttpErrorResponse) => {
        this.snackBar.open(
          err.message,
          'close',
          {
            duration: 3000,
            panelClass: ['error-snackbar']
          });
      }
    });
  }

  getUserById(id: number) {
    return this.http.get<ApiResponse>(`${this.apiUrl}/${id}`)
  }

  patchUser(profileData: ProfileData) {
    return this.http.patch<ApiResponse>(`${this.apiUrl}/${profileData.id}`, profileData);
  }

  getUserSnapshot(id: number) {
    return this.users().find(u => u.id === id);
  }

  deleteUser(id: number) {
    return this.http.delete<ApiResponse>(`${this.apiUrl}/${id}`);
  }
}
