// src/app/security/has-any-authority.directive.ts

import { Directive, Input, TemplateRef, ViewContainerRef } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { ActivatedRouteSnapshot } from '@angular/router'; // Import is present but unused

import { Principal } from './principal.service'; // Inferred path
import { AccountService } from './account.service';
import { PopService } from '../views/common/pop/pop.service'; // Inferred path
import { ConsoleService } from '../views/services/console.service'; // Inferred path

@Directive({
  selector: '[hasAnyAuthority]' // Inferred selector from usage example
})
export class HasAnyAuthorityDirective {
  private authorities: string[];
  account: any;
  private changes = new Subject<any>(); // Inferred usage from code context

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainerRef: ViewContainerRef,
    private accountService: AccountService,
    private popService: PopService,
    private printOut: ConsoleService // Renamed to fit context
  ) {}

  /**
   * @Input()
   * set hasAnyAuthority(value: string | string[])
   * Conditionally hides an HTML element if current user has any
   * of the authorities passed by the expression.
   *
   * @howToUse
   * <some-element *hasAnyAuthority="['ROLE_ADMIN']"></some-element>
   * <some-element *hasAnyAuthority="['ROLE_ADMIN', 'ROLE_USER']"></some-element>
   */
  @Input()
  set hasAnyAuthority(value: string | string[]) {
    this.authorities = Array.isArray(value) ? value : [value];
    this.printOut.log('Method called directive'); // Assumed logging method
    this.getlatestview();
  }

  // Gets the latest view for the control
  private getlatestview(flag = false): void {
    if (this.account) {
      if (localStorage.getItem('hasVisited') === 'Y') {
        this.account = this.accountService.getUserInfo();

        if (this.account && this.account.batchStatus === 'N' && this.account.authorities && this.account.authorities.length > 0) {
          this.account.authorities.forEach((role: string) => {
            if (!flag) {
              flag = this.authorities.includes(role);
            }
          });
          if (flag) {
            this.viewContainerRef.clear();
            this.viewContainerRef.createEmbeddedView(this.templateRef);
          }
        } else {
          // If no authorities or batchStatus is not 'N', try to fetch user data
          this.getliveUserData(flag);
        }
      } else {
        this.getliveUserData(flag);
      }
    } else {
        this.getliveUserData(flag); // Fallback if account is null/undefined
    }
  }

  private getliveUserData(flag: boolean): void {
    this.accountService.getUser().subscribe(data => {
      this.account = data;

      if (this.account && this.account.loginStatus === 'Y') {
        localStorage.setItem('hasVisited', 'Y');
        this.accountService.setAccount(this.account);
        this.account.batchStatus = this.account.batchStatus || 'N';

        if (this.account && this.account.batchStatus === 'Y') {
          if (localStorage.getItem('batchStatus') !== 'Y') {
             // Inferred popService usage
             // this.popService.batchInfo(common.batchInformation);
             localStorage.setItem('batchStatus', this.account.batchStatus);
          }
          return;
        } else {
          if (this.account && this.account.batchStatus !== 'N' && this.account.authorities && this.account.authorities.length > 0) {
            this.account.authorities.forEach((role: string) => {
              if (!flag) {
                flag = this.authorities.includes(role);
              }
            });

            if (flag) {
              this.viewContainerRef.clear();
              this.viewContainerRef.createEmbeddedView(this.templateRef);
            }

          } else if (localStorage.getItem('batchStatus') !== 'N') {
             // Inferred popService usage
             // this.popService.batchInfo(common.batchDone);
             localStorage.setItem('batchStatus', this.account.batchStatus);
          }
        }
      } else {
          this.getliveUserData(flag); // Recurse or handle error
      }
    });
  }
}