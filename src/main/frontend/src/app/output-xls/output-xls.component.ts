import { Component, OnInit } from '@angular/core';
import { GdeService } from '../gde.service';
import { DomSanitizer } from '@angular/platform-browser';
import { Subscription, interval } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ComputingResult } from '../ComputingResult';
import { BVDECResponse, DECResponse, EXUResponse, BVEXUResponse } from '../Response';

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
  public bytesPerfDBF: Blob;
  public bytesDebitDBF: Blob;

  public bvDecResponse: BVDECResponse;
  public decResponse: DECResponse;
  public bvExuResponse: BVEXUResponse;
  public exuResponse: EXUResponse;
  public bvDecSent: boolean = false;
  public decSent: boolean = false;
  public bvExuSent: boolean = false;
  public exuSent: boolean = false;
  
  aliveSub: Subscription;

  public opened: boolean = false;

  
  constructor(private gdeService: GdeService, private modalService: NgbModal) {
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

    this.gdeService.getBVEXUSent().subscribe(data => {
      console.log("output notified bv exu sent");
      this.bvExuSent = true;
    });


    this.gdeService.getEXUSent().subscribe(data => {
      console.log("output notified exu sent");
      this.exuSent = true;
    });

    this.gdeService.getBVDECResponse().subscribe(data => {
      console.log("Réception d'un résultat BV DEC", data);
      this.bvDecResponse = data;
      this.bvDecSent = true;
    });

    this.gdeService.getDECResponse().subscribe(data => {
      console.log("Réception d'un résultat DEC", data);
      this.decResponse = data;
      this.decSent = true;
    });

    this.gdeService.getBVEXUResponse().subscribe(data => {
      console.log("Réception d'un résultat BV EXU", data);
      this.bvExuResponse = data;
      this.bvExuSent = true;
    });

    this.gdeService.getEXUResponse().subscribe(data => {
      console.log("Réception d'un résultat EXU", data);
      this.exuResponse = data;
      this.exuSent = true;
    });

    this.gdeService.getResult().subscribe(data => {
      console.log("Réception d'un résultat", data);
      this.result = data;
    });

    this.gdeService.getResultBytesXLS().subscribe(data => {
      console.log("Bytes XLS recus");
      this.bytes = data;
    });

    this.gdeService.getResultBytesPerfBDF().subscribe(data => {
      console.log("Bytes perf BDF recus");
      this.bytesPerfDBF = data;
    });

    this.gdeService.getResultBytesDebitBDF().subscribe(data => {
      console.log("Bytes debit BDF recus");
      this.bytesDebitDBF = data;
    });

    const source = interval(1000);
    this.aliveSub = source.subscribe(() => this.gdeService.ping());
  }

  reset() {
    console.log("reset ouput-xls");
    this.bvDecResponse = null;
    this.decResponse = null;
    this.bvExuResponse = null;
    this.exuResponse = null;
    this.result = null;
    this.bvDecSent = false;
    this.decSent = false;
    this.bvExuSent = false;
  }

  openPopup(content) {
    console.log('openPopup content', content);
    console.log('openPopup result', this.result);
    console.log('openPopup opened', this.opened);
    if (!this.opened) {
      this.modalService.open(content,
        { ariaLabelledBy: 'modal-basic-title' }).result.then(() => {
          console.log("modal result");
          this.opened = false;
        }, () => {
          console.log("modal error");
          this.opened = false;
        });
      this.opened = true;
    }
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

  downloadPerfDbfFile(): string {
    console.log("clic sur Télécharger Perf BDF");
    // console.log("bytes", this.bytes);
    const url = window.URL.createObjectURL(this.bytesPerfDBF); // <-- work with blob directly

    // create hidden dom element (so it works in all browsers)
    const a = document.createElement('a');
    a.setAttribute('style', 'display:none;');
    document.body.appendChild(a);

    // create file, attach to hidden element and open hidden element
    a.href = url;
    a.download = this.gdeService.bvDecFileName;
    a.click();
    return url;
  }

  downloadDebitDbfFile(): string {
    console.log("clic sur Télécharger Debit BDF");
    // console.log("bytes", this.bytes);
    const url = window.URL.createObjectURL(this.bytesDebitDBF); // <-- work with blob directly

    // create hidden dom element (so it works in all browsers)
    const a = document.createElement('a');
    a.setAttribute('style', 'display:none;');
    document.body.appendChild(a);

    // create file, attach to hidden element and open hidden element
    a.href = url;
    a.download = this.gdeService.exuFileName;
    a.click();
    return url;
  }

}
