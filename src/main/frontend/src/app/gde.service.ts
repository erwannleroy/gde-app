import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';

import { HttpClient, HttpParams, HttpRequest } from '@angular/common/http';
import { ComputingResult } from './ComputingResult';
import { timeout } from 'q';
import { BVResponse, DECResponse, EXUResponse } from './Response';

@Injectable({
  providedIn: 'root'
})
export class GdeService {

  private subjectBV = new Subject<BVResponse>();
  private subjectDEC = new Subject<DECResponse>();
  private subjectEXU = new Subject<EXUResponse>();
  private subjectResult = new Subject<ComputingResult>();
  private subjectBytes = new Subject<Blob>();
  private nbTry: number = 0;
  private computationProblem: boolean = false;
  private bvParsingError = false;

  constructor(private http: HttpClient) {
  }

  ping(): void {
    console.log("ping back end");
    this.http.post<any>('gde/ping', null).subscribe(data => {
      console.log("rest ping");
    });
  }

  getBVResponse(): Observable<BVResponse> {
    return this.subjectBV.asObservable();
  }
  getDECResponse(): Observable<DECResponse> {
    return this.subjectDEC.asObservable();
  }
  getEXUResponse(): Observable<EXUResponse> {
    return this.subjectEXU.asObservable();
  }
  getResult(): Observable<ComputingResult> {
    return this.subjectResult.asObservable();
  }
  getResultBytes(): Observable<Blob> {
    return this.subjectBytes.asObservable();
  }

  postBVFile(file: File) {
    this.subjectBV.next(null);
    this.bvParsingError = false;
    console.log("postBVFile : " + file);
    const formData: FormData = new FormData();
    formData.append('bv', file);
    this.http.post<BVResponse>('gde/upload-bv-decanteurs-file', formData).subscribe(data => {
      console.log("retour du WS");
      console.log(data);
      this.subjectBV.next(data);
    },
      error => {
        let bvR = new BVResponse();
        bvR.error = true;
        bvR.errorMessage = "Fichier incorrect";
        this.subjectBV.next(bvR);
      });

  }

  postDECFile(file: File) {
    console.log("postDECFile : " + file);
    const formData: FormData = new FormData();
    formData.append('dec', file);
    this.http.post<DECResponse>('gde/upload-decanteurs-file', formData).subscribe(data => {
      console.log("retour du WS");
      console.log(data);
      this.subjectDEC.next(data);
    },
      error => {
        let decR = new DECResponse();
        decR.error = true;
        decR.errorMessage = "Fichier incorrect";
        this.subjectDEC.next(decR);
      });
  }

  postEXUFile(file: File) {
    console.log("postEXUFile : " + file);
    const formData: FormData = new FormData();
    formData.append('exu', file);
    this.http.post<EXUResponse>('gde/upload-bv-exutoires-file', formData).subscribe(data => {
      console.log("retour du WS");
      console.log(data);
      this.subjectEXU.next(data);
    },
      error => {
        let exuR = new EXUResponse();
        exuR.error = true;
        exuR.errorMessage = "Fichier incorrect";
        this.subjectEXU.next(exuR);
      });
  }

  async delay(ms: number) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  async refreshResult() {
    console.log("refreshResult");
    if (this.nbTry < 5) {
      this.http.get<ComputingResult>('gde/get-result').subscribe(async data => {
        console.log("retour du WS", data);
        console.log(data);
        this.subjectResult.next(data);
        if (data && data.inProgress) {
          console.log("Résultat pas encore prêt, on attend");
          await this.delay(500);
          this.refreshResult();
          this.nbTry++;
        } else if (data && !data.inProgress) {
          this.http.get('gde/get-result-bytes', { responseType: 'blob' }).subscribe(async data => {
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