import { Component, OnInit } from '@angular/core';
import { GdeService } from '../gde.service';
import { DomSanitizer } from '@angular/platform-browser';
import { Subscription, interval } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ComputingResult } from '../ComputingResult';
import { BVResponse, DECResponse, EXUResponse } from '../Response';

@Component({
  selector: 'app-output-xls',
  templateUrl: './output-xls.component.html',
  styleUrls: ['./output-xls.component.css']
})
export class OutputXlsComponent implements OnInit {

  title = 'front';

  public result: ComputingResult;
  public fileUrl: any;
  public bytes: Blob;
  public bytesDBF: Blob;

  public bvResponse: BVResponse;
  public decResponse: DECResponse;
  public exuResponse: EXUResponse;
  public bvDecSent: boolean = false;
  public decSent: boolean = false;
  public bvExuSent: boolean = false;
  aliveSub: Subscription;

  constructor(private gdeService: GdeService) {
  }

  ngOnInit() {
    this.gdeService.getResetOrder().subscribe(data => {
      this.reset();
    });

    this.gdeService.getBVSent().subscribe(data => {
      console.log("output notified bv dec sent");
      this.bvDecSent = true;
    });

    this.gdeService.getDECSent().subscribe(data => {
      console.log("output notified dec sent");
      this.decSent = true;
    });

    this.gdeService.getEXUSent().subscribe(data => {
      console.log("output notified bv exu sent");
      this.bvExuSent = true;
    });


    this.gdeService.getBVResponse().subscribe(data => {
      console.log("Réception d'un résultat BV", data);
      this.bvResponse = data;
      this.bvDecSent = true;
    });

    this.gdeService.getDECResponse().subscribe(data => {
      console.log("Réception d'un résultat DEC", data);
      this.decResponse = data;
      this.decSent = true;
    });

    this.gdeService.getEXUResponse().subscribe(data => {
      console.log("Réception d'un résultat EXU", data);
      this.exuResponse = data;
      this.bvExuSent = true;
    });

    this.gdeService.getResult().subscribe(data => {
      console.log("Réception d'un résultat", data);
      this.result = data;
    });

    this.gdeService.getResultBytesXLS().subscribe(data => {
      console.log("Bytes XLS recus");
      this.bytes = data;
    });

    this.gdeService.getResultBytesBDF().subscribe(data => {
      console.log("Bytes BDF recus");
      this.bytesDBF = data;
    });

    const source = interval(1000);
    this.aliveSub = source.subscribe(() => this.gdeService.ping());
  }

  reset() {
    console.log("reset ouput-xls");
    this.bvResponse = null;
    this.decResponse = null;
    this.exuResponse = null;
    this.result = null;
    this.bvDecSent = false;
    this.decSent = false;
    this.bvExuSent = false;
  }

  downloadXlsFile(): string {
    console.log("clic sur Télécharger");
    // console.log("bytes", this.bytes);
    const url = window.URL.createObjectURL(this.bytes); // <-- work with blob directly

    // create hidden dom element (so it works in all browsers)
    const a = document.createElement('a');
    a.setAttribute('style', 'display:none;');
    document.body.appendChild(a);

    // create file, attach to hidden element and open hidden element
    a.href = url;
    a.download = "gde-dimensionnement.xlsx";
    a.click();
    return url;
  }

  downloadDbfFile(): string {
    console.log("clic sur Télécharger BDF");
    // console.log("bytes", this.bytes);
    const url = window.URL.createObjectURL(this.bytesDBF); // <-- work with blob directly

    // create hidden dom element (so it works in all browsers)
    const a = document.createElement('a');
    a.setAttribute('style', 'display:none;');
    document.body.appendChild(a);

    // create file, attach to hidden element and open hidden element
    a.href = url;
    a.download = "bv-decanteurs-performance.dbf";
    a.click();
    return url;
  }

}
