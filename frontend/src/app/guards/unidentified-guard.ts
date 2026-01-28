import { CanActivateFn } from '@angular/router';

export const unidentifiedGuard: CanActivateFn = (route, state) => {
  return true;
};
