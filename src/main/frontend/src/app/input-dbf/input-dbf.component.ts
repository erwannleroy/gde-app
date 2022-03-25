import { Component, OnInit, Input, ElementRef, ViewChild } from '@angular/core';
import { NgxFileDropEntry, FileSystemFileEntry, FileSystemDirectoryEntry } from 'ngx-file-drop';
import { GdeService } from '../gde.service';
import { Subscription } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { BVResponse, DECResponse, EXUResponse, MeteoResponse } from '../Response';
import { ComputingResult } from '../ComputingResult';

@Component({
  selector: 'app-input-dbf',
  templateUrl: './input-dbf.component.html',
  styleUrls: ['./input-dbf.component.css']
})
export class InputDbfComponent implements OnInit {

  title = 'front';

  maxPrecipitations: number;
  coefA: number;
  coefB: number;

  public result: ComputingResult;

  public bvfiles: NgxFileDropEntry[] = [];
  public decfiles: NgxFileDropEntry[] = [];
  public exufiles: NgxFileDropEntry[] = [];

  public bvResponse: BVResponse;
  public decResponse: DECResponse;
  public exuResponse: EXUResponse;
  public meteoResponse: MeteoResponse;

  public bvDecFilled: boolean = false;
  public decFilled: boolean = false;
  public bvExuFilled: boolean = false;

  public bvDecSent: boolean = false;
  public decSent: boolean = false;
  public bvExuSent: boolean = false;

  public freeze: boolean = false;
  public fileUrl: any;

  public bytes: Blob;

  public opened: boolean = false;
  public meteoTurn: boolean = true;
  public bvDecTurn: boolean = false;
  public decTurn: boolean = false;
  public bvExuTurn: boolean = false;

  @ViewChild('errorparams') errorparams: ElementRef;

  constructor(private gdeService: GdeService, private modalService: NgbModal) {
  }

  ngOnInit() {
    this.gdeService.getBVResponse().subscribe(data => {
      console.log("Réception d'une BVResponse", data);
      if (data) {
        // console.log("bvFilled 1 ", this.bvFilled);
        this.bvResponse = data;
        this.gdeService.refreshResult();
        //console.log("bvFilled 2 ", this.bvFilled);
        this.bvDecTurn = this.bvResponse.error;
        this.decTurn = !this.bvResponse.error;
        //console.log("bvFilled", this.bvFilled);
      }
    });

    this.gdeService.getDECResponse().subscribe(data => {
      console.log("Réception d'une DECResponse");
      this.decResponse = data;
      this.gdeService.refreshResult();
      this.decTurn = this.decResponse.error;
    });

    this.gdeService.getEXUResponse().subscribe(data => {
      console.log("Réception d'une EXUResponse");
      this.exuResponse = data;
      this.gdeService.refreshResult();
      this.bvExuTurn = this.exuResponse.error;
    });

    this.gdeService.getMeteoResponse().subscribe(data => {
      console.log("Réception d'une MeteoResponse", data);
      this.meteoResponse = data;
      this.freeze = false;
      if (this.meteoResponse.result) {
        this.bvDecTurn = true;
        this.bvExuTurn = true;
        this.meteoTurn = false;
      } else {
        this.bvDecTurn = false;
        this.bvExuTurn = false;
        this.meteoTurn = true;
      }
    }, error => {
      this.bvDecTurn = false;
      this.bvExuTurn = false;
      this.meteoTurn = true;
    }
    );

    this.gdeService.getResetOrder().subscribe(data => {
      this.reset();
    });

    this.gdeService.getResult().subscribe(data => {
      console.log("Réception d'un résultat", data);
      this.result = data;

      const objRetWait = this.bvDecSent && !data.objRetComputeOk && !data.xlsComputationOk;
      const retWait = this.decSent && !data.retComputeOk && !data.xlsComputationOk && !data.dbfComputationOk;
      const cassisWait = this.bvExuSent && !data.cassisComputeOk && !data.xlsComputationOk;
      const q100Wait = this.bvExuSent && !data.q100ComputeOk && !data.xlsComputationOk;

      if (this.result.error) {
        //this.openPopup("errorparams");
        //this.openPopup(exuparams);
         this.openPopup(this.errorparams);
      }
      else if (objRetWait || retWait || cassisWait || q100Wait) {
        this.freeze = true;
        console.log("freeze");
      } else {
        this.freeze = false;
        console.log("defrost");
      }
    });
  }

  reset() {
    this.bvDecFilled = false;
    this.decFilled = false;
    this.bvExuFilled = false;
    this.bvDecSent = false;
    this.decSent = false;
    this.bvExuSent = false;
    this.bvDecTurn = false;
    this.decTurn = false;
    this.bvExuTurn = false;
    this.meteoTurn = true;
  }

  applyMeteo() {
    this.freeze = true;
    this.gdeService.applyMeteo(this.maxPrecipitations, this.coefA, this.coefB);
  }

  openPopup(content) {
    console.log('openPopup content', content);
    console.log('openPopup result', this.result);
    console.log('openPopup opened', this.opened);
        if (!this.opened) {
      this.modalService.open(content,
        { ariaLabelledBy: 'modal-basic-title' }).result.then(() => { }, () => { });
        this.opened = true;
    }
  }

  closePopup() {
    this.opened = false;
  }

  public bv_dropped(files: NgxFileDropEntry[]) {
    console.log("bv_dropped");
    this.freeze = true;
    this.bvDecFilled = true;
    this.bvResponse = null;
    this.bvfiles = files;
    for (const droppedFile of files) {

      // Is it a file?
      if (droppedFile.fileEntry.isFile) {
        const fileEntry = droppedFile.fileEntry as FileSystemFileEntry;
        fileEntry.file((file: File) => {

          // Here you can access the real file
          console.log(droppedFile.relativePath, file);

          this.gdeService.postBVFile(file);
          this.bvDecSent = true;
          this.bvDecTurn = false;
        });
      } else {
        // It was a directory (empty directories are added, otherwise only files)
        const fileEntry = droppedFile.fileEntry as FileSystemDirectoryEntry;
        console.log(droppedFile.relativePath, fileEntry);
      }
    }
    this.gdeService.refreshResult();
  }


  public dec_dropped(files: NgxFileDropEntry[]) {
    this.decFilled = true;
    this.decResponse = null;
    this.decfiles = files;
    this.freeze = true;
    for (const droppedFile of files) {

      // Is it a file?
      if (droppedFile.fileEntry.isFile) {
        const fileEntry = droppedFile.fileEntry as FileSystemFileEntry;
        fileEntry.file((file: File) => {

          // Here you can access the real file
          console.log(droppedFile.relativePath, file);

          this.gdeService.postDECFile(file);
          this.decSent = true;
          this.decTurn = false;
        });
      } else {
        // It was a directory (empty directories are added, otherwise only files)
        const fileEntry = droppedFile.fileEntry as FileSystemDirectoryEntry;
        console.log(droppedFile.relativePath, fileEntry);
      }
    }

    this.gdeService.refreshResult();
  }

  public exu_dropped(files: NgxFileDropEntry[]) {
    this.freeze = true;
    this.bvExuFilled = true;
    this.exuResponse = null;
    this.exufiles = files;
    for (const droppedFile of files) {

      // Is it a file?
      if (droppedFile.fileEntry.isFile) {
        const fileEntry = droppedFile.fileEntry as FileSystemFileEntry;
        fileEntry.file((file: File) => {

          // Here you can access the real file
          console.log(droppedFile.relativePath, file);

          this.gdeService.postEXUFile(file);
          this.bvExuSent = true;
          this.bvExuTurn = false;
        });
      } else {
        // It was a directory (empty directories are added, otherwise only files)
        const fileEntry = droppedFile.fileEntry as FileSystemDirectoryEntry;
        console.log(droppedFile.relativePath, fileEntry);
      }
    }
    this.gdeService.refreshResult();
  }

  public fileOver(event) {
    console.log(event);
  }

  public fileLeave(event) {
    console.log(event);
  }


}
