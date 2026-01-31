import {Routes} from '@angular/router';
import {unidentifiedGuard} from './guards/unidentified-guard';
import {Login} from '@components/login/login';
import {Register} from '@components/register/register';
import {Profile} from '@components/profile/profile';
import {authGuard} from './guards/auth-guard';

export const routes: Routes = [
  {path: 'login', component: Login, canActivate: [unidentifiedGuard]},
  {path: 'register', component: Register, canActivate: [unidentifiedGuard]},
  {path: 'profile', component: Profile, canActivate: [authGuard]},
  {path: '**', redirectTo: 'login', pathMatch: 'full'}
];
