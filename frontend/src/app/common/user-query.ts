import {SortDirection} from '@angular/material/sort';

export interface UserQuery {
  page: number;
  size: number;
  search: string;
  sortBy: string;
  sortDir: SortDirection;
}
