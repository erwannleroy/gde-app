import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { NgxFileDropModule } from 'ngx-file-drop';
import { HttpClientModule } from '@angular/common/http';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { FooterComponent } from './footer/footer.component';
import { InputDbfComponent } from './input-dbf/input-dbf.component';
import { OutputXlsComponent } from './output-xls/output-xls.component';

@NgModule({
  declarations: [
    AppComponent,
    FooterComponent,
    InputDbfComponent,
    OutputXlsComponent
  ],
  imports: [
    BrowserModule,
    NgxFileDropModule,
    HttpClientModule,
    NgbModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
