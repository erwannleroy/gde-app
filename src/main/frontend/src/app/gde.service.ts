import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';

import { HttpClient } from '@angular/common/http';
import { ComputingResult } from './ComputingResult';
import { BVResponse, DECResponse, EXUResponse, MeteoResponse } from './Response';

@Injectable({
  providedIn: 'root'
})
export class GdeService {

  bvDecSent: boolean = false;
  decSent: boolean = false;
  bvExuSent: boolean = false;

  private subjectMeteo = new Subject<MeteoResponse>();
  private subjectBV = new Subject<BVResponse>();
  private subjectDEC = new Subject<DECResponse>();
  private subjectEXU = new Subject<EXUResponse>();
  private subjectResult = new Subject<ComputingResult>();
  private subjectBytesXLS = new Subject<Blob>();
  private subjectBytesDBF = new Subject<Blob>();
  private nbTry: number = 0;
  private subjectBVSent = new Subject<Object>();
  private subjectDECSent = new Subject<Object>();
  private subjectEXUSent = new Subject<Object>();

  private subjectReset = new Subject<Object>();

  constructor(private http: HttpClient) {
  }

  ping(): void {
    //console.log("ping back end");
    this.http.post<any>('gde/ping', null).subscribe(data => {
      //console.log("rest ping");
    });
  }


  reset(): void {
    console.log("reset compute");
    this.http.post<any>('gde/reset', null).subscribe(data => {
      console.log("rest reset");
    });
    this.subjectReset.next();
  }

  applyMeteo(maxPrecipitations: number, coefA: number, coefB: number) {
    console.log("applyMeteo");
    const params = { 'maxPrecipitations': maxPrecipitations, 'coefA': coefA, 'coefB': coefB };
    console.log("meteo params", params);
    this.http.post<any>('gde/apply-meteo', params).subscribe(data => {
      console.log("post meteo", params);
      this.subjectMeteo.next(data);
    },
      error => {
        let metR = new MeteoResponse();
        metR.error = true;
        metR.errorMessage = "Paramètres incorrects";
        this.subjectMeteo.next(metR);
      });
  }

  getResetOrder(): Observable<Object> {
    return this.subjectReset.asObservable();
  }

  getBVSent(): Observable<Object> {
    return this.subjectBVSent.asObservable();
  }

  getDECSent(): Observable<Object> {
    return this.subjectDECSent.asObservable();
  }

  getEXUSent(): Observable<Object> {
    return this.subjectEXUSent.asObservable();
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

  getMeteoResponse(): Observable<MeteoResponse> {
    return this.subjectMeteo.asObservable();
  }

  getResult(): Observable<ComputingResult> {
    return this.subjectResult.asObservable();
  }

  getResultBytesXLS(): Observable<Blob> {
    return this.subjectBytesXLS.asObservable();
  }

  getResultBytesBDF(): Observable<Blob> {
    return this.subjectBytesDBF.asObservable();
  }

  postBVFile(file: File) {
    this.bvDecSent = true;
    this.subjectBV.next(null);
    console.log("postBVFile : " + file);
    const formData: FormData = new FormData();
    formData.append('bv', file);
    this.subjectBVSent.next();
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
    this.decSent = true;
    console.log("postDECFile : " + file);
    const formData: FormData = new FormData();
    formData.append('dec', file);
    this.subjectDECSent.next();
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
    this.bvExuSent = true;
    console.log("postEXUFile : " + file);
    const formData: FormData = new FormData();
    formData.append('exu', file);
    this.subjectEXUSent.next();
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

    this.http.get<ComputingResult>('gde/get-result').subscribe(async data => {
      console.log("retour du WS", data);
      console.log(data);
      this.subjectResult.next(data);
      const objRetWait = this.bvDecSent && !data.objRetComputeOk && !data.xlsComputationOk;
      const retWait = this.decSent && !data.retComputeOk && !data.xlsComputationOk && !data.dbfComputationOk;
      const cassisWait = this.bvExuSent && !data.cassisComputeOk && !data.xlsComputationOk;
      const q100Wait = this.bvExuSent && !data.q100ComputeOk && !data.xlsComputationOk;


      if (!data.error && (objRetWait || retWait || cassisWait || q100Wait)) {
        console.log("Résultat pas encore prêt, on attend");
        await this.delay(1000);
        this.refreshResult();
      } else {
        this.http.get('gde/get-result-bytes-xls', { responseType: 'blob' }).subscribe(async data => {
          console.log("Résultat prêt", data);
          console.log(data);
          this.subjectBytesXLS.next(data);
        });
        if (this.decSent) {
          this.http.get('gde/get-result-bytes-dbf', { responseType: 'blob' }).subscribe(async data => {
            console.log("Résultat DBF prêt", data);
            console.log(data);
            this.subjectBytesDBF.next(data);
          });  
        }
      } 
    });

  }

}
