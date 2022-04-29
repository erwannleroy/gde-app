import { Component, OnInit, Input, ElementRef, ViewChild } from '@angular/core';
import { NgxFileDropEntry, FileSystemFileEntry, FileSystemDirectoryEntry } from 'ngx-file-drop';
import { GdeService } from '../gde.service';
import { Subscription } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DECResponse, BVEXUResponse, MeteoResponse, BVDECResponse, EXUResponse } from '../Response';
import { ComputingResult } from '../ComputingResult';
import { DonneesMeteo } from '../DonneesMeteo';

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
  public donneesMeteo: DonneesMeteo = new DonneesMeteo();

  public bvDecFiles: NgxFileDropEntry[] = [];
  public decFiles: NgxFileDropEntry[] = [];
  public bvExuFiles: NgxFileDropEntry[] = [];
  public exuFiles: NgxFileDropEntry[] = [];

  public bvDecResponse: BVDECResponse;
  public decResponse: DECResponse;
  public bvExuResponse: BVEXUResponse;
  public meteoResponse: MeteoResponse;
  public exuResponse: EXUResponse;
  
  public bvDecFilled: boolean = false;
  public decFilled: boolean = false;
  public bvExuFilled: boolean = false;
  public exuFilled: boolean = false;

  public bvDecSent: boolean = false;
  public decSent: boolean = false;
  public bvExuSent: boolean = false;
  public exuSent: boolean = false;

  public freeze: boolean = false;
  public fileUrl: any;

  public bytes: Blob;

  public opened: boolean = false;
  public meteoTurn: boolean = true;
  public bvDecTurn: boolean = false;
  public decTurn: boolean = false;
  public bvExuTurn: boolean = false;
  public exuTurn: boolean = false;

  @ViewChild('errormeteo') errormeteo: ElementRef;
  @ViewChild('errordbf') errordbf: ElementRef;

  constructor(private gdeService: GdeService, private modalService: NgbModal) {
  }

  ngOnInit() {
    this.gdeService.getBVDECResponse().subscribe(data => {
      console.log("Réception d'une BVDECResponse", data);
      if (data) {
        // console.log("bvFilled 1 ", this.bvFilled);
        this.bvDecResponse = data;
        this.gdeService.refreshResult();
        //console.log("bvFilled 2 ", this.bvFilled);
        this.bvDecTurn = this.bvDecResponse.error;
        this.decTurn = !this.bvDecResponse.error;
        //console.log("bvFilled"  , this.bvFilled);
      }
    });

    this.gdeService.getDECResponse().subscribe(data => {
      console.log("Réception d'une DECResponse", data);
      this.decResponse = data;
      this.gdeService.refreshResult();
      this.decTurn = this.decResponse.error;
    });

    this.gdeService.getBVEXUResponse().subscribe(data => {
      console.log("Réception d'une BVEXUResponse", data);
      this.bvExuResponse = data;
      this.gdeService.refreshResult();
      this.bvExuTurn = this.bvExuResponse.error;
      this.exuTurn = !this.bvExuResponse.error;
    });

    
    this.gdeService.getEXUResponse().subscribe(data => {
      console.log("Réception d'une EXUResponse", data);
      this.exuResponse = data;
      this.gdeService.refreshResult();
      this.exuTurn = this.exuResponse.error;
    });

    this.gdeService.getMeteoResponse().subscribe(data => {
      console.log("Réception d'une MeteoResponse", data);
      this.meteoResponse = data;
      this.freeze = false;
      if (this.meteoResponse.result) {
        this.bvDecTurn = true;
        this.decTurn = false;
        this.bvExuTurn = true;
        this.exuTurn = false;
        this.meteoTurn = false;
      } else {
        this.bvDecTurn = false;
        this.decTurn = false;
        this.exuTurn = false;
        this.bvExuTurn = false;
        this.meteoTurn = true;
        this.openPopup(this.errormeteo);
      }
    }, error => {
      this.bvDecTurn = false;
      this.decTurn = false;
      this.bvExuTurn = false;
      this.exuTurn = false;
      this.meteoTurn = true;
      this.openPopup(this.errormeteo);
    }
    );

    this.gdeService.getResetOrder().subscribe(data => {
      this.reset();
    });

    
    this.gdeService.getDonneesMeteoParam().subscribe(data => {
      this.coefA = data.coefA;
      this.coefB = data.coefB;
      this.maxPrecipitations = data.maxPrecipitations;
    });

    this.gdeService.refreshDonneesMeteo();

    this.gdeService.getResult().subscribe(data => {
      console.log("Réception d'un résultat", data);
      this.result = data;

      const objRetWait = this.bvDecSent && !data.objRetComputeOk;
      const retWait = this.decSent && !data.retComputeOk && !data.perfDbfComputationOk;
      const cassisWait = this.bvExuSent && !data.cassisComputeOk;
      const q100Wait = this.bvExuSent && !data.q100ComputeOk;
      const exuWait = this.exuSent && !data.debitDbfComputationOk;

      if (this.result.error) {
        console.log('on ouvre la popup error');
        this.openPopup(this.errordbf);
        if (objRetWait) {
          console.log("objRetWait");
          this.meteoTurn = false;
          this.bvDecTurn = true;
          this.decTurn = false;
          this.bvExuTurn = true;
          this.exuTurn = false;
        } else if (retWait) {
          console.log("retWait");
          this.meteoTurn = false;
          this.bvDecTurn = false;
          this.decTurn = true;
          this.bvExuTurn = true;
          this.exuTurn = false;
        } else if (q100Wait) {
          console.log("q100Wait");
          this.meteoTurn = false;
          this.bvDecTurn = false;
          this.decTurn = false;
          this.bvExuTurn = true;
          this.exuTurn = false;
        } else {
          this.meteoTurn = false;
          this.bvDecTurn = false;
          this.decTurn = false;
          this.bvExuTurn = false;
          this.exuTurn = true;
        }
        this.freeze = false;
      }
      else if (objRetWait || retWait || cassisWait || q100Wait || exuWait) {
        this.freeze = true;
        console.log("freeze");
      } else {
        this.freeze = false;
        console.log("defrost");
      }
    });
  }

  reset() {
    this.bvDecResponse = null;
    this.bvExuResponse = null;
    this.exuResponse = null;
    this.decResponse = null;
    this.result = null;
    this.bvDecFilled = false;
    this.decFilled = false;
    this.bvExuFilled = false;
    this.exuFilled = false;
    this.bvDecSent = false;
    this.decSent = false;
    this.bvExuSent = false;
    this.exuSent = false;
    this.bvDecTurn = false;
    this.decTurn = false;
    this.bvExuTurn = false;
    this.meteoTurn = true;
    this.exuTurn = false;
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

  public bvdec_dropped(files: NgxFileDropEntry[]) {
    console.log("bvdec_dropped");
    this.freeze = true;
    this.bvDecFilled = true;
    this.bvDecResponse = null;
    this.bvDecFiles = files;
    for (const droppedFile of files) {

      // Is it a file?
      if (droppedFile.fileEntry.isFile) {
        const fileEntry = droppedFile.fileEntry as FileSystemFileEntry;
        fileEntry.file((file: File) => {

          // Here you can access the real file
          console.log(droppedFile.relativePath, file);

          this.gdeService.postBVDECFile(file);
          this.bvDecSent = true;
          this.bvDecTurn = false;
        });
      } else {
        // It was a directory (empty directories are added, otherwise only files)
        const fileEntry = droppedFile.fileEntry as FileSystemDirectoryEntry;
        console.log(droppedFile.relativePath, fileEntry);
      }
    }
  }


  public dec_dropped(files: NgxFileDropEntry[]) {
    this.decFilled = true;
    this.decResponse = null;
    this.decFiles = files;
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

  }

  public bvexu_dropped(files: NgxFileDropEntry[]) {
    this.freeze = true;
    this.bvExuFilled = true;
    this.bvExuResponse = null;
    this.bvExuFiles = files;
    for (const droppedFile of files) {

      // Is it a file?
      if (droppedFile.fileEntry.isFile) {
        const fileEntry = droppedFile.fileEntry as FileSystemFileEntry;
        fileEntry.file((file: File) => {

          // Here you can access the real file
          console.log(droppedFile.relativePath, file);

          this.gdeService.postBVEXUFile(file);
          this.bvExuSent = true;
          this.bvExuTurn = false;
        });
      } else {
        // It was a directory (empty directories are added, otherwise only files)
        const fileEntry = droppedFile.fileEntry as FileSystemDirectoryEntry;
        console.log(droppedFile.relativePath, fileEntry);
      }
    }
  }


  public exu_dropped(files: NgxFileDropEntry[]) {
    this.freeze = true;
    this.exuFilled = true;
    this.exuResponse = null;
    this.exuFiles = files;
    for (const droppedFile of files) {

      // Is it a file?
      if (droppedFile.fileEntry.isFile) {
        const fileEntry = droppedFile.fileEntry as FileSystemFileEntry;
        fileEntry.file((file: File) => {

          // Here you can access the real file
          console.log(droppedFile.relativePath, file);

          this.gdeService.postEXUFile(file);
          this.exuSent = true;
          this.exuTurn = false;
        });
      } else {
        // It was a directory (empty directories are added, otherwise only files)
        const fileEntry = droppedFile.fileEntry as FileSystemDirectoryEntry;
        console.log(droppedFile.relativePath, fileEntry);
      }
    }
  }

  public fileOver(event) {
    console.log(event);
  }

  public fileLeave(event) {
    console.log(event);
  }


}
