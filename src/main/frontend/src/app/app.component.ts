import { Component, OnInit } from '@angular/core';
import { NgxFileDropEntry, FileSystemFileEntry, FileSystemDirectoryEntry } from 'ngx-file-drop';
import { GdeService } from './gde.service';
import { BVResponse } from './BVResponse';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { DECResponse } from './DECResponse';
import { ComputingResult } from './ComputingResult';
import { DomSanitizer } from '@angular/platform-browser';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'front';

  public bvfiles: NgxFileDropEntry[] = [];
  public decfiles: NgxFileDropEntry[] = [];

  public bvResponse: BVResponse;
  public decResponse: DECResponse;
  public result: ComputingResult;
  public bvFilled: boolean = false;
  public decFilled: boolean = false;
  public fileUrl: any;
  public bytes: Blob;

  constructor(private gdeService: GdeService, private sanitizer: DomSanitizer) {
  }

  ngOnInit() {
    this.gdeService.getBVResponse().subscribe(data => {
      console.log("Réception d'une BVResponse");
      this.bvResponse = data;
      this.refreshResult();
    });

    this.gdeService.getDECResponse().subscribe(data => {
      console.log("Réception d'une DECResponse");
      this.decResponse = data;
      this.refreshResult();
    });

    this.gdeService.getResult().subscribe(data => {
      console.log("Réception d'un résultat");
      this.result = data;
      // if (this.result && this.result.xls) {
      //   //const blob = new Blob([this.result.xls], { type: 'application/vnd.ms.excel' });
      //   //const file = new File([blob], 'gde.xlsx', { type: 'application/vnd.ms.excel' });
      //   //saveAs(file);

      //   const blob = new Blob([this.result.xls], { type: 'application/vnd.ms.excel' });
      //   var a = document.createElement('a');
      //   a.href = URL.createObjectURL(blob);
      //   a.download = "gde.xlsx";
      //   document.body.appendChild(a);
      //   a.click();
      //   document.body.removeChild(a);

      //   //const blob = new Blob([this.result.xls], { type: 'application/vnd.ms.excel' });
      //   //this.fileUrl = this.sanitizer.bypassSecurityTrustResourceUrl(window.URL.createObjectURL(this.result.xls));
      // }
    });

    this.gdeService.getResultBytes().subscribe(data => {
      console.log("Bytes recus");
      this.bytes = data;
    });
  }

  refreshResult() {
    this.gdeService.refreshResult();
  }

  public bv_dropped(files: NgxFileDropEntry[]) {
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
