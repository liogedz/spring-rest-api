import {Component, computed, effect, inject, signal} from '@angular/core';
import {UserService} from '@services/user-service';
import {RouterLink} from '@angular/router';
import {AuthService} from '@services/auth-service';
import {UserQuery} from '@common/user-query';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-users',
  imports: [
    RouterLink
  ],
  templateUrl: './users.html',
  styleUrl: './users.css',
})
export class Users {

  private userService = inject(UserService);
  private authService: AuthService = inject(AuthService);
  private snackBar = inject(MatSnackBar);
  users = this.userService.users;
  currentUser = this.authService.currentUser;
  paged = this.userService.pagedUsers;
  seeding = signal(false);

  query = signal<UserQuery>({
    page: 0,
    size: 10,
    search: '',
    sortBy: 'id',
    sortDir: 'asc',
  });

  pages = computed(() =>
    Array.from({length: this.paged()?.totalPages ?? 0}, (_, i) => i)
  );

  private loadUsers(query: UserQuery) {
    this.userService.getAllUsers(query)
      .subscribe({
        next: response => {
          this.userService.setUsers(response.data);
        },
        error: (err: any) => {
          this.showError(err.error.message);
        }
      });
  }

  private _ = effect(() => {
    const query = this.query();
    this.loadUsers(query)
  });

  goToPage(page: number): void {
    this.query.update(q => ({...q, page}));
  }

  onSearch(event: Event): void {
    const search = (event.target as HTMLInputElement).value;
    this.query.update(q => ({...q, search, page: 0}));
  }

  onSizeChange(event: Event): void {
    const size = +(event.target as HTMLSelectElement).value;
    this.query.update(q => ({...q, size, page: 0}));
  }

  onSort(sortBy: string): void {
    this.query.update(q => ({
      ...q,
      sortBy,
      sortDir: q.sortBy === sortBy && q.sortDir === 'asc' ? 'desc' : 'asc',
      page: 0,
    }));
  }

  sortIcon(col: string): string {
    const q = this.query();
    if (q.sortBy !== col) return '↕';
    return q.sortDir === 'asc' ? '↑' : '↓';
  }

  protected confirmAndDelete(id: number) {
    const adminId = this.currentUser()?.id;
    if (confirm(id === adminId
      ? 'Are you sure you want to permanently delete your own account?'
      : 'Are you sure you want to delete this user?'
    )) {
      this.removeUser(id);
    }
  }

  private removeUser(id: number) {
    this.userService.deleteUser(id)
      .subscribe({
        next: (response) => {
          this.snackBar.open(
            response.message,
            'ok',
            {
              duration: 3000,
              panelClass: ['success-snackbar']
            });
          if (id === this.currentUser()?.id) {
            this.authService.logout()
            return
          }
          this.loadUsers(this.query());
        },
        error: (err: any) => {
          this.showError(err.error.message);
        }
      });
  }

  showError(message: string) {
    this.snackBar.open(message, 'Close', {
      duration: 0,
      panelClass: ['error-snackbar']
    });
  }

  protected seedDatabase() {
    this.seeding.set(true);
    this.userService.seedUsers().subscribe({

      next: () => {
        this.seeding.set(false);
        this.query.update(q => ({...q, page: 0}))
      },
      error: (err: any) => {
        this.seeding.set(false);
        this.snackBar.open(
          err.error.message,
          'close',
          {
            duration: 0,
            panelClass: ['error-snackbar']
          });
      }
    })
  }
}
