import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { BVResponse } from './BVResponse';
import { HttpClient, HttpParams, HttpRequest } from '@angular/common/http';
import { DECResponse } from './DECResponse';

@Injectable({
  providedIn: 'root'
})
export class GdeService {

  private subjectBV = new Subject<BVResponse>();
  private subjectDEC = new Subject<DECResponse>();
  private subjectResult = new Subject<ComputeResult>();

  constructor(private http: HttpClient) {
  }

  getBVResponse(): Observable<BVResponse> {
    return this.subjectBV.asObservable();
  }
  getDECResponse(): Observable<DECResponse> {
    return this.subjectDEC.asObservable();
  }

  postBVFile(file: File) {
    console.log("postBVFile : " + file);
    const formData: FormData = new FormData();
    formData.append('bv', file);
    this.http.post<BVResponse>('http://localhost:8000/upload-bv-file', formData).subscribe(data => {
      //console.log("retour du WS");
      //console.log(data);
      this.subjectBV.next(data);
    });

  }

  postDECFile(file: File) {
    console.log("postDECFile : " + file);
    const formData: FormData = new FormData();
    formData.append('dec', file);
    this.http.post<DECResponse>('http://localhost:8000/upload-dec-file', formData).subscribe(data => {
      //console.log("retour du WS");
      //console.log(data);
      this.subjectDEC.next(data);
    });

  }

  refreshResult()

}
