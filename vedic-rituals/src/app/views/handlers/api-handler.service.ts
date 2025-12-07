// import { AlertService } from '../alert/alert.service';
import { Injectable } from '@angular/core';
import { catchError, map, tap, finalize } from 'rxjs/operators';
import { HttpClient, HttpHeaders, HttpAgent } from '@angular/common/http';
import { Observable } from 'rxjs';
import 'rxjs/Rx'; // Deprecated import style, preserved from image
// import * as errorMessage from '../common/constants/constant'; // Relative path guessed
// import { ConsoleService } from './../services/console.service'; // Relative path guessed
// import { SessionTimerService } from 'session-expiration-service'; // Name guessed from usage
import { HttpAgent } from 'agentkeepalive'; // Name guessed from usage
import { environment } from './../environments/environment';

const getLoginStatusUrl = 'getloginUserDetails';
const keepaliveAgent = new HttpAgent({
    keepAlive: true,
    maxSockets: Infinity,
    maxFreeSockets: 256,
    timeout: 600000, // active socket keepalive for 50 seconds
    freeSocketTimeout: 300000, // free socket keepalive for 30 seconds
});

const httpOptions = {
    agent: keepaliveAgent,
    headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Strict-Transport-Security': 'max-age=31536000; includeSubDomains; preload',
        'X-XSS-Protection': '1; mode=block',
        'X-Frame-Options': 'SAMEORIGIN',
        'Content-Security-Policy': `script-src 'self' + environment.allowedOrigins.join(',')`,
        'X-Content-Type-Options': 'nosniff',
        'Cache-Control': 'no-cache, no-store, max-age=0, must-revalidate',
        'Access-Control-Allow-Origin': environment.allowedOrigins.join(','),
        'Access-Control-Allow-Methods': 'POST',
        'Access-Control-Allow-Headers': 'Content-Type'
    }),
};

@Injectable({
    providedIn: 'root'
})
export class ApiHandlerService {

    protected headers: HttpHeaders;
    constructor(
        private http: HttpClient,
        private printOut: ConsoleService,
        private alertService: AlertService,
        private sessionTimerService: SessionTimerService
    ) { }

    get(url: string): Observable<any> {
        this.alertService.clear();
        return this.http.get(url)
            .pipe(
                tap((_ => this.printOut.log('fetched data'))),
                catchError(this.handleError('get')),
                finalize(() => this.onEnd())
            );
    }

    post(url: string, inputParam: any): Observable<any> {
        if (url.indexOf(getLoginStatusUrl) < 0) {
            this.alertService.clear();
        }
        this.sessionTimerService.resetTimer();
        return this.http.post(url, JSON.stringify(inputParam), httpOptions)
            .pipe(
                tap((_ => this.printOut.log('posted data'))),
                catchError(this.handleError('post')),
                finalize(() => this.onEnd())
            );
    }

    private onEnd(): void {
        // empty block in image
    }

    /**
     * Handle Http operation that failed.
     * Let the app continue.
     * @param operation - name of the operation that failed
     * @param result - optional value to return as the observable result
     */
    private handleError<T>(operation = 'operation', result?: T) {
        return (error: any): Observable<T> => {
            console.error(error); // log to console instead

            // TODO: better job of transforming error for user consumption

            if (error.status === 0) {
                error.message = errorMessage.sessionCrashed;
            }

            if (error.status === 401) {
                error.message = errorMessage.unauthorizedAccess;
            }

            if (error.status === 440) {
                error.message = errorMessage.sessionExpired;
            }

            this.printOut.log(`${operation} failed: ${error.message}`);
            this.alertService.error('Server error occurred. Please contact your administrator');

            // Let the app keep running by returning an empty result.
            return Observable.throw(error);
        };
    }
}