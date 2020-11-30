import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { BVResponse } from './BVResponse';
import { HttpClient, HttpParams, HttpRequest } from '@angular/common/http';
import { DECResponse } from './DECResponse';
import { ComputingResult } from './ComputingResult';
import { timeout } from 'q';

@Injectable({
  providedIn: 'root'
})
export class GdeService {

  private subjectBV = new Subject<BVResponse>();
  private subjectDEC = new Subject<DECResponse>();
  private subjectResult = new Subject<ComputingResult>();
  private subjectBytes  = new Subject<Blob>();
  private nbTry: number = 0;
  private computationProblem: boolean = false;

  constructor(private http: HttpClient) {
  }

  getBVResponse(): Observable<BVResponse> {
    return this.subjectBV.asObservable();
  }
  getDECResponse(): Observable<DECResponse> {
    return this.subjectDEC.asObservable();
  }
  getResult(): Observable<ComputingResult> {
    return this.subjectResult.asObservable();
  }
  getResultBytes(): Observable<Blob> {
    return this.subjectBytes.asObservable();
  }

  postBVFile(file: File) {
    console.log("postBVFile : " + file);
    const formData: FormData = new FormData();
    formData.append('bv', file);
    this.http.post<BVResponse>('http://localhost:8000/upload-bv-file', formData).subscribe(data => {
      console.log("retour du WS");
      console.log(data);
      this.subjectBV.next(data);
    });

  }

  postDECFile(file: File) {
    console.log("postDECFile : " + file);
    const formData: FormData = new FormData();
    formData.append('dec', file);
    this.http.post<DECResponse>('http://localhost:8000/upload-dec-file', formData).subscribe(data => {
      console.log("retour du WS");
      console.log(data);
      this.subjectDEC.next(data);
    });

  }

  async delay(ms: number) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  async refreshResult() {
    console.log("refreshResult");
    if (this.nbTry < 5) {
      this.http.get<ComputingResult>('http://localhost:8000/get-result').subscribe(async data => {
        console.log("retour du WS", data);
        console.log(data);
        this.subjectResult.next(data);
        if (data && data.inProgress) {
          console.log("Résultat pas encore prêt, on attend");
          await this.delay(500);
          this.refreshResult();
          this.nbTry++;
        } else if (data && !data.inProgress) {
          this.http.get('http://localhost:8000/get-result-bytes', {responseType: 'blob'}).subscribe(async data => {
            console.log("Résultat prêt", data);
            console.log(data);
            this.subjectBytes.next(data);
          });
        } else {
          this.nbTry = 0;

        }
      });

    } else {
      this.computationProblem = true;
      this.nbTry = 0;
    }

  }

  
}