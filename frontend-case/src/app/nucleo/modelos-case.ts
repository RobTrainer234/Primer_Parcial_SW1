export type TipoDato = 'TEXTO' | 'ENTERO' | 'ENTERO_LARGO' | 'DECIMAL' | 'BOOLEANO' | 'FECHA' | 'FECHA_HORA';
export type Cardinalidad = 'UNO' | 'MUCHOS';
export type TipoRelacion = 'ASOCIACION' | 'AGREGACION' | 'COMPOSICION' | 'GENERALIZACION' | 'DEPENDENCIA' | 'REALIZACION';
export type EstadoGeneracion = 'EN_PROCESO' | 'COMPLETADO' | 'FALLIDO';
export type VisibilidadOperacion = 'PUBLICA' | 'PROTEGIDA' | 'PRIVADA' | 'PAQUETE';

export interface Proyecto {
  id: number;
  nombre: string;
  descripcion?: string;
  estado: string;
  creadoEn: string;
  actualizadoEn: string;
}

export type RolProyecto = 'OWNER' | 'ADMIN' | 'EDITOR' | 'VIEWER' | string;
export type EstadoMiembroProyecto = 'ACTIVE' | 'INACTIVE' | 'SUSPENDED' | string;
export type TipoComandoProyecto = 'INVITE_MEMBER' | 'ACCEPT_INVITATION' | 'REJECT_INVITATION' | 'REVOKE_INVITATION' | string;

export interface SolicitudComandoProyecto {
  type: TipoComandoProyecto;
  payload?: Record<string, unknown>;
}

export interface RespuestaComandoProyecto {
  tipo: string;
  estado: string;
  actor: string;
  registradoEn: string;
  detalle: string;
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
  semantica?: string;
}

export interface EntidadModelo {
  id: number;
  nombre: string;
  posicionX: number;
  posicionY: number;
  atributos: AtributoEntidad[];
  operaciones?: string[];
  detalleOperaciones?: OperacionEntidad[];
}

export interface OperacionEntidad {
  id: number;
  nombre: string;
  tipoRetorno: string;
  firma: string;
  visibilidad: VisibilidadOperacion;
  texto: string;
  creadoEn: string;
  actualizadoEn: string;
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
  verbo?: string;
  tipo: TipoRelacion;
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
  perfil?: 'UML_2_5_CLASES_SUBCONJUNTO' | string;
  reglasAplicadas?: string[];
}

export interface Generacion {
  id: number;
  modeloId: number;
  estado: EstadoGeneracion;
  iniciadoEn: string;
  finalizadoEn?: string;
  mensajeError?: string;
  target?: string;
  perfil?: string;
  artifactHash?: string;
}

export interface SesionDemo {
  usuarioId: string;
  nombreVisible: string;
  tokenDemo: string;
  demo: boolean;
  advertencia: string;
}

export interface SnapshotModelo {
  modeloId: number;
  proyectoId: number;
  revision: number;
  modelo: ModeloCompleto;
  layout: unknown;
  origen: string;
  generadoEn: string;
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

export interface SolicitudOperacion {
  nombre: string;
  tipoRetorno: string;
  firma: string;
  visibilidad: VisibilidadOperacion;
}

export interface SolicitudRelacion {
  entidadOrigenId: number;
  entidadDestinoId: number;
  nombre: string;
  verbo?: string;
  tipo?: TipoRelacion;
  cardinalidadOrigen: Cardinalidad;
  cardinalidadDestino: Cardinalidad;
}

export interface MiembroProyecto {
  userId: string;
  displayName: string;
  role: string;
  canCreateDiagram: boolean;
  active: boolean;
  updatedAt: string;
  status?: string;
  canEditModel?: boolean;
  canManageMembers?: boolean;
  canManagePermissions?: boolean;
}

export interface InvitacionProyecto {
  id: string;
  email: string;
  role: string;
  status: string;
  createdAt: string;
  userId?: string;
  inviterUserId?: string;
  token?: string;
  expiresAt?: string;
}

export interface SugerenciaCuenta {
  userId: string;
  displayName: string;
  email: string;
  alreadyMember: boolean;
}

export interface EventoPermiso {
  id: number;
  type: string;
  actor: string;
  viewId?: number;
  detail: string;
  occurredAt: string;
}

export interface DiagramaProyecto {
  id: number;
  projectId: number;
  name: string;
  administratorUserId: string;
  createdAt: string;
}

export interface AccesoDiagrama {
  userId: string;
  role: string;
  canEdit: boolean;
  canComment: boolean;
  updatedAt: string;
}

export interface RespuestaAccesoDiagrama {
  diagram: DiagramaProyecto;
  collaborators: AccesoDiagrama[];
}

export interface PresenciaProyecto {
  projectId: number;
  modelId: number;
  userId: string;
  displayName: string;
  status: string;
  cursor: unknown;
  seenAt: string;
}

export interface ConflictoSync {
  operationId: string;
  projectId: number;
  modelId: number;
  baseRevision: number;
  currentRevision: number;
  status: string;
  detail: string;
  commandPayload: unknown;
  resolution?: string;
  resolvedAt?: string;
  createdAt: string;
}

export interface PropuestaModelo {
  id: number;
  projectId: number;
  title: string;
  text: string;
  source: string;
  sourceStatus: string;
  metadata: unknown;
  status: string;
  reviewer?: string;
  reviewNote: string;
  createdAt: string;
  updatedAt: string;
}

export interface RevisionPropuesta {
  proposalId: number;
  projectId: number;
  status: string;
  checklist: string[];
  sourceStatus: string;
  note: string;
}

export interface ExportacionXmi {
  modelId: number;
  modelName: string;
  format: string;
  lossless: boolean;
  limitation: string;
  xmi: string;
}

export interface VistaPreviaXmi {
  previewToken: string;
  modelId: number;
  lossless: boolean;
  limitation: string;
  warnings: string[];
  entities: Array<{ nombre: string; posicionX: number; posicionY: number; atributos: unknown[] }>;
  relations: unknown[];
  createdAt: string;
}

export interface ConfirmacionXmi {
  modelId: number;
  applied: boolean;
  entitiesCreated: number;
  attributesCreated: number;
  relationsCreated: number;
  limitation: string;
  warnings: string[];
}

export interface DeploymentDemo {
  id: number;
  projectId: number;
  name: string;
  environment: string;
  target: string;
  enabled: boolean;
  status: string;
  limitation: string;
  createdAt: string;
  updatedAt: string;
}

export interface EstadoAlmacenamiento {
  activeProvider: string;
  status: string;
  limitation: string;
  providers: Array<{ id: string; selected: boolean; configured: boolean; detail: string }>;
}
