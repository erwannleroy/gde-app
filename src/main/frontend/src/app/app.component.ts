import { Component, OnInit } from '@angular/core';
import { NgxFileDropEntry, FileSystemFileEntry, FileSystemDirectoryEntry } from 'ngx-file-drop';
import { GdeService } from './gde.service';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { ComputingResult } from './ComputingResult';
import { DomSanitizer } from '@angular/platform-browser';
import { saveAs } from 'file-saver';
import { BVResponse, DECResponse, EXUResponse } from './Response';
import { Subscription, interval } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'front';

  public bvfiles: NgxFileDropEntry[] = [];
  public decfiles: NgxFileDropEntry[] = [];
  public exufiles: NgxFileDropEntry[] = [];

  public bvResponse: BVResponse;
  public decResponse: DECResponse;
  public exuResponse: EXUResponse;
  public result: ComputingResult;
  public bvFilled: boolean = false;
  public decFilled: boolean = false;
  public exuFilled: boolean = false;
  public bvSent: boolean = false;
  public decSent: boolean = false;
  public exuSent: boolean = false;
  public fileUrl: any;
  public bytes: Blob;

  public bvTurn: boolean = true;
  public decTurn: boolean = false;
  public exuTurn: boolean = false;

  aliveSub: Subscription;

  constructor(private gdeService: GdeService, private sanitizer: DomSanitizer,
    private modalService: NgbModal) {
  }

  ngOnInit() {
    this.gdeService.getBVResponse().subscribe(data => {
      console.log("Réception d'une BVResponse", data);
      if (data) {
        this.bvResponse = data;
        this.refreshResult();
        this.bvTurn = this.bvResponse.error;
        this.decTurn = !this.bvResponse.error;
      }
    });

    this.gdeService.getDECResponse().subscribe(data => {
      console.log("Réception d'une DECResponse");
      this.decResponse = data;
      this.refreshResult();
      this.decTurn = this.decResponse.error;
      this.exuTurn = !this.decResponse.error;
    });

    this.gdeService.getEXUResponse().subscribe(data => {
      console.log("Réception d'une EXUResponse");
      this.exuResponse = data;
      this.refreshResult();
      this.exuTurn = this.exuResponse.error;
    });

    this.gdeService.getResult().subscribe(data => {
      console.log("Réception d'un résultat");
      this.result = data;
    });

    this.gdeService.getResultBytes().subscribe(data => {
      console.log("Bytes recus");
      this.bytes = data;
    });

    const source = interval(1000);
    this.aliveSub = source.subscribe(val => this.gdeService.ping());
  }

  reset() {

    this.bvFilled = false;
    this.decFilled = false;
    this.exuFilled = false;
    this.bvSent = false;
    this.decSent = false;
    this.exuSent = false;
    this.bvTurn = true;
    this.decTurn = false;
    this.exuTurn = false;
    this.result = null;
  }

  refreshResult() {
    this.gdeService.refreshResult();
  }

  openPopup(content) {
    this.modalService.open(content,
      {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {}, (reason) => {});
  }


  public bv_dropped(files: NgxFileDropEntry[]) {
    console.log("bv_dropped");
    this.bvFilled = true;
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
          this.bvSent = true;
          this.bvTurn = false;
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
    this.decfiles = files;
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

  public exu_dropped(files: NgxFileDropEntry[]) {
    this.exuFilled = true;
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

  downloadFile(): string {
    console.log("clicl sur Télécharger");
    console.log("bytes", this.bytes);
    const url = window.URL.createObjectURL(this.bytes); // <-- work with blob directly

    // create hidden dom element (so it works in all browsers)
    const a = document.createElement('a');
    a.setAttribute('style', 'display:none;');
    document.body.appendChild(a);

    // create file, attach to hidden element and open hidden element
    a.href = url;
    a.download = "gde.xlsx";
    a.click();
    return url;
  }

}
