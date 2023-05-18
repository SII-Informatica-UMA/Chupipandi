import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { NgbAlertModule, NgbNavModule } from '@ng-bootstrap/ng-bootstrap';
import { AppComponent } from './app.component';
import { DetalleCorreccionComponent } from './detalle-correccion/detalle-correccion.component';
import { DetalleNotasComponent } from './detalle-notas/detalle-notas.component';
import { FormularioNotasComponent } from './formulario-notas/formulario-notas.component';
import { FormularioNotificacionComponent } from './formulario-notificacion/formulario-notificacion.component';


@NgModule({
  declarations: [
    AppComponent,
    FormularioNotasComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    NgbAlertModule,
    NgbNavModule,
    DetalleNotasComponent,
    DetalleCorreccionComponent,
    FormularioNotificacionComponent,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
