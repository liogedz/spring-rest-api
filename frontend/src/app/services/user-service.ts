import {computed, Injectable, signal} from '@angular/core';
import {ENVIRONMENT} from "@common/environment";
import {HttpClient, HttpParams} from '@angular/common/http';
import {ProfileData} from '@common/profile-data';
import {ApiResponse} from '@common/api-response';
import {MatSnackBar} from '@angular/material/snack-bar';
import {PagedResponse} from '@common/paged-response';
import {UserQuery} from '@common/user-query';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = `${ENVIRONMENT.apiUrl}/user`

  loading = signal(false);
  users = computed(() => this.pagedUsers()?.content ?? []);
  pagedUsers = signal<PagedResponse<ProfileData> | null>(null);

  constructor(
    private http: HttpClient,
    private snackBar: MatSnackBar
  ) {
  }

  getAllUsers(query: UserQuery): void {
    this.loading.set(true);
    const params = new HttpParams()
      .set('page', query.page)
      .set('size', query.size)
      .set('search', query.search)
      .set('sortBy', query.sortBy)
      .set('sortDir', query.sortDir);

    this.http.get<ApiResponse<PagedResponse<ProfileData>>>(`${this.apiUrl}s`, {params})
      .subscribe({
        next: (response) =>
          this.pagedUsers.set(response.data),
        error: (err: any) => {
          this.snackBar.open(
            err.error.message,
            'close',
            {
              duration: 0,
              panelClass: ['error-snackbar']
            });
        }
      });
  }

  seedUsers() {
    return this.http.post <ApiResponse<string>>(`${this.apiUrl}s/seed`, {});
  }

  getUserById(id: number) {
    return this.http.get<ApiResponse<ProfileData>>(`${this.apiUrl}/${id}`)
  }

  patchUser(profileData: ProfileData) {
    return this.http.patch<ApiResponse<ProfileData>>(`${this.apiUrl}/${profileData.id}`, profileData);
  }

  getUserSnapshot(id: number) {
    return this.users().find(u => u.id === id);
  }

  deleteUser(id: number) {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`);
  }
}
