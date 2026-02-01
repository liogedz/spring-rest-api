import {Role} from '@common/role';

export interface RegData {
  name: string;
  email: string;
  password: string;
  confirm_password: string;
  role: Role;
  remember: boolean;
}
