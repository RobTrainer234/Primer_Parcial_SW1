export type TipoDato = 'TEXTO' | 'ENTERO' | 'ENTERO_LARGO' | 'DECIMAL' | 'BOOLEANO' | 'FECHA' | 'FECHA_HORA';
export type Cardinalidad = 'UNO' | 'MUCHOS';
export type EstadoGeneracion = 'EN_PROCESO' | 'COMPLETADO' | 'FALLIDO';

export interface Proyecto {
  id: number;
  nombre: string;
  descripcion?: string;
  estado: string;
  creadoEn: string;
  actualizadoEn: string;
}

export interface ModeloConceptual {
  id: number;
  proyectoId: number;
  nombre: string;
  version: number;
}

export interface ModeloCompleto extends ModeloConceptual {
  entidades: EntidadModelo[];
  relaciones: RelacionModelo[];
}

export interface EntidadModelo {
  id: number;
  nombre: string;
  posicionX: number;
  posicionY: number;
  atributos: AtributoEntidad[];
}

export interface AtributoEntidad {
  id: number;
  nombre: string;
  tipoDato: TipoDato;
  clavePrimaria: boolean;
  obligatorio: boolean;
  valorUnico: boolean;
}

export interface RelacionModelo {
  id: number;
  entidadOrigenId: number;
  entidadDestinoId: number;
  nombre: string;
  cardinalidadOrigen: Cardinalidad;
  cardinalidadDestino: Cardinalidad;
}

export interface ErrorValidacion {
  codigo: string;
  mensaje: string;
  destino: string;
}

export interface ResultadoValidacion {
  valido: boolean;
  errores: ErrorValidacion[];
}

export interface Generacion {
  id: number;
  modeloId: number;
  estado: EstadoGeneracion;
  iniciadoEn: string;
  finalizadoEn?: string;
  mensajeError?: string;
}

export interface SolicitudProyecto {
  nombre: string;
  descripcion?: string;
}

export interface SolicitudModelo {
  nombre: string;
}

export interface SolicitudEntidad {
  nombre: string;
  posicionX: number;
  posicionY: number;
}

export interface SolicitudAtributo {
  nombre: string;
  tipoDato: TipoDato;
  clavePrimaria: boolean;
  obligatorio: boolean;
  valorUnico: boolean;
}
