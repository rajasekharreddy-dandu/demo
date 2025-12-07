import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
// import { Principal } from 'src/app/security/principal.service'; // Relative path guessed
import { Observable } from 'rxjs/Rx'; // Deprecated import style, preserved from image
// import { ConsoleService } from './../../views/services/console.service'; // Relative path guessed

@Injectable()
export class UserRouteAccessService implements CanActivate {

    flag: boolean = false;
    extroles: any;
    homePageVisited: any;

    constructor(
        private router: Router,
        // private principal: Principal,
        // private printOut: ConsoleService
    ) { }

    canActivate(
        route: ActivatedRouteSnapshot,
        state: RouterStateSnapshot
    ): boolean | Observable<boolean> {

        const authorities: string[] = route.data['authorities'];
        // this.printOut.log('can activate caled');

        const page: any = route.data['page'];
        // this.principal; // Appears to be an unused line in the image
        // this.principal.identity(); // Calls the identity function on the Principal service

        // Set local storage flag for 'hasVisited'
        localStorage.setItem("hasVisited", "N");

        // If no authorities are required for the route, allow access immediately
        if (!authorities || authorities.length === 0) {
            this.flag = true;
            return Observable.of(this.flag);
        }

        // Check if the current user has any of the required authorities
        // let obj: any = this.principal.hasAnyAuthority(authorities);

        // this.printOut.log('act result :' + obj.value);

        // Check the result from the Observable returned by hasAnyAuthority
        // The value check appears to compare the Observable's value to the string "true"
        // if (obj.value !== undefined && obj.value.toString() === "true") {
        //     // Access granted
        //     return Observable.of(true);
        // } else {
        //     // Access denied, navigate to 'accessdenied' route
        //     this.router.navigate(['accessdenied', page]).then(() => {
        //         // empty block in image
        //     });
        //     return Observable.of(false);
        // }
    }
}