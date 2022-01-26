import { Component, OnInit } from '@angular/core';
import { NgxFileDropEntry, FileSystemFileEntry, FileSystemDirectoryEntry } from 'ngx-file-drop';
import { GdeService } from '../gde.service';
import { Subscription } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { BVResponse, DECResponse, EXUResponse } from '../Response';
import { ComputingResult } from '../ComputingResult';

@Component({
  selector: 'app-input-dbf',
  templateUrl: './input-dbf.component.html',
  styleUrls: ['./input-dbf.component.css']
})
export class InputDbfComponent implements OnInit {

  title = 'front';

  public result: ComputingResult;
  
  public bvfiles: NgxFileDropEntry[] = [];
  public decfiles: NgxFileDropEntry[] = [];
  public exufiles: NgxFileDropEntry[] = [];

  public bvResponse: BVResponse;
  public decResponse: DECResponse;
  public exuResponse: EXUResponse;

  public bvFilled: boolean = false;
  public decFilled: boolean = false;
  public exuFilled: boolean = false;

  public bvSent: boolean = false;
  public decSent: boolean = false;
  public exuSent: boolean = false;

  public freeze: boolean = false;
  public fileUrl: any;

  public bytes: Blob;

  public bvTurn: boolean = true;
  public decTurn: boolean = false;
  public exuTurn: boolean = true;

  constructor(private gdeService: GdeService, private modalService: NgbModal) {
  }

  ngOnInit() {
    this.gdeService.getBVResponse().subscribe(data => {
      console.log("Réception d'une BVResponse", data);
      if (data) {
        console.log("bvFilled 1 ", this.bvFilled);
        this.bvResponse = data;
        this.gdeService.refreshResult();
        console.log("bvFilled 2 ", this.bvFilled);
        this.bvTurn = this.bvResponse.error;
        this.decTurn = !this.bvResponse.error;
        console.log("bvFilled", this.bvFilled);
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
      this.exuTurn = this.exuResponse.error;
    });

    this.gdeService.getResetOrder().subscribe( data => {
      this.reset();
    });

    this.gdeService.getResult().subscribe(data => {
      console.log("Réception d'un résultat", data);
      this.result = data;
      if (!this.result.inProgress) {
        this.freeze = false;
        console.log("defrost");
      } else {
        this.freeze = true;
        console.log("freeze");
        console.log("freeze " + this.freeze + " - bvTurn " + this.bvTurn);
      }
    });
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
    this.exuTurn = true;
  }


  openPopup(content) {
    this.modalService.open(content,
      { ariaLabelledBy: 'modal-basic-title' }).result.then(() => { }, () => { });
  }


  public bv_dropped(files: NgxFileDropEntry[]) {
    console.log("bv_dropped");
    this.freeze = true;
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
    this.gdeService.refreshResult();
  }

  public fileOver(event) {
    console.log(event);
  }

  public fileLeave(event) {
    console.log(event);
  }


}
