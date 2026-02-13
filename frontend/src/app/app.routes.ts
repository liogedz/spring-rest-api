import {Routes} from '@angular/router';
import {unidentifiedGuard} from './guards/unidentified-guard';
import {Login} from '@components/login/login';
import {Register} from '@components/register/register';
import {Profile} from '@components/profile/profile';
import {authGuard} from './guards/auth-guard';
import {Users} from '@components/users/users';
import {Home} from '@components/home/home';
import {Verify} from '@components/verify/verify';
import {SetPassword} from '@components/set-password/set-password';

export const routes: Routes = [
  {path: 'login', component: Login, canActivate: [unidentifiedGuard]},
  {path: 'register', component: Register, canActivate: [unidentifiedGuard]},
  {path: 'verify', component: Verify, canActivate: [unidentifiedGuard]},
  {path: 'home', component: Home, canActivate: [authGuard]},
  {path: 'profile/:id', component: Profile, canActivate: [authGuard]},
  {path: 'set-password', component: SetPassword, canActivate: [authGuard]},
  {path: 'users', component: Users, canActivate: [authGuard]},
  {path: '**', redirectTo: 'login', pathMatch: 'full'}
];
