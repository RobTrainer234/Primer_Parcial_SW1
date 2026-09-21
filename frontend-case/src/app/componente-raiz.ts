import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { ServicioApiCase } from './nucleo/servicio-api-case';
import { EntidadModelo, Generacion, ModeloCompleto, Proyecto, ResultadoValidacion, TipoDato } from './nucleo/modelos-case';

@Component({
  selector: 'app-raiz',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './componente-raiz.html',
  styleUrl: './componente-raiz.css'
})
export class ComponenteRaiz implements OnInit {
  proyectos: Proyecto[] = [];
  proyectoActual?: Proyecto;
  modeloActual?: ModeloCompleto;
  entidadActual?: EntidadModelo;
  resultadoValidacion?: ResultadoValidacion;
  generacion?: Generacion;

  tiposDato: TipoDato[] = ['TEXTO', 'ENTERO', 'ENTERO_LARGO', 'DECIMAL', 'BOOLEANO', 'FECHA', 'FECHA_HORA'];

  formularioProyecto = { nombre: 'Clinica', descripcion: 'Modelo de prueba para la herramienta CASE' };
  formularioModelo = { nombre: 'Clinica' };
  formularioEntidad = { nombre: 'Paciente', posicionX: 100, posicionY: 100 };
  formularioAtributo = {
    nombre: 'id',
    tipoDato: 'ENTERO_LARGO' as TipoDato,
    clavePrimaria: true,
    obligatorio: true,
    valorUnico: true
  };

  cargando = false;
  mensaje = '';
  error = '';

  constructor(private readonly api: ServicioApiCase) {}

  ngOnInit(): void {
    this.cargarProyectos();
  }

  cargarProyectos(): void {
    this.ejecutar('Cargando proyectos...', () => {
      this.api.listarProyectos().subscribe({
        next: (proyectos) => {
          this.proyectos = proyectos;
          this.mensaje = proyectos.length ? 'Proyectos cargados.' : 'Todavia no hay proyectos.';
        },
        error: (error) => this.mostrarError(error)
      });
    });
  }

  crearProyecto(): void {
    this.ejecutar('Creando proyecto...', () => {
      this.api.crearProyecto(this.formularioProyecto).subscribe({
        next: (proyecto) => {
          this.proyectoActual = proyecto;
          this.proyectos = [proyecto, ...this.proyectos];
          this.modeloActual = undefined;
          this.entidadActual = undefined;
          this.resultadoValidacion = undefined;
          this.generacion = undefined;
          this.mensaje = `Proyecto creado: ${proyecto.nombre}`;
        },
        error: (error) => this.mostrarError(error)
      });
    });
  }

  seleccionarProyecto(proyecto: Proyecto): void {
    this.proyectoActual = proyecto;
    this.modeloActual = undefined;
    this.entidadActual = undefined;
    this.resultadoValidacion = undefined;
    this.generacion = undefined;
    this.mensaje = `Proyecto seleccionado: ${proyecto.nombre}`;
  }

  crearModelo(): void {
    if (!this.proyectoActual) {
      this.error = 'Primero selecciona o crea un proyecto.';
      return;
    }
    this.ejecutar('Creando modelo...', () => {
      this.api.crearModelo(this.proyectoActual!.id, this.formularioModelo).subscribe({
        next: (modelo) => this.refrescarModelo(modelo.id, `Modelo creado: ${modelo.nombre}`),
        error: (error) => this.mostrarError(error)
      });
    });
  }

  crearEntidad(): void {
    if (!this.modeloActual) {
      this.error = 'Primero crea un modelo conceptual.';
      return;
    }
    this.ejecutar('Creando entidad...', () => {
      this.api.crearEntidad(this.modeloActual!.id, this.formularioEntidad).subscribe({
        next: (entidad) => {
          this.entidadActual = entidad;
          this.refrescarModelo(this.modeloActual!.id, `Entidad creada: ${entidad.nombre}`);
        },
        error: (error) => this.mostrarError(error)
      });
    });
  }

  seleccionarEntidad(entidad: EntidadModelo): void {
    this.entidadActual = entidad;
    this.mensaje = `Entidad seleccionada: ${entidad.nombre}`;
  }

  crearAtributo(): void {
    if (!this.entidadActual || !this.modeloActual) {
      this.error = 'Primero selecciona una entidad.';
      return;
    }
    this.ejecutar('Creando atributo...', () => {
      this.api.crearAtributo(this.entidadActual!.id, this.formularioAtributo).subscribe({
        next: (atributo) => {
          this.refrescarModelo(this.modeloActual!.id, `Atributo creado: ${atributo.nombre}`);
          if (this.formularioAtributo.clavePrimaria) {
            this.formularioAtributo = {
              nombre: 'nombre',
              tipoDato: 'TEXTO',
              clavePrimaria: false,
              obligatorio: true,
              valorUnico: false
            };
          }
        },
        error: (error) => this.mostrarError(error)
      });
    });
  }

  validarModelo(): void {
    if (!this.modeloActual) {
      this.error = 'Primero crea un modelo conceptual.';
      return;
    }
    this.ejecutar('Validando modelo...', () => {
      this.api.validarModelo(this.modeloActual!.id).subscribe({
        next: (resultado) => {
          this.resultadoValidacion = resultado;
          this.mensaje = resultado.valido ? 'Modelo valido para generar.' : 'El modelo tiene errores.';
        },
        error: (error) => this.mostrarError(error)
      });
    });
  }

  generarBackend(): void {
    if (!this.modeloActual) {
      this.error = 'Primero crea un modelo conceptual.';
      return;
    }
    this.ejecutar('Generando backend...', () => {
      this.api.generarBackend(this.modeloActual!.id).subscribe({
        next: (generacion) => {
          this.generacion = generacion;
          this.mensaje = generacion.estado === 'COMPLETADO'
            ? 'Backend generado correctamente.'
            : `Generacion finalizada con estado ${generacion.estado}.`;
        },
        error: (error) => this.mostrarError(error)
      });
    });
  }

  descargarArtefacto(): void {
    if (!this.generacion || this.generacion.estado !== 'COMPLETADO') {
      this.error = 'No hay una generacion completada para descargar.';
      return;
    }
    window.open(this.api.urlDescargaArtefacto(this.generacion.id), '_blank');
  }

  private refrescarModelo(modeloId: number, mensaje: string): void {
    this.api.obtenerModelo(modeloId).subscribe({
      next: (modelo) => {
        this.modeloActual = modelo;
        if (this.entidadActual) {
          this.entidadActual = modelo.entidades.find((entidad) => entidad.id === this.entidadActual?.id) ?? this.entidadActual;
        }
        this.resultadoValidacion = undefined;
        this.generacion = undefined;
        this.mensaje = mensaje;
      },
      error: (error) => this.mostrarError(error)
    });
  }

  private ejecutar(mensaje: string, operacion: () => void): void {
    this.cargando = true;
    this.error = '';
    this.mensaje = mensaje;
    operacion();
    setTimeout(() => this.cargando = false, 250);
  }

  private mostrarError(error: unknown): void {
    this.cargando = false;
    const posible = error as { error?: { mensaje?: string }, message?: string };
    this.error = posible.error?.mensaje ?? posible.message ?? 'Ocurrio un error inesperado.';
  }
}
