import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { NgbAlertModule } from '@ng-bootstrap/ng-bootstrap';
import { AppComponent } from './app.component';
import { DetalleNotasComponent } from './detalle-notas/detalle-notas.component';
import { FormularioNotasComponent } from './formulario-notas/formulario-notas.component';


@NgModule({
  declarations: [
    AppComponent,
    DetalleNotasComponent,
    FormularioNotasComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    NgbAlertModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
