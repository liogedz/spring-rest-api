import {Role} from './role';

export class User {
  id?: number;
  name?: string;
  email?: string;
  password?: string;
  role?: Role;
  authToken?: string;
}
