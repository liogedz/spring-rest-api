import {computed, Injectable, signal} from '@angular/core';
import {ENVIRONMENT} from "@common/environment";
import {HttpClient, HttpParams} from '@angular/common/http';
import {ProfileData} from '@common/profile-data';
import {ApiResponse} from '@common/api-response';
import {PagedResponse} from '@common/paged-response';
import {UserQuery} from '@common/user-query';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = `${ENVIRONMENT.apiUrl}/user`

  users = computed(() => this.pagedUsers()?.content ?? []);
  pagedUsers = signal<PagedResponse<ProfileData> | null>(null);

  constructor(
    private http: HttpClient,
  ) {
  }

  getAllUsers(query: UserQuery) {
    const params = new HttpParams()
      .set('page', query.page)
      .set('size', query.size)
      .set('search', query.search)
      .set('sortBy', query.sortBy)
      .set('sortDir', query.sortDir);

    return this.http.get<ApiResponse<PagedResponse<ProfileData>>>(
      `${this.apiUrl}s`,
      {params});
  }

  seedUsers() {
    return this.http.post <ApiResponse<string>>(`${this.apiUrl}s/seed`, {});
  }

  getUserById(id: number) {
    return this.http.get<ApiResponse<ProfileData>>(`${this.apiUrl}/${id}`)
  }

  setUsers(data: PagedResponse<ProfileData>) {
    this.pagedUsers.set(data);
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
