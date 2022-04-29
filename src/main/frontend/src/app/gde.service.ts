import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';

import { HttpClient } from '@angular/common/http';
import { ComputingResult } from './ComputingResult';
import { DECResponse, MeteoResponse, BVDECResponse, BVEXUResponse, EXUResponse } from './Response';
import { DonneesMeteo } from './DonneesMeteo';

@Injectable({
  providedIn: 'root'
})
export class GdeService {


  bvDecSent: boolean = false;
  decSent: boolean = false;
  bvExuSent: boolean = false;
  exuSent: boolean = false;
  public bvDecFileName: string = "";
  public exuFileName: string = "";

  private subjectMeteo = new Subject<MeteoResponse>();
  private subjectBVDEC = new Subject<BVDECResponse>();
  private subjectDEC = new Subject<DECResponse>();
  private subjectBVEXU = new Subject<BVEXUResponse>();
  private subjectEXU = new Subject<EXUResponse>();
  private subjectResult = new Subject<ComputingResult>();
  private subjectMeteoParam = new Subject<DonneesMeteo>();
  private subjectBytesXLS = new Subject<Blob>();
  private subjectBytesPerfDBF = new Subject<Blob>();
  private subjectBytesDebitDBF = new Subject<Blob>();
  private nbTry: number = 0;
  private subjectBVSent = new Subject<Object>();
  private subjectDECSent = new Subject<Object>();
  private subjectBVEXUSent = new Subject<Object>();
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

  refreshDonneesMeteo() {
     this.http.get<DonneesMeteo>('gde/get-meteo').subscribe(async data => {
      console.log("retour du WS", data);
      console.log(data);
      this.subjectMeteoParam.next(data);
    });
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

  getDonneesMeteoParam(): Observable<DonneesMeteo> {
    return this.subjectMeteoParam.asObservable();
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

  getBVEXUSent(): Observable<Object> {
    return this.subjectBVEXUSent.asObservable();
  }

  getEXUSent(): Observable<Object> {
    return this.subjectEXUSent.asObservable();
  }

  getBVDECResponse(): Observable<BVDECResponse> {
    return this.subjectBVDEC.asObservable();
  }

  getDECResponse(): Observable<DECResponse> {
    return this.subjectDEC.asObservable();
  }

  getBVEXUResponse(): Observable<BVEXUResponse> {
    return this.subjectBVEXU.asObservable();
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

  getResultBytesPerfBDF(): Observable<Blob> {
    return this.subjectBytesPerfDBF.asObservable();
  }

  getResultBytesDebitBDF(): Observable<Blob> {
    return this.subjectBytesDebitDBF.asObservable();
  }

  postBVDECFile(file: File) {
    this.bvDecFileName = file.name;
    this.bvDecSent = true;
    this.subjectBVDEC.next(null);
    console.log("postBVDECFile : " + file);
    const formData: FormData = new FormData();
    formData.append('bvdec', file);
    this.subjectBVSent.next();
    this.http.post<BVDECResponse>('gde/upload-bv-decanteurs-file', formData).subscribe(data => {
      console.log("retour du WS");
      console.log(data);
      this.subjectBVDEC.next(data);
      this.refreshResult();
    },
      error => {
        let bvR = new BVDECResponse();
        bvR.error = true;
        bvR.errorMessage = "Fichier incorrect";
        this.subjectBVDEC.next(bvR);
        this.refreshResult();
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
      this.refreshResult();
    },
      error => {
        let decR = new DECResponse();
        decR.error = true;
        decR.errorMessage = "Fichier incorrect";
        this.subjectDEC.next(decR);
        this.refreshResult();
      });
  }

  postBVEXUFile(file: File) {
    this.bvExuSent = true;
    console.log("postBVEXUFile : " + file);
    const formData: FormData = new FormData();
    formData.append('bvexu', file);
    this.subjectBVEXUSent.next();
    this.http.post<BVEXUResponse>('gde/upload-bv-exutoires-file', formData).subscribe(data => {
      console.log("retour du WS");
      console.log(data);
      this.subjectBVEXU.next(data);
      this.refreshResult();
    },
      error => {
        let bvExuR = new BVEXUResponse();
        bvExuR.error = true;
        bvExuR.errorMessage = "Fichier incorrect";
        this.subjectBVEXU.next(bvExuR);
        this.refreshResult();
      });
  }

  postEXUFile(file: File) {
    this.exuFileName = file.name;
    this.exuSent = true;
    console.log("postEXUFile : " + file);
    const formData: FormData = new FormData();
    formData.append('exu', file);
    this.subjectEXUSent.next();
    this.http.post<EXUResponse>('gde/upload-exutoires-file', formData).subscribe(data => {
      console.log("retour du WS");
      console.log(data);
      this.subjectEXU.next(data);
      this.refreshResult();
    },
      error => {
        let exuR = new EXUResponse();
        exuR.error = true;
        exuR.errorMessage = "Fichier incorrect";
        this.subjectEXU.next(exuR);
        this.refreshResult();
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

      const objRetWait = this.bvDecSent && !data.objRetComputeOk;
      const retWait = this.decSent && !data.retComputeOk && !data.perfDbfComputationOk;
      const cassisWait = this.bvExuSent && !data.cassisComputeOk;
      const q100Wait = this.bvExuSent && !data.q100ComputeOk;
      const exuWait = this.exuSent && !data.debitDbfComputationOk;

      if (!data.error && (objRetWait || retWait || cassisWait || q100Wait || exuWait)) {
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
          this.http.get('gde/get-result-bytes-perf-dbf', { responseType: 'blob' }).subscribe(async data => {
            console.log("Résultat perf DBF prêt", data);
            console.log(data);
            this.subjectBytesPerfDBF.next(data);
          });
        }
        if (this.exuSent) {
          this.http.get('gde/get-result-bytes-debit-dbf', { responseType: 'blob' }).subscribe(async data => {
            console.log("Résultat debit DBF prêt", data);
            console.log(data);
            this.subjectBytesDebitDBF.next(data);
          });
        }
      }
    });

  }

}
