import {Role} from '@common/role';

export interface ProfileData {
  id: number;
  name: string;
  email: string;
  password: string;
  confirm_password: string;
  role: Role;
  confirmed: boolean;
}
