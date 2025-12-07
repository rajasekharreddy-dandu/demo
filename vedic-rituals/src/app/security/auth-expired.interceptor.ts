// src/app/security/auth-expired.interceptor.ts

import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpResponse,
  HttpErrorResponse,
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap, finalize } from 'rxjs/operators'; // Inferred operators

@Injectable()
export class AuthExpiredInterceptor implements HttpInterceptor {
  loader: any; // Inferred loader service/component

  constructor() {
    // Inferred: this.loader = loaderService;
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Logging/Loader invocation before API call
    this.loader.log('Before making api call loader is invoked');
    this.loader.show();
    this.loader.add();
    this.loader.log('Before making api call: ' + request.url);
    
    return next.handle(request).pipe(
      tap({
        next: (event: HttpEvent<any>) => {
          // Logging the http response to browser's console in case of a success
          if (event instanceof HttpResponse) {
            this.loader.log('api call success: ' + event.url);
          }
        },
        error: (err: any) => {
          // Logging the http response to browser's console in case of a failure
          if (err instanceof HttpErrorResponse) {
             this.loader.log('status: ' + err.status);
             // Inferred: Handle 401/403 errors here
          }
        }
      }),
      finalize(() => {
        // Hiding/removing loader regardless of success or error
        this.checkStatus();
      })
    );
  }

  checkStatus() {
    this.loader.delete();
    var count = this.loader.status(); // Gets current loader count
    if (count == 0 || count < 0) {
      this.loader.hide();
    } else {
      this.loader.show();
    }
  }
}