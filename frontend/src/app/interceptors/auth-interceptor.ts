import {inject, Injectable} from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpErrorResponse
} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {Router} from '@angular/router';
import {PUBLIC_END_POINTS} from '@common/publicEndPoints';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private router = inject(Router);

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const authToken = localStorage.getItem('authToken');


    if (PUBLIC_END_POINTS.some(e => req.url.includes(e))) {
      return next.handle(req).pipe(
        catchError((error: HttpErrorResponse) => this.handle401(error))
      );
    }

    let authReq = req;
    if (authToken) {
      authReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${authToken}`
        }
      });
    }

    return next.handle(authReq).pipe(
      catchError((error: HttpErrorResponse) => this.handle401(error))
    );
  }

  private handle401(error: HttpErrorResponse) {
    if (error.status === 401) {
      localStorage.clear();
      this.router.navigate(['/login']);
    }
    return throwError(() => error);
  }
}
