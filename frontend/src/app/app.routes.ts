import {Routes} from '@angular/router';
import {unidentifiedGuard} from './guards/unidentified-guard';
import {Login} from './components/login/login';
import {Register} from './components/register/register';

export const routes: Routes = [
  {path: 'login', component: Login, canActivate: [unidentifiedGuard]},
  {path: 'register', component: Register, canActivate: [unidentifiedGuard]},
  {path: '**', redirectTo: 'login', pathMatch: 'full'}
];
