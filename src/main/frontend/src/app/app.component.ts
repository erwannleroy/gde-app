import { Component, OnInit } from '@angular/core';
import { GdeService } from './gde.service';
import { Subscription, interval } from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'front';

  aliveSub: Subscription;

  constructor(private gdeService: GdeService) {
  }

  ngOnInit() {
    const source = interval(1000);
    this.aliveSub = source.subscribe(() => this.gdeService.ping());
  }

  reset() {
    this.gdeService.reset();
    window.location.reload();
  }


}
