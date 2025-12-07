// src/app/security/account.service.ts

import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import 'rxjs/Rx'; // Deprecated RxJS import
import { Router } from '@angular/router';

import { environment } from './../environments/environment';
import { ApiHandlerService } from './../views/handlers/api-handler.service'; // Inferred path

@Injectable({
  providedIn: 'root',
})
export class AccountService {
  account: any;
  wmid: any;
  loginStatus: any = 'N'; // 'N' likely means Not logged in
  batchStatus: any;

  constructor(private api: ApiHandlerService, private router: Router) {
    /**
     * sets the status of batch and login
     * @param batchStatus
     * @param loginStatus
     */
  }

  setuserPrelogin(batchStatus: any, loginStatus: any) {
    if (this.account === undefined) {
      this.account = {} as any;
    }
    this.loginStatus = loginStatus;
    this.batchStatus = batchStatus;
    this.account.loginStatus = this.loginStatus;
  }

  /**
   * @param account stores the logged in non sensitive account details
   */
  setAccount(account: any): any {
    this.account = account;
    this.loginStatus = 'Y';
    return this.account;
  }

  /**
   * gets the logged in user information from server
   */
  getUser(): Observable<any> {
    if (this.loginStatus === 'N') {
      if (this.account === undefined) {
        this.account = {} as any;
      }
      this.account.batchStatus = 'N';
      this.account.loginStatus = 'N';
    }
    // API call to fetch user details
    return this.api.post(environment.apiUrl + '/Home/getLoginUserDetails', this.account);
  }

  /**
   * gets the logged in user information from storage service
   */
  getUserInfo(): any {
    return this.account;
  }

  /**
   * gets the logged status alone
   */
  getLoginStatus() {
    return this.loginStatus;
  }

  /**
   * sets the login status true manually
   */
  setLoginStatus() {
    this.loginStatus = 'Y';
  }

  /**
   * gets the batch status from account details
   */
  getBatchStatus() {
    return this.batchStatus;
  }

  /**
   * logout from the application by selecting the respective logout url
   */
  logout() {
    this.account = {} as any; // Clear local state
    // localStorage.clear(); // Clears all local storage data
    var logoutUrl = environment.logOutUrl;
    window.location.href = logoutUrl; // Redirect to external logout URL
  }
}