import { Component, OnInit } from '@angular/core';
// import { LoaderService } from './../../preloader/loader.service'; // Relative path guessed
// import { AlertService } from './../../alert/alert.service'; // Relative path guessed
// import { Component, OnInit } from '@angular/core'; // Duplicate import removed

@Component({
    selector: 'app-home',
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

    openModal: any = false;
    openModal1: any = false;
    panelOpenState: any = false;
    homeImage: string = 'assets/img/homeImage.jpg';

    constructor(
        // private load: LoaderService,
        // private alert: AlertService
    ) { }

    ngOnInit() {
        // empty block in image
    }
}