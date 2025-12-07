import { Component, OnInit } from '@angular/core';
// import { DataService } from './../../data/data.service'; // Relative path guessed

@Component({
    selector: 'app-footer',
    templateUrl: './footer.component.html',
    styleUrls: ['./footer.component.css']
})
export class FooterComponent implements OnInit {

    d = new Date();
    currentYear: any;

    constructor(private dataService: DataService) { }

    ngOnInit() {
        this.currentYear = this.d.getFullYear();

        // this.dataService.getChangeReasonList().subscribe(
        //     response => {
        //         if (response.result && response.result.length > 0) {
        //             this.dataService.setChangeReason(response.result);
        //         } else {
        //             this.dataService.changeReasonList = [];
        //         }
        //     }
        // );
    }
}