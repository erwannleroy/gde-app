import { Component, OnInit } from '@angular/core';
import { NgxFileDropEntry, FileSystemFileEntry, FileSystemDirectoryEntry } from 'ngx-file-drop';
import { GdeService } from './gde.service';
import { BVResponse } from './BVResponse';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { DECResponse } from './DECResponse';

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

  constructor(private gdeService: GdeService) {
  }

  ngOnInit() {
    this.gdeService.getBVResponse().subscribe(data => {
      this.bvResponse = data;
      this.refreshResult();
    });
    
    this.gdeService.getDECResponse().subscribe(data => {
      this.decResponse = data;
    });
  }
  refreshResult() {
    this.gdeService.getResult().
  }
  
  public bv_dropped(files: NgxFileDropEntry[]) {
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

 
  public fileOver(event){
    console.log(event);
  }
 
  public fileLeave(event){
    console.log(event);
  }
}
