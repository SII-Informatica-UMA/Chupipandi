import { Component, HostListener, OnInit } from '@angular/core';
import { Corrector, CorrectorNuevo } from './model/interfaces';
import { CorrectorService } from './service/corrector.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FormularioCorrectorComponent } from './formulario-corrector/formulario-corrector.component';
import { TokenService } from './service/token.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  correctores: Corrector[] = [];
  correctorElegido?: Corrector;
  idConvBusq?: bigint;
  busquedaFallida: boolean = false;
  // para comprobar si se ha conectado con el backend
  conectado: boolean = false;

  constructor(private correctorService: CorrectorService, private tokenService: TokenService, private modalService: NgbModal) { }

  ngOnInit(): void {
    this.correctorService.tryConnect().then(() => {
      console.log("Conectado con el backend");
    }).catch(error => {
      // console.error('Error al conectar con el backend', error);
      this.conectado = false;
    });
    this.tokenService.getTokenValidity().subscribe(valid => {
      if (!valid.body?.valueOf()) {
        this.tokenService.updateToken()
        .catch(error => {
          console.error('Error al actualizar el token', error);
        });
      }
      this.actualizaCorrectores()
    })
  }

  @HostListener('keydown', ['$event'])
  onKeyDown(e: KeyboardEvent) {
    switch (e.key) {
      case "ArrowDown":
        let newPosDown = (this.correctores.indexOf(this.correctorElegido ?? this.correctores[0]) + 1) % this.correctores.length;
        this.correctorElegido = this.correctores[newPosDown];
        break;
      case "ArrowUp":
        let newPosUp = (this.correctores.indexOf(this.correctorElegido ?? this.correctores[0]) - 1 + this.correctores.length) % this.correctores.length;
        this.correctorElegido = this.correctores[newPosUp];
        break;
      case "Delete":
        if (this.correctorElegido) {
          this.eliminarCorrector(this.correctorElegido.id);
        }
        break;
      case "Enter":
        if (this.correctorElegido) {
          let ref = this.modalService.open(FormularioCorrectorComponent);
          ref.componentInstance.accion = "Editar";
          ref.componentInstance.createFormGroup();
          ref.result.then((corrector: CorrectorNuevo) => {
            this.editarCorrector(corrector, this.correctorElegido?.id ?? 0n);
          }, () => { console.log("Edición cancelada") });
        }
        break;
      case "Escape":
        this.correctorElegido = undefined;
        break;
      default:
        console.log("Tecla no reconocida");
      }
  }

  actualizaCorrectores(id?: bigint): void {
    this.correctorService.getCorrectores()
      .subscribe(correctores => {
        this.correctores = correctores;
        if (id) {
          this.correctorElegido = this.correctores.find(corrector => corrector.id === id);
        }
      });
  }

  elegirCorrector(corrector: Corrector): void {
    this.correctorElegido = corrector;
  }

  aniadirCorrector(): void {
    let ref = this.modalService.open(FormularioCorrectorComponent);
    ref.componentInstance.accion = "Añadir";
    ref.componentInstance.createFormGroup();
    ref.result.then((corrector: CorrectorNuevo) => {
      this.correctorService.addCorrector(corrector)
        .subscribe(c => {
          this.actualizaCorrectores(c.body?.id);
        })
    }, () => { console.log("Añadir cancelado") });
  }

  editarCorrector(corrector: CorrectorNuevo, id: bigint): void {
    this.correctorService.editCorrector(corrector, id)
      .subscribe(() => {
        this.actualizaCorrectores(id);
      });
  }

  eliminarCorrector(id: bigint): void {
    this.correctorService.deleteCorrector(id)
      .subscribe(() => {
        this.actualizaCorrectores();
      });
    this.correctorElegido = undefined;
  }

  // Aunque se visualicen instantáneamente los cambios en la lista de correctores,
  // el 'id' que se envia en la peticion que se hace al back para que se actualice
  // la lista de correctores es un valor entero, no un string, por lo que para buscar
  // un corrector hay que proporcionar el id de la convocatoria entero, no un prefijo
  // ej. para ID convoc. 125 buscamos pues 125, al escribir 1 o 12 no sale
  busqCorrectores() {
    this.correctorService.getCorrectores(this.idConvBusq).subscribe(correctores => {
      this.busquedaFallida = (correctores.length === 0);
      this.correctores = correctores;
      this.correctorElegido = (this.correctores.length > 0) ? this.correctores[0] : undefined;
    });
  }

}
