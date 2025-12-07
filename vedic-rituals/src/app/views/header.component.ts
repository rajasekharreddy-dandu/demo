import { Component, OnInit, NgZone } from '@angular/core';
import { AccountService } from './../security/account.service'; // Relative path guessed
// import { PopService } from './../../pop/pop.service'; // Relative path guessed
// import * as Common from './../../../constant/constant'; // Relative path guessed
// import { ConsoleService } from './../../services/console.service'; // Relative path guessed
// import { AlertService } from './../../alert/alert.service'; // Relative path guessed
import { Router } from '@angular/router';
// import * as roles from './../roles/roles'; // Relative path guessed - inferred from usage

@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

    firstName: string = '';
    lastName: string = '';
    loginstatus: string = '';
    account: any;
    // ... (other properties, some are cut off)
    // massuploadRole: string = roles.massupload;
    // initiateApprovalRole: string = roles.initiateApproval;
    // approverRole: string = roles.approver;
    // information: any = Common.browserNotification;
    ifBatchIsRunning: boolean = true;
    readonlyUser: boolean = false; // Initialized to false, but set to true in login if not admin

    // Constants from image_cf7eba.jpg
    readonly batchStatus = "batchStatus";
    readonly contextPath = "/Pricing";
    readonly No = "N";

    constructor(
        private ngZone: NgZone,
        private accountService: AccountService,
        // private popService: PopService,
        // private printOut: ConsoleService,
        // private alertService: AlertService,
        public router: Router
    ) { }

    ngOnInit() {
        // Check if login status is 'Y' or 'N' and call login if needed
        this.loginstatus = this.accountService.getLoginStatus() || this.No;
        if (this.loginstatus === this.No || this.loginstatus === '') {
            this.login();
        }
    }

    // Authenticate UI user and get user login details from Server
    login() {
        localStorage.setItem(this.batchStatus, this.No);

        this.accountService.getUser().subscribe(
            response => {
                this.ngZone.run(() => {
                    this.account = response;
                    // this.printOut.log('account::' + JSON.stringify(this.account));
                    // this.accountService.setUser(this.account);
                    this.loginstatus = this.accountService.getLoginStatus();

                    // Check for System Admin role
                    if (this.account && this.account.authorities && this.account.authorities.includes(roles.systemAdmin)) {
                        this.readonlyUser = false;
                    } else {
                        // Assumption: if not System Admin, they are a readonly user
                        this.readonlyUser = true; 
                    }

                    // Handle batch status
                    if (this.account.batchStatus === 'Yes') {
                        // this.popService.batchInfo(Common.batchInformation);
                        localStorage.setItem(this.batchStatus, this.account.batchStatus);

                        // Check for approver role and redirect if true
                        if (this.account.authorities && this.account.authorities.length > 0 && this.account.authorities.includes(roles.approver)) {
                            this.router.navigateByUrl('/OpportunityTracking/approval'); // Path guessed from image
                        }
                    }

                });
            },
            err => {
                // this.alertService.error(Common.pleaseWait); // Value inferred/guessed
                window.location.href = window.location.origin + this.contextPath;
            }
        );
    }

    // Logout the logged in user
    logout() {
        // this.accountService.logOut();
    }

    // Update User Name on header label
    getUserInfo(): boolean {
        this.account = this.accountService.getUserInfo();
        if (this.account) {
            this.firstName = this.account.username;
            return true;
        }
        return false;
    }
}