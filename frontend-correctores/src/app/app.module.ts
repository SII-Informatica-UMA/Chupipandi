import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { HttpClientModule } from '@angular/common/http';
import { DetalleCorrectorComponent } from './detalle-corrector/detalle-corrector.component';
import { FormularioCorrectorComponent } from './formulario-corrector/formulario-corrector.component';
import { FormsModule } from '@angular/forms';
import { ErrorModalComponentComponent } from './error-modal-component/error-modal-component.component';

@NgModule({
  declarations: [
    AppComponent,
    DetalleCorrectorComponent,
    FormularioCorrectorComponent,
    ErrorModalComponentComponent
  ],
  imports: [
    BrowserModule,
    NgbModule,
    FormsModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
