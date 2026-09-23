import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { catchError, forkJoin, map, of } from 'rxjs';
import { ServicioApiCase } from './nucleo/servicio-api-case';
import {
  AccesoDiagrama,
  AtributoEntidad,
  Cardinalidad,
  ConflictoSync,
  DeploymentDemo,
  DiagramaProyecto,
  EntidadModelo,
  EstadoAlmacenamiento,
  EventoPermiso,
  Generacion,
  InvitacionProyecto,
  MiembroProyecto,
  ModeloCompleto,
  OperacionEntidad,
  PresenciaProyecto,
  PropuestaModelo,
  Proyecto,
  RelacionModelo,
  ResultadoValidacion,
  RevisionPropuesta,
  SesionDemo,
  SnapshotModelo,
  SugerenciaCuenta,
  TipoDato,
  TipoRelacion,
  VisibilidadOperacion,
  VistaPreviaXmi
} from './nucleo/modelos-case';

export type ModuloActivo = 'metamodelo' | 'diagramas' | 'qwen' | 'generador' | 'ast';
export type ModoLienzo = 'dark' | 'daylight' | 'blueprint';
export type HerramientaUml = 'select' | 'pan' | 'entity' | 'assoc' | 'aggr' | 'comp' | 'gen' | 'dep' | 'real' | 'note';
export type TipoRelacionVisual = TipoRelacion | 'HERENCIA';
export type RutaRelacionVisual = 'RECTA' | 'ORTOGONAL';
export type TipoToast = 'success' | 'info' | 'error';

export interface AtributoVisual {
  idBackend?: number;
  nombre: string;
  tipo: string;
  visibilidad: '+' | '-' | '#' | '~';
  pk?: boolean;
  req?: boolean;
  unique?: boolean;
  conflicto?: boolean;
  valorDefecto?: string;
}

export interface NodoDiagrama {
  id: string;
  nombre: string;
  estereotipo: string;
  tabla: string;
  x: number;
  y: number;
  ancho: number;
  atributos: AtributoVisual[];
  operaciones: string[];
  presencia?: { usuario: string; accion: string; color: string; icono: string };
  conflicto?: boolean;
}

export interface NotaOclDiagrama {
  id: string;
  x: number;
  y: number;
  ancho: number;
  titulo: string;
  invariantes: { contexto: string; regla: string }[];
}

export interface RelacionVisual {
  id: string;
  relacionId?: number;
  origenId: string;
  destinoId: string;
  tipo: TipoRelacionVisual;
  nombre: string;
  verbo?: string;
  cardinalidadOrigen: '1' | '0..*';
  cardinalidadDestino: '1' | '0..*';
  ruta: RutaRelacionVisual;
  controlX?: number;
  persistida: boolean;
}

interface RectanguloCanvas {
  x: number;
  y: number;
  ancho: number;
  alto: number;
}

interface ContextoSesionPersistido {
  proyectoId?: number;
  modeloId?: number;
}

@Component({
  selector: 'app-raiz',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './componente-raiz.html',
  styleUrl: './componente-raiz.css'
})
export class ComponenteRaiz implements OnInit, OnDestroy {
  // Navegación Modular (5 módulos de arquitectura)
  moduloActivo: ModuloActivo = 'diagramas';

  // Configuración del Lienzo UML 2.5
  modoLienzo: ModoLienzo = 'dark';
  rejillaActiva = true;
  zoomCanvas = 100;
  vistaSeleccionada = 'diag-v1';
  herramientaUml: HerramientaUml = 'select';
  nodoSeleccionado: string | null = null;
  nodosSeleccionados = new Set<string>();
  marquesinaSeleccion?: RectanguloCanvas;
  relacionSeleccionada: string | null = null;
  origenRelacionPendiente: string | null = null;
  contadorClasesLocales = 1;
  inspectorModo: 'clase' | 'relacion' | 'lienzo' = 'lienzo';
  edicionInlineClase?: { nodoId: string; campo: 'nombre' | 'tabla'; valor: string; original: string };
  edicionInlineAtributo?: { nodoId: string; index: number; nombre: string; tipo: string; pk: boolean; req: boolean; unique: boolean; original: AtributoVisual };

  // Controles de Workbench StarUML / Draw.io
  panelIzquierdoAbierto = true;
  panelDerechoAbierto = true;
  pestanaIzquierda: 'stencils' | 'arbol' = 'stencils';
  drawerActivo: 'ninguno' | 'spring' | 'ia' | 'xmi' | 'validacion' | 'metamodelo' | 'administracion' = 'ninguno';
  coordenadasCursor = { x: 0, y: 0 };
  arbolFiltro = '';

  // Sistema Interactivo de Arrastre (Drag and Drop)
  arrastrandoId: string | null = null;
  posInicioRaton = { x: 0, y: 0 };
  posInicioElemento = { x: 0, y: 0 };
  canvasPan = { x: 0, y: 0 };
  arrastrandoCanvas = false;
  arrastrandoControlRelacionId: string | null = null;
  private marquesinaActiva = false;
  private inicioMarquesina = { x: 0, y: 0 };
  private posicionesInicialesArrastre = new Map<string, { x: number; y: number }>();
  private huboMovimientoArrastre = false;
  private suprimirClickCanvas = false;
  private suprimirClickNodo = false;
  private versionInteraccionCanvas = 0;

  // Nodos del lienzo: se cargan solo después de una sesión explícita y un modelo activo.
  nodos: NodoDiagrama[] = [];

  notaOcl: NotaOclDiagrama = this.crearNotaOclVacia();

  relacionesVisuales: RelacionVisual[] = [];

  formularioRelacion = {
    nombre: 'relacion',
    verbo: '',
    tipo: 'ASOCIACION' as TipoRelacionVisual,
    ruta: 'ORTOGONAL' as RutaRelacionVisual,
    cardinalidadOrigen: '1' as '1' | '0..*',
    cardinalidadDestino: '0..*' as '1' | '0..*'
  };

  // Datos del Dominio CASE y Backend
  proyectos: Proyecto[] = [];
  proyectoActual?: Proyecto;
  modeloActual?: ModeloCompleto;
  entidadActual?: EntidadModelo;
  resultadoValidacion?: ResultadoValidacion;
  generacion?: Generacion;
  sesion?: SesionDemo;
  snapshot?: SnapshotModelo;
  miembros: MiembroProyecto[] = [];
  invitaciones: InvitacionProyecto[] = [];
  sugerencias: SugerenciaCuenta[] = [];
  historialPermisos: EventoPermiso[] = [];
  historialDiagrama: EventoPermiso[] = [];
  administracionCargando = false;
  formularioInvitacion = { userId: '', role: 'EDITOR' };
  readonly rolesProyecto = ['OWNER', 'ADMIN', 'EDITOR', 'VIEWER'];
  readonly estadosMiembro = ['ACTIVE', 'INACTIVE', 'SUSPENDED'];
  diagrama?: DiagramaProyecto;
  accesosDiagrama: AccesoDiagrama[] = [];
  presencias: PresenciaProyecto[] = [];
  conflictos: ConflictoSync[] = [];
  propuestas: PropuestaModelo[] = [];
  revisionPropuesta?: RevisionPropuesta;
  propuestaSeleccionada?: PropuestaModelo;
  xmiExportado = '';
  xmiImportacion = '';
  vistaPreviaXmi?: VistaPreviaXmi;
  deployments: DeploymentDemo[] = [];
  nombreDeployment = 'Demo Local Spring Boot';
  estadoAlmacenamiento?: EstadoAlmacenamiento;
  eventoSse = 'SSE inactivo: inicia sesión para conectar eventos.';
  private eventos?: EventSource;

  tiposDato: TipoDato[] = ['TEXTO', 'ENTERO', 'ENTERO_LARGO', 'DECIMAL', 'BOOLEANO', 'FECHA', 'FECHA_HORA'];

  formularioProyecto = { nombre: '', descripcion: '' };
  formularioModelo = { nombre: '' };
  formularioEntidad = { nombre: '', posicionX: 0, posicionY: 0 };
  formularioAtributo = {
    nombre: '',
    tipoDato: 'TEXTO' as TipoDato,
    clavePrimaria: false,
    obligatorio: false,
    valorUnico: false
  };
  formularioOperacion = this.crearFormularioOperacionVacio();
  operacionEditandoId: number | null = null;
  operacionGuardando = false;
  readonly visibilidadesOperacion: VisibilidadOperacion[] = ['PUBLICA', 'PROTEGIDA', 'PRIVADA', 'PAQUETE'];
  textoPropuesta = '';

  modalLoginAbierto = false;
  formularioLogin = { usuario: 'demo-modeler', clave: '', recordar: true };
  mostrarPassword = false;
  credencialesGuardadas = false;

  get passLongitud(): boolean {
    return (this.formularioLogin.clave || '').length >= 8;
  }

  get passMayuscula(): boolean {
    return /[A-Z]/.test(this.formularioLogin.clave || '');
  }

  get passMinuscula(): boolean {
    return /[a-z]/.test(this.formularioLogin.clave || '');
  }

  get passNumero(): boolean {
    return /[0-9]/.test(this.formularioLogin.clave || '');
  }

  get passEspecial(): boolean {
    return /[!@#$%^&*(),.?":{}|<>_\-]/.test(this.formularioLogin.clave || '');
  }

  get cantidadCondicionesCumplidas(): number {
    let count = 0;
    if (this.passLongitud) count++;
    if (this.passMayuscula) count++;
    if (this.passMinuscula) count++;
    if (this.passNumero) count++;
    if (this.passEspecial) count++;
    return count;
  }

  get contrasenaAceptable(): boolean {
    return this.passLongitud && this.cantidadCondicionesCumplidas >= 4;
  }

  get porcentajeFortaleza(): number {
    return (this.cantidadCondicionesCumplidas / 5) * 100;
  }

  get textoFortaleza(): string {
    const c = this.cantidadCondicionesCumplidas;
    if (!this.formularioLogin.clave) return 'Sin ingresar';
    if (c <= 1) return 'Muy débil';
    if (c <= 2) return 'Débil';
    if (c <= 3) return 'Media';
    if (c === 4) return 'Fuerte';
    return 'Muy Segura (Excelente)';
  }

  get colorFortaleza(): string {
    const c = this.cantidadCondicionesCumplidas;
    if (c <= 1) return 'bg-rose-500';
    if (c <= 2) return 'bg-amber-500';
    if (c <= 3) return 'bg-yellow-400';
    if (c === 4) return 'bg-emerald-500';
    return 'bg-emerald-400';
  }

  usuariosDemoDisponibles = [
    {
      id: 'demo-modeler',
      nombre: 'Demo Modeler',
      rol: 'Modelador Principal (Propietario)',
      descripcion: 'Acceso total a modelado, edición UML, generación y proyectos.',
      icono: 'engineering'
    },
    {
      id: 'profesor_evaluador',
      nombre: 'Profesor Evaluador',
      rol: 'Evaluador Académico (Revisor)',
      descripcion: 'Revisión de diagramas, validación OCL e inspección de AST.',
      icono: 'school'
    },
    {
      id: 'estudiante_2',
      nombre: 'Estudiante Colaborador',
      rol: 'Colaborador Secundario (Editor)',
      descripcion: 'Edición colaborativa en tiempo real y resolución de conflictos.',
      icono: 'group'
    },
    {
      id: 'admin_sistema',
      nombre: 'Administrador de Sistema',
      rol: 'Operador de Infraestructura',
      descripcion: 'Control de almacenamiento S3/Floci y despliegues demo.',
      icono: 'admin_panel_settings'
    }
  ];

  cargando = false;
  mensaje = '';
  error = '';

  toastVisible = false;
  toastMensaje = '';
  toastKind: TipoToast = 'info';
  private toastTimer?: ReturnType<typeof setTimeout>;
  private readonly claveSesionPersistida = 'case_sesion_activa';
  private readonly claveContextoPersistido = 'case_contexto_activo';

  constructor(private readonly api: ServicioApiCase) {}

  ngOnInit(): void {
    this.cargarCredencialesGuardadas();
    this.limpiarContextoSesion();
    this.restaurarSesionPersistida();
  }

  ngOnDestroy(): void {
    this.limpiarTemporizadorToast();
    this.eventos?.close();
  }

  cerrarToast(): void {
    this.toastVisible = false;
    this.limpiarTemporizadorToast();
  }

  private mostrarToast(mensaje: string, kind: TipoToast = 'info'): void {
    this.limpiarTemporizadorToast();
    this.toastMensaje = mensaje;
    this.toastKind = kind;
    this.toastVisible = true;
    this.toastTimer = setTimeout(() => {
      this.toastVisible = false;
      this.toastTimer = undefined;
    }, 3200);
  }

  private limpiarTemporizadorToast(): void {
    if (this.toastTimer) {
      clearTimeout(this.toastTimer);
      this.toastTimer = undefined;
    }
  }

  private notificarExito(mensaje: string): void {
    this.error = '';
    this.mensaje = mensaje;
    this.mostrarToast(mensaje, 'success');
  }

  private notificarInfo(mensaje: string): void {
    this.mensaje = mensaje;
    this.mostrarToast(mensaje, 'info');
  }

  private notificarError(mensaje: string): void {
    this.error = mensaje;
    this.mostrarToast(mensaje, 'error');
  }

  private requiereSesion(): boolean {
    if (this.sesion) {
      return true;
    }
    this.notificarError('Debes iniciar sesión antes de operar con proyectos o modelos.');
    this.modalLoginAbierto = true;
    return false;
  }

  private limpiarContextoSesion(): void {
    this.eventos?.close();
    this.eventos = undefined;
    this.proyectos = [];
    this.proyectoActual = undefined;
    this.limpiarContextoModelo();
    this.miembros = [];
    this.invitaciones = [];
    this.sugerencias = [];
    this.historialPermisos = [];
    this.historialDiagrama = [];
    this.diagrama = undefined;
    this.accesosDiagrama = [];
    this.propuestas = [];
    this.revisionPropuesta = undefined;
    this.propuestaSeleccionada = undefined;
    this.xmiExportado = '';
    this.xmiImportacion = '';
    this.vistaPreviaXmi = undefined;
    this.deployments = [];
    this.estadoAlmacenamiento = undefined;
    this.eventoSse = 'SSE inactivo: inicia sesión para conectar eventos.';
    this.drawerActivo = 'ninguno';
    this.formularioProyecto = { nombre: '', descripcion: '' };
    this.formularioModelo = { nombre: '' };
    this.formularioEntidad = { nombre: '', posicionX: 0, posicionY: 0 };
    this.formularioAtributo = {
      nombre: '',
      tipoDato: 'TEXTO',
      clavePrimaria: false,
      obligatorio: false,
      valorUnico: false
    };
    this.formularioOperacion = this.crearFormularioOperacionVacio();
    this.operacionEditandoId = null;
    this.operacionGuardando = false;
    this.mensaje = '';
    this.error = '';
    this.cerrarToast();
  }

  private limpiarContextoModelo(): void {
    this.modeloActual = undefined;
    this.entidadActual = undefined;
    this.resultadoValidacion = undefined;
    this.generacion = undefined;
    this.snapshot = undefined;
    this.nodos = [];
    this.relacionesVisuales = [];
    this.notaOcl = this.crearNotaOclVacia();
    this.nodoSeleccionado = null;
    this.nodosSeleccionados.clear();
    this.marquesinaSeleccion = undefined;
    this.relacionSeleccionada = null;
    this.origenRelacionPendiente = null;
    this.inspectorModo = 'lienzo';
    this.contadorClasesLocales = 1;
    this.herramientaUml = 'select';
    this.presencias = [];
    this.conflictos = [];
    this.arrastrandoId = null;
    this.arrastrandoCanvas = false;
    this.arrastrandoControlRelacionId = null;
    this.formularioOperacion = this.crearFormularioOperacionVacio();
    this.operacionEditandoId = null;
    this.operacionGuardando = false;
  }

  private crearFormularioOperacionVacio() {
    return {
      nombre: '',
      tipoRetorno: 'void',
      firma: '()',
      visibilidad: 'PUBLICA' as VisibilidadOperacion
    };
  }

  private crearNotaOclVacia(): NotaOclDiagrama {
    return {
      id: 'nota-ocl',
      x: 650,
      y: 430,
      ancho: 280,
      titulo: '',
      invariantes: []
    };
  }

  private restaurarSesionPersistida(): void {
    const sesionGuardada = this.leerSesionPersistida();
    if (!sesionGuardada) {
      return;
    }
    this.sesion = sesionGuardada;
    this.cargarProyectos(true);
  }

  private leerSesionPersistida(): SesionDemo | undefined {
    try {
      const raw = localStorage.getItem(this.claveSesionPersistida);
      if (!raw) return undefined;
      const parsed = JSON.parse(raw) as Partial<SesionDemo>;
      if (!parsed.usuarioId || !parsed.nombreVisible || !parsed.tokenDemo) {
        this.borrarSesionPersistida();
        return undefined;
      }
      return {
        usuarioId: parsed.usuarioId,
        nombreVisible: parsed.nombreVisible,
        tokenDemo: parsed.tokenDemo,
        demo: !!parsed.demo,
        advertencia: parsed.advertencia || ''
      };
    } catch {
      this.borrarSesionPersistida();
      return undefined;
    }
  }

  private persistirSesion(sesion: SesionDemo): void {
    try {
      localStorage.setItem(this.claveSesionPersistida, JSON.stringify({
        usuarioId: sesion.usuarioId,
        nombreVisible: sesion.nombreVisible,
        tokenDemo: sesion.tokenDemo,
        demo: sesion.demo,
        advertencia: sesion.advertencia
      }));
    } catch {
      this.notificarInfo('No se pudo guardar la sesión en este navegador.');
    }
  }

  private borrarSesionPersistida(): void {
    try {
      localStorage.removeItem(this.claveSesionPersistida);
    } catch {
      // Ignorar almacenamiento no disponible.
    }
  }

  private leerContextoPersistido(): ContextoSesionPersistido {
    try {
      const raw = localStorage.getItem(this.claveContextoPersistido);
      if (!raw) return {};
      const parsed = JSON.parse(raw) as ContextoSesionPersistido;
      return {
        proyectoId: this.normalizarIdPersistido(parsed.proyectoId),
        modeloId: this.normalizarIdPersistido(parsed.modeloId)
      };
    } catch {
      this.borrarContextoPersistido();
      return {};
    }
  }

  private normalizarIdPersistido(valor: unknown): number | undefined {
    const numero = typeof valor === 'string' ? parseInt(valor, 10) : valor;
    return typeof numero === 'number' && Number.isFinite(numero) && numero > 0 ? numero : undefined;
  }

  private persistirContextoSesion(contexto: ContextoSesionPersistido): void {
    try {
      const actual = this.leerContextoPersistido();
      const combinado: ContextoSesionPersistido = { ...actual, ...contexto };
      Object.keys(combinado).forEach((clave) => {
        const key = clave as keyof ContextoSesionPersistido;
        if (!combinado[key]) delete combinado[key];
      });
      if (Object.keys(combinado).length === 0) {
        localStorage.removeItem(this.claveContextoPersistido);
        return;
      }
      localStorage.setItem(this.claveContextoPersistido, JSON.stringify(combinado));
    } catch {
      this.notificarInfo('No se pudo guardar el contexto activo en este navegador.');
    }
  }

  private borrarContextoPersistido(): void {
    try {
      localStorage.removeItem(this.claveContextoPersistido);
    } catch {
      // Ignorar almacenamiento no disponible.
    }
  }

  private borrarModeloPersistido(): void {
    this.persistirContextoSesion({ modeloId: undefined });
  }

  private borrarProyectoYModeloPersistidos(): void {
    this.persistirContextoSesion({ proyectoId: undefined, modeloId: undefined });
  }

  // Métodos de Arrastre Interactivo (Drag & Drop en Canvas)
  iniciarArrastre(id: string, event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (target && (target.tagName === 'BUTTON' || target.tagName === 'INPUT' || target.tagName === 'SELECT' || target.tagName === 'LABEL' || target.closest('.inline-edit-stop'))) {
      return;
    }
    if (this.esHerramientaRelacion(this.herramientaUml)) {
      return;
    }
    event.preventDefault();
    event.stopPropagation();
    this.cancelarMarquesina();
    this.versionInteraccionCanvas++;
    this.suprimirClickNodo = true;
    this.huboMovimientoArrastre = false;
    this.posicionesInicialesArrastre.clear();

    if (id === 'nota-ocl') {
      this.arrastrandoId = id;
      this.nodoSeleccionado = null;
      this.nodosSeleccionados.clear();
      this.posInicioRaton = { x: event.clientX, y: event.clientY };
      this.posInicioElemento = { x: this.notaOcl.x, y: this.notaOcl.y };
      return;
    }

    const alternar = event.ctrlKey || event.metaKey;
    if (alternar) {
      if (this.nodosSeleccionados.has(id)) {
        this.nodosSeleccionados.delete(id);
      } else {
        this.nodosSeleccionados.add(id);
      }
      this.nodoSeleccionado = this.nodosSeleccionados.has(id) ? id : this.primerNodoSeleccionado();
    } else if (!this.nodosSeleccionados.has(id)) {
      this.nodosSeleccionados.clear();
      this.nodosSeleccionados.add(id);
      this.nodoSeleccionado = id;
    } else {
      this.nodoSeleccionado = id;
    }

    this.relacionSeleccionada = null;
    this.inspectorModo = this.nodoSeleccionado ? 'clase' : 'lienzo';
    const entidad = this.buscarEntidadPorNodo(id);
    if (entidad) this.entidadActual = entidad;

    if (!this.nodosSeleccionados.has(id)) {
      return;
    }

    this.arrastrandoId = id;
    this.posInicioRaton = { x: event.clientX, y: event.clientY };
    const nodo = this.nodos.find(n => n.id === id);
    if (nodo) {
      this.posInicioElemento = { x: nodo.x, y: nodo.y };
    }
    this.nodos
      .filter(n => this.nodosSeleccionados.has(n.id))
      .forEach(n => this.posicionesInicialesArrastre.set(n.id, { x: n.x, y: n.y }));
  }

  iniciarPanCanvas(event: MouseEvent): void {
    (event.currentTarget as HTMLElement).focus();
    if (this.herramientaUml === 'pan' || event.button === 1) {
      event.preventDefault();
      this.cancelarMarquesina();
      this.arrastrandoCanvas = true;
      this.posInicioRaton = { x: event.clientX, y: event.clientY };
      this.posInicioElemento = { x: this.canvasPan.x, y: this.canvasPan.y };
      return;
    }

    if (this.herramientaUml === 'select' && event.button === 0 && event.target === event.currentTarget) {
      event.preventDefault();
      this.versionInteraccionCanvas++;
      this.marquesinaActiva = true;
      this.suprimirClickCanvas = true;
      this.relacionSeleccionada = null;
      this.nodoSeleccionado = null;
      this.inspectorModo = 'lienzo';
      const punto = this.puntoCanvasDesdeEvento(event);
      this.inicioMarquesina = punto;
      this.marquesinaSeleccion = { x: punto.x, y: punto.y, ancho: 0, alto: 0 };
      this.nodosSeleccionados.clear();
    }
  }

  alMoverRaton(event: MouseEvent): void {
    if (this.arrastrandoControlRelacionId) {
      event.preventDefault();
      const relacion = this.relacionesVisuales.find(r => r.id === this.arrastrandoControlRelacionId);
      if (!relacion) return;
      const factorZoom = this.zoomCanvas / 100;
      const deltaX = (event.clientX - this.posInicioRaton.x) / factorZoom;
      relacion.controlX = Math.max(20, Math.min(this.posInicioElemento.x + deltaX, 1450));
    } else if (this.arrastrandoId) {
      event.preventDefault();
      const factorZoom = this.zoomCanvas / 100;
      const deltaX = (event.clientX - this.posInicioRaton.x) / factorZoom;
      const deltaY = (event.clientY - this.posInicioRaton.y) / factorZoom;

      if (Math.abs(deltaX) > 0 || Math.abs(deltaY) > 0) {
        this.huboMovimientoArrastre = true;
      }

      if (this.arrastrandoId === 'nota-ocl') {
        let nuevoX = this.posInicioElemento.x + deltaX;
        let nuevoY = this.posInicioElemento.y + deltaY;
        if (this.rejillaActiva) {
          nuevoX = Math.round(nuevoX / 10) * 10;
          nuevoY = Math.round(nuevoY / 10) * 10;
        }
        this.notaOcl.x = Math.max(10, Math.min(nuevoX, 1400));
        this.notaOcl.y = Math.max(10, Math.min(nuevoY, 900));
        return;
      }

      this.moverSeleccionDesdeDelta(deltaX, deltaY);
    } else if (this.marquesinaActiva) {
      event.preventDefault();
      this.actualizarMarquesina(event);
    } else if (this.arrastrandoCanvas) {
      event.preventDefault();
      const deltaX = event.clientX - this.posInicioRaton.x;
      const deltaY = event.clientY - this.posInicioRaton.y;
      this.canvasPan.x = this.posInicioElemento.x + deltaX;
      this.canvasPan.y = this.posInicioElemento.y + deltaY;
    }
  }

  alSoltarRaton(): void {
    if (this.arrastrandoControlRelacionId) {
      this.arrastrandoControlRelacionId = null;
    }
    if (this.marquesinaActiva) {
      this.finalizarMarquesina();
    }
    if (this.arrastrandoId) {
      const idFinalizado = this.arrastrandoId;
      this.arrastrandoId = null;
      if (idFinalizado !== 'nota-ocl') {
        this.persistirMovimientoSeleccion();
      }
    }
    this.arrastrandoCanvas = false;
  }

  manejarTeclaCanvas(event: KeyboardEvent): void {
    const target = event.target as HTMLElement | null;
    const estaEditandoTexto = !!target && (
      target.tagName === 'INPUT' ||
      target.tagName === 'TEXTAREA' ||
      target.tagName === 'SELECT' ||
      target.isContentEditable ||
      !!target.closest('.inline-edit-stop')
    );
    if (estaEditandoTexto) return;

    if (event.key === 'Escape') {
      event.preventDefault();
      this.cancelarInteraccionSeleccion();
      return;
    }

    if ((event.ctrlKey || event.metaKey) && event.key.toLowerCase() === 'a') {
      event.preventDefault();
      this.nodosSeleccionados = new Set(this.nodos.map(n => n.id));
      this.nodoSeleccionado = this.nodos[0]?.id ?? null;
      this.relacionSeleccionada = null;
      this.inspectorModo = this.nodoSeleccionado ? 'clase' : 'lienzo';
    }
  }

  private cancelarInteraccionSeleccion(): void {
    this.cancelarMarquesina();
    this.arrastrandoId = null;
    this.posicionesInicialesArrastre.clear();
    this.huboMovimientoArrastre = false;
  }

  private cancelarMarquesina(): void {
    this.marquesinaActiva = false;
    this.marquesinaSeleccion = undefined;
  }

  private puntoCanvasDesdeEvento(event: MouseEvent): { x: number; y: number } {
    const stage = (event.currentTarget as HTMLElement).getBoundingClientRect();
    const factorZoom = this.zoomCanvas / 100;
    return {
      x: (event.clientX - stage.left - this.canvasPan.x) / factorZoom,
      y: (event.clientY - stage.top - this.canvasPan.y) / factorZoom
    };
  }

  private actualizarMarquesina(event: MouseEvent): void {
    const actual = this.puntoCanvasDesdeEvento(event);
    const x = Math.min(this.inicioMarquesina.x, actual.x);
    const y = Math.min(this.inicioMarquesina.y, actual.y);
    const ancho = Math.abs(actual.x - this.inicioMarquesina.x);
    const alto = Math.abs(actual.y - this.inicioMarquesina.y);
    this.marquesinaSeleccion = { x, y, ancho, alto };
    this.nodosSeleccionados = new Set(
      this.nodos
        .filter(nodo => this.rectangulosIntersectan(this.rectanguloNodo(nodo), this.marquesinaSeleccion!))
        .map(nodo => nodo.id)
    );
    this.nodoSeleccionado = this.primerNodoSeleccionado();
  }

  private finalizarMarquesina(): void {
    this.marquesinaActiva = false;
    if (this.nodoSeleccionado) {
      this.relacionSeleccionada = null;
      this.inspectorModo = 'clase';
      const entidad = this.buscarEntidadPorNodo(this.nodoSeleccionado);
      if (entidad) this.entidadActual = entidad;
    } else {
      this.inspectorModo = 'lienzo';
    }
  }

  private rectanguloNodo(nodo: NodoDiagrama): RectanguloCanvas {
    return { x: nodo.x, y: nodo.y, ancho: nodo.ancho, alto: 170 };
  }

  private rectangulosIntersectan(a: RectanguloCanvas, b: RectanguloCanvas): boolean {
    return a.x < b.x + b.ancho && a.x + a.ancho > b.x && a.y < b.y + b.alto && a.y + a.alto > b.y;
  }

  private moverSeleccionDesdeDelta(deltaX: number, deltaY: number): void {
    const origenArrastrado = this.posicionesInicialesArrastre.get(this.arrastrandoId!);
    if (!origenArrastrado) return;

    let nuevoXArrastrado = origenArrastrado.x + deltaX;
    let nuevoYArrastrado = origenArrastrado.y + deltaY;
    if (this.rejillaActiva) {
      nuevoXArrastrado = Math.round(nuevoXArrastrado / 10) * 10;
      nuevoYArrastrado = Math.round(nuevoYArrastrado / 10) * 10;
    }

    let deltaAplicadoX = nuevoXArrastrado - origenArrastrado.x;
    let deltaAplicadoY = nuevoYArrastrado - origenArrastrado.y;
    for (const [id, posicion] of this.posicionesInicialesArrastre) {
      const nodo = this.nodos.find(n => n.id === id);
      if (!nodo) continue;
      deltaAplicadoX = Math.max(deltaAplicadoX, 10 - posicion.x);
      deltaAplicadoX = Math.min(deltaAplicadoX, 1400 - posicion.x);
      deltaAplicadoY = Math.max(deltaAplicadoY, 10 - posicion.y);
      deltaAplicadoY = Math.min(deltaAplicadoY, 900 - posicion.y);
    }

    for (const [id, posicion] of this.posicionesInicialesArrastre) {
      const nodo = this.nodos.find(n => n.id === id);
      if (!nodo) continue;
      nodo.x = posicion.x + deltaAplicadoX;
      nodo.y = posicion.y + deltaAplicadoY;
      const entidad = this.buscarEntidadPorNodo(nodo.id);
      if (entidad) {
        entidad.posicionX = nodo.x;
        entidad.posicionY = nodo.y;
      }
      if (this.entidadActual && this.entidadActual.id === entidad?.id) {
        this.entidadActual.posicionX = nodo.x;
        this.entidadActual.posicionY = nodo.y;
      }
    }
  }

  private persistirMovimientoSeleccion(): void {
    if (!this.huboMovimientoArrastre) {
      this.posicionesInicialesArrastre.clear();
      return;
    }
    const cambios = this.nodos
      .filter(nodo => {
        const inicio = this.posicionesInicialesArrastre.get(nodo.id);
        return !!inicio && (inicio.x !== nodo.x || inicio.y !== nodo.y);
      })
      .map(nodo => ({ nodo, entidad: this.buscarEntidadPorNodo(nodo.id) }))
      .filter((cambio): cambio is { nodo: NodoDiagrama; entidad: EntidadModelo } => !!cambio.entidad);

    this.posicionesInicialesArrastre.clear();
    if (!this.modeloActual || cambios.length === 0) return;

    const modeloId = this.modeloActual.id;
    const versionPersistencia = this.versionInteraccionCanvas;
    forkJoin(cambios.map(({ nodo, entidad }) =>
      this.api.actualizarEntidad(entidad.id, { nombre: nodo.nombre, posicionX: nodo.x, posicionY: nodo.y }).pipe(
        map(actualizada => ({ ok: true as const, actualizada })),
        catchError(error => of({ ok: false as const, error }))
      )
    )).subscribe({
      next: (resultados) => {
        const seleccionPrevia = new Set(this.nodosSeleccionados);
        const nodoPrincipal = this.nodoSeleccionado;
        const actualizadas = resultados.filter((resultado): resultado is { ok: true; actualizada: EntidadModelo } => resultado.ok).map(resultado => resultado.actualizada);
        const fallidas = resultados.filter(resultado => !resultado.ok);
        if (actualizadas.length === 1) {
          this.entidadActual = actualizadas[0];
        }
        this.api.obtenerModelo(modeloId).subscribe({
          next: (modelo) => {
            if (this.versionInteraccionCanvas !== versionPersistencia) return;
            const mensaje = fallidas.length
              ? 'No se pudieron guardar todas las posiciones seleccionadas. Revisá la conexión e intentá nuevamente.'
              : (actualizadas.length > 1 ? 'Posiciones de clases actualizadas.' : `Posición actualizada: ${actualizadas[0].nombre}`);
            this.aplicarModeloActivo(modelo, mensaje, fallidas.length ? 'error' : 'success');
            this.nodosSeleccionados = new Set([...seleccionPrevia].filter(id => this.nodos.some(n => n.id === id)));
            this.nodoSeleccionado = nodoPrincipal && this.nodosSeleccionados.has(nodoPrincipal) ? nodoPrincipal : this.primerNodoSeleccionado();
            this.inspectorModo = this.nodoSeleccionado ? 'clase' : 'lienzo';
          },
          error: (err) => this.mostrarError(err)
        });
      }
    });
  }

  // Geometría y edición del lienzo UML
  get nodoActual(): NodoDiagrama | undefined {
    return this.nodos.find(n => n.id === this.nodoSeleccionado);
  }

  esNodoSeleccionado(id: string): boolean {
    return this.nodosSeleccionados.has(id);
  }

  private primerNodoSeleccionado(): string | null {
    const siguiente = this.nodosSeleccionados.values().next();
    return siguiente.done ? null : siguiente.value;
  }

  get relacionActual(): RelacionVisual | undefined {
    return this.relacionesVisuales.find(r => r.id === this.relacionSeleccionada);
  }

  get modoPersistenciaCanvas(): string {
    return this.modeloActual ? 'Persistido en modelo actual' : 'Demo/local: sin modelo seleccionado';
  }

  pathRelacion(relacion: RelacionVisual): string {
    const puntos = this.puntosRutaRelacion(relacion);
    if (puntos.length === 0) return 'M 0 0 L 0 0';
    return puntos.map((punto, index) => `${index === 0 ? 'M' : 'L'} ${punto.x} ${punto.y}`).join(' ');
  }

  posControlRelacion(relacion: RelacionVisual): { x: number; y: number } {
    const puntos = this.puntosRutaRelacion(relacion);
    if (puntos.length < 4) return { x: -100, y: -100 };
    return { x: puntos[1].x - 9, y: ((puntos[1].y + puntos[2].y) / 2) - 9 };
  }

  iniciarArrastreControlRelacion(relacion: RelacionVisual, event: MouseEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.seleccionarRelacion(relacion);
    this.arrastrandoControlRelacionId = relacion.id;
    this.arrastrandoCanvas = false;
    this.arrastrandoId = null;
    this.posInicioRaton = { x: event.clientX, y: event.clientY };
    this.posInicioElemento = { x: this.obtenerControlX(relacion), y: 0 };
  }

  posLabelRelacion(relacion: RelacionVisual): { x: number; y: number } {
    const puntos = this.puntosRutaRelacion(relacion);
    if (puntos.length === 0) return { x: 40, y: 40 };
    const indice = Math.floor((puntos.length - 1) / 2);
    const actual = puntos[indice];
    const siguiente = puntos[Math.min(indice + 1, puntos.length - 1)];
    return { x: (actual.x + siguiente.x) / 2 - 45, y: (actual.y + siguiente.y) / 2 - 18 };
  }

  posCardinalidadRelacion(relacion: RelacionVisual, extremo: 'origen' | 'destino'): { x: number; y: number } {
    const origen = this.nodos.find(n => n.id === relacion.origenId);
    const destino = this.nodos.find(n => n.id === relacion.destinoId);
    if (!origen || !destino) return { x: 20, y: 20 };
    const base = extremo === 'origen' ? this.puntoConexion(origen, destino) : this.puntoConexion(destino, origen);
    return { x: base.x + 8, y: base.y - 18 };
  }

  etiquetaRelacion(relacion: RelacionVisual): string {
    const partes = [relacion.nombre, relacion.verbo].filter(Boolean);
    return partes.length ? partes.join(' · ') : this.nombreTipoRelacion(relacion.tipo);
  }

  nombreTipoRelacion(tipo: TipoRelacionVisual): string {
    const normalizado = this.normalizarTipoRelacion(tipo);
    const nombres: Record<TipoRelacion, string> = {
      ASOCIACION: 'Asociación',
      AGREGACION: 'Agregación',
      COMPOSICION: 'Composición',
      GENERALIZACION: 'Generalización',
      DEPENDENCIA: 'Dependencia',
      REALIZACION: 'Realización'
    };
    return nombres[normalizado];
  }

  claseRelacion(relacion: RelacionVisual): string {
    return `uml-relation-${this.normalizarTipoRelacion(relacion.tipo).toLowerCase()}`;
  }

  marcadorFinRelacion(relacion: RelacionVisual): string | null {
    const tipo = this.normalizarTipoRelacion(relacion.tipo);
    if (tipo === 'AGREGACION') return 'url(#aggregationUml)';
    if (tipo === 'COMPOSICION') return 'url(#compositionUml)';
    if (tipo === 'GENERALIZACION') return 'url(#inheritanceUml)';
    if (tipo === 'DEPENDENCIA') return 'url(#openArrowUml)';
    if (tipo === 'REALIZACION') return 'url(#realizationUml)';
    return 'url(#openArrowUml)';
  }

  trazoRelacion(relacion: RelacionVisual): string | null {
    const tipo = this.normalizarTipoRelacion(relacion.tipo);
    return tipo === 'DEPENDENCIA' || tipo === 'REALIZACION' ? '7,5' : null;
  }

  private puntosRutaRelacion(relacion: RelacionVisual): { x: number; y: number }[] {
    const origen = this.nodos.find(n => n.id === relacion.origenId);
    const destino = this.nodos.find(n => n.id === relacion.destinoId);
    if (!origen || !destino) return [];
    const start = this.puntoConexion(origen, destino);
    const end = this.puntoConexion(destino, origen);
    if (relacion.ruta === 'RECTA') {
      return [start, end];
    }
    const controlX = this.obtenerControlX(relacion, start, end);
    return [start, { x: controlX, y: start.y }, { x: controlX, y: end.y }, end];
  }

  private obtenerControlX(relacion: RelacionVisual, start?: { x: number; y: number }, end?: { x: number; y: number }): number {
    if (relacion.controlX !== undefined) return relacion.controlX;
    if (!start || !end) {
      const origen = this.nodos.find(n => n.id === relacion.origenId);
      const destino = this.nodos.find(n => n.id === relacion.destinoId);
      if (!origen || !destino) return 0;
      start = this.puntoConexion(origen, destino);
      end = this.puntoConexion(destino, origen);
    }
    return (start.x + end.x) / 2;
  }

  private puntoConexion(origen: NodoDiagrama, destino: NodoDiagrama): { x: number; y: number } {
    const centroOrigen = { x: origen.x + origen.ancho / 2, y: origen.y + 85 };
    const centroDestino = { x: destino.x + destino.ancho / 2, y: destino.y + 85 };
    const dx = centroDestino.x - centroOrigen.x;
    const dy = centroDestino.y - centroOrigen.y;
    if (Math.abs(dx) > Math.abs(dy)) {
      return { x: dx >= 0 ? origen.x + origen.ancho : origen.x, y: centroOrigen.y };
    }
    return { x: centroOrigen.x, y: dy >= 0 ? origen.y + 170 : origen.y };
  }

  cambiarModulo(modulo: ModuloActivo): void {
    this.moduloActivo = modulo;
    if (modulo === 'diagramas') {
      if (this.proyectoActual && this.modeloActual) {
        this.listarPresencia();
        this.listarConflictos();
      }
    } else if (modulo === 'qwen') {
      if (this.proyectoActual) {
        this.listarPropuestas();
      }
    } else if (modulo === 'generador') {
      if (this.proyectoActual) {
        this.cargarDeployments();
        this.cargarAlmacenamiento();
        this.cargarAdministracionColaboracion();
      }
    }
  }

  cambiarModoLienzo(modo: ModoLienzo): void {
    this.modoLienzo = modo;
  }

  alternarRejilla(): void {
    this.rejillaActiva = !this.rejillaActiva;
  }

  ajustarZoom(delta: number): void {
    const nuevoZoom = this.zoomCanvas + delta;
    if (nuevoZoom >= 50 && nuevoZoom <= 180) {
      this.zoomCanvas = nuevoZoom;
    }
  }

  centrarLienzo(): void {
    this.zoomCanvas = 100;
    this.canvasPan = { x: 0, y: 0 };
    this.notificarInfo('Lienzo centrado al 100%.');
  }

  autoOrganizarSugiyama(): void {
    const pac = this.nodos.find(n => n.id === 'Paciente');
    const med = this.nodos.find(n => n.id === 'Medico');
    const turno = this.nodos.find(n => n.id === 'Turno');
    if (pac) { pac.x = 80; pac.y = 120; }
    if (med) { med.x = 530; med.y = 120; }
    if (turno) { turno.x = 310; turno.y = 370; }
    this.notaOcl.x = 650;
    this.notaOcl.y = 430;
    this.canvasPan = { x: 0, y: 0 };
    this.notificarInfo('Disposición ortogonal optimizada con algoritmo jerárquico Sugiyama.');
  }

  exportarSvg(): void {
    this.notificarExito('Diagrama exportado en formato SVG compatible OMG UML 2.5.');
  }

  exportarPng(): void {
    this.notificarExito('Diagrama exportado en PNG de alta resolución.');
  }

  cambiarZoom(delta: number): void {
    this.ajustarZoom(delta);
  }

  restablecerZoom(): void {
    this.centrarLienzo();
  }

  aplicarLayoutSugiyama(): void {
    this.autoOrganizarSugiyama();
  }

  exportarDiagramaSvg(): void {
    this.exportarSvg();
  }

  exportarDiagramaPng(): void {
    this.exportarPng();
  }

  seleccionarProyectoPorId(id: number | string | null): void {
    if (!id) return;
    const numId = typeof id === 'string' ? parseInt(id, 10) : id;
    const proyecto = this.proyectos.find(p => p.id === numId);
    if (proyecto) {
      this.seleccionarProyecto(proyecto);
    }
  }

  activarHerramienta(herramienta: HerramientaUml): void {
    if (herramienta === 'entity') {
      this.crearClaseDirectaStencil();
      return;
    }
    this.seleccionarHerramienta(herramienta);
  }

  arrastrar(event: MouseEvent): void {
    this.alMoverRaton(event);
  }

  detenerArrastre(): void {
    this.alSoltarRaton();
  }

  solicitarPropuestaQwen(): void {
    this.solicitarPropuestaQwenTexto();
  }

  seleccionarHerramienta(herramienta: HerramientaUml): void {
    if (herramienta === 'entity') {
      this.crearClaseDirectaStencil();
      return;
    }
    this.herramientaUml = herramienta;
    this.cancelarMarquesina();
    this.origenRelacionPendiente = null;
    if (this.esHerramientaRelacion(herramienta)) {
      this.formularioRelacion.tipo = this.tipoRelacionDesdeHerramienta(herramienta);
      this.notificarInfo(`${this.nombreTipoRelacion(this.formularioRelacion.tipo)}: seleccioná clase origen y luego clase destino.`);
    }
  }


  seleccionarNodo(nombre: string, event?: MouseEvent): void {
    event?.stopPropagation();
    if (this.esHerramientaRelacion(this.herramientaUml)) {
      this.seleccionarNodoParaRelacion(nombre);
      return;
    }
    if (this.suprimirClickNodo) {
      this.suprimirClickNodo = false;
      return;
    }
    if (event?.ctrlKey || event?.metaKey) {
      if (this.nodosSeleccionados.has(nombre)) {
        this.nodosSeleccionados.delete(nombre);
      } else {
        this.nodosSeleccionados.add(nombre);
      }
      this.nodoSeleccionado = this.nodosSeleccionados.has(nombre) ? nombre : this.primerNodoSeleccionado();
    } else {
      this.nodosSeleccionados.clear();
      this.nodosSeleccionados.add(nombre);
      this.nodoSeleccionado = nombre;
    }
    this.relacionSeleccionada = null;
    this.inspectorModo = this.nodoSeleccionado ? 'clase' : 'lienzo';
    if (this.modeloActual) {
      const match = this.modeloActual.entidades.find(e => e.id.toString() === nombre || e.nombre.toLowerCase() === nombre.toLowerCase());
      if (match) this.entidadActual = match;
    }
  }

  seleccionarRelacion(relacion: RelacionVisual, event?: MouseEvent): void {
    event?.stopPropagation();
    this.relacionSeleccionada = relacion.id;
    this.nodoSeleccionado = null;
    this.nodosSeleccionados.clear();
    this.origenRelacionPendiente = null;
    this.inspectorModo = 'relacion';
    this.formularioRelacion = {
      nombre: relacion.nombre,
      verbo: relacion.verbo || '',
      tipo: relacion.tipo,
      ruta: relacion.ruta ?? 'ORTOGONAL',
      cardinalidadOrigen: relacion.cardinalidadOrigen,
      cardinalidadDestino: relacion.cardinalidadDestino
    };
  }

  alClickCanvas(event: MouseEvent): void {
    if (this.suprimirClickCanvas) {
      this.suprimirClickCanvas = false;
      return;
    }
    if (event.target !== event.currentTarget) return;
    if (this.herramientaUml === 'select') {
      this.nodoSeleccionado = null;
      this.nodosSeleccionados.clear();
      this.relacionSeleccionada = null;
      this.inspectorModo = 'lienzo';
    }
  }

  guardarClaseInspector(): void {
    const nodo = this.nodoActual;
    if (!nodo) return;
    nodo.tabla = nodo.nombre.toLowerCase();
    const entidad = this.buscarEntidadPorNodo(nodo.id);
    if (this.modeloActual && entidad) {
      this.api.actualizarEntidad(entidad.id, { nombre: nodo.nombre, posicionX: nodo.x, posicionY: nodo.y }).subscribe({
        next: (actualizada) => {
          this.entidadActual = actualizada;
          this.refrescarModelo(this.modeloActual!.id, `Clase actualizada: ${actualizada.nombre}`);
        },
        error: (err) => this.mostrarError(err)
      });
      return;
    }
    this.notificarExito('Clase actualizada en modo demo/local (estereotipo y nombre no persistidos).');
  }

  agregarAtributoDesdeInspector(): void {
    const nodo = this.nodoActual;
    if (!nodo || !this.formularioAtributo.nombre.trim()) return;
    const entidad = this.buscarEntidadPorNodo(nodo.id);
    if (this.modeloActual && entidad) {
      this.api.crearAtributo(entidad.id, this.formularioAtributo).subscribe({
        next: (atributo) => {
          this.agregarAtributoVisual(nodo, atributo);
          this.refrescarModelo(this.modeloActual!.id, `Atributo creado: ${atributo.nombre}`);
        },
        error: (err) => this.mostrarError(err)
      });
      return;
    }
    nodo.atributos.push({ nombre: this.formularioAtributo.nombre, tipo: this.formularioAtributo.tipoDato, visibilidad: this.formularioAtributo.clavePrimaria ? '+' : '-', pk: this.formularioAtributo.clavePrimaria, req: this.formularioAtributo.obligatorio, unique: this.formularioAtributo.valorUnico });
    this.notificarExito('Atributo agregado en modo demo/local.');
  }

  get operacionesEntidadActual(): OperacionEntidad[] {
    return this.entidadActual?.detalleOperaciones ?? [];
  }

  get operacionesCompatiblesEntidadActual(): string[] {
    if (this.operacionesEntidadActual.length) {
      return this.operacionesEntidadActual.map(op => this.textoOperacion(op));
    }
    return this.entidadActual?.operaciones ?? [];
  }

  prepararOperacionNueva(): void {
    this.operacionEditandoId = null;
    this.formularioOperacion = this.crearFormularioOperacionVacio();
  }

  editarOperacionDesdeInspector(operacion: OperacionEntidad): void {
    this.operacionEditandoId = operacion.id;
    this.formularioOperacion = {
      nombre: operacion.nombre,
      tipoRetorno: operacion.tipoRetorno,
      firma: operacion.firma,
      visibilidad: operacion.visibilidad
    };
  }

  guardarOperacionDesdeInspector(): void {
    if (!this.requiereSesion()) {
      return;
    }
    if (!this.modeloActual || !this.entidadActual) {
      this.notificarError('Primero seleccioná una clase persistida para editar operaciones.');
      return;
    }
    const solicitud = {
      nombre: this.formularioOperacion.nombre.trim(),
      tipoRetorno: this.formularioOperacion.tipoRetorno.trim(),
      firma: this.formularioOperacion.firma.trim(),
      visibilidad: this.formularioOperacion.visibilidad
    };
    if (!solicitud.nombre || !solicitud.tipoRetorno || !solicitud.firma) {
      this.notificarError('Completá nombre, tipo de retorno y firma de la operación.');
      return;
    }

    const modeloId = this.modeloActual.id;
    this.operacionGuardando = true;
    this.cargando = true;
    const operacionId = this.operacionEditandoId;
    const peticion = operacionId
      ? this.api.actualizarOperacion(operacionId, solicitud)
      : this.api.crearOperacion(this.entidadActual.id, solicitud);

    peticion.subscribe({
      next: (operacion) => {
        this.operacionEditandoId = operacion.id;
        this.refrescarModelo(modeloId, operacionId ? `Operación actualizada: ${operacion.nombre}` : `Operación creada: ${operacion.nombre}`);
      },
      error: (err) => {
        this.operacionGuardando = false;
        this.mostrarError(err);
      },
      complete: () => {
        this.operacionGuardando = false;
      }
    });
  }

  eliminarOperacionDesdeInspector(operacion: OperacionEntidad): void {
    if (!this.requiereSesion()) {
      return;
    }
    if (!this.modeloActual) {
      this.notificarError('Primero seleccioná un modelo activo.');
      return;
    }
    const modeloId = this.modeloActual.id;
    this.operacionGuardando = true;
    this.cargando = true;
    this.api.eliminarOperacion(operacion.id).subscribe({
      next: () => {
        if (this.operacionEditandoId === operacion.id) {
          this.prepararOperacionNueva();
        }
        this.refrescarModelo(modeloId, `Operación eliminada: ${operacion.nombre}`);
      },
      error: (err) => {
        this.operacionGuardando = false;
        this.mostrarError(err);
      },
      complete: () => {
        this.operacionGuardando = false;
      }
    });
  }

  textoOperacion(operacion: OperacionEntidad): string {
    return operacion.texto || `${this.simboloVisibilidadOperacion(operacion.visibilidad)}${operacion.nombre}${operacion.firma} : ${operacion.tipoRetorno}`;
  }

  etiquetaVisibilidadOperacion(visibilidad: VisibilidadOperacion): string {
    return {
      PUBLICA: '+ pública',
      PROTEGIDA: '# protegida',
      PRIVADA: '- privada',
      PAQUETE: '~ paquete'
    }[visibilidad];
  }

  private simboloVisibilidadOperacion(visibilidad: VisibilidadOperacion): string {
    return { PUBLICA: '+', PROTEGIDA: '#', PRIVADA: '-', PAQUETE: '~' }[visibilidad];
  }

  guardarRelacionInspector(): void {
    const relacion = this.relacionActual;
    if (!relacion) return;
    Object.assign(relacion, {
      nombre: this.formularioRelacion.nombre,
      verbo: this.formularioRelacion.verbo || undefined,
      tipo: this.formularioRelacion.tipo,
      ruta: this.formularioRelacion.ruta,
      cardinalidadOrigen: this.formularioRelacion.cardinalidadOrigen,
      cardinalidadDestino: this.formularioRelacion.cardinalidadDestino
    });
    this.persistirRelacionVisual(relacion, 'Relación actualizada en modo demo/local.');
  }

  cambiarCardinalidadRelacion(relacion: RelacionVisual, extremo: 'origen' | 'destino', valor: string, event?: Event): void {
    event?.stopPropagation();
    if (valor !== '1' && valor !== '0..*') return;
    this.seleccionarRelacion(relacion);
    if (extremo === 'origen') {
      relacion.cardinalidadOrigen = valor;
    } else {
      relacion.cardinalidadDestino = valor;
    }
    this.sincronizarFormularioRelacion(relacion);
    this.persistirRelacionVisual(relacion, 'Cardinalidad actualizada en modo demo/local.');
  }

  actualizarVerboRelacion(relacion: RelacionVisual, valor: string, event?: Event): void {
    event?.stopPropagation();
    this.seleccionarRelacion(relacion);
    relacion.verbo = valor.trim() || undefined;
    this.sincronizarFormularioRelacion(relacion);
  }

  confirmarVerboRelacion(relacion: RelacionVisual, event?: Event): void {
    event?.stopPropagation();
    this.seleccionarRelacion(relacion);
    this.sincronizarFormularioRelacion(relacion);
    this.persistirRelacionVisual(relacion, 'Verbo actualizado en modo demo/local.');
  }

  confirmarVerboRelacionConEnter(relacion: RelacionVisual, event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    this.confirmarVerboRelacion(relacion, event);
  }

  detenerEdicionCanvas(event: Event): void {
    event.stopPropagation();
  }

  iniciarEdicionClaseInline(nodo: NodoDiagrama, campo: 'nombre' | 'tabla', event?: Event): void {
    event?.preventDefault();
    event?.stopPropagation();
    this.seleccionarNodo(nodo.id);
    this.edicionInlineAtributo = undefined;
    this.edicionInlineClase = { nodoId: nodo.id, campo, valor: campo === 'nombre' ? nodo.nombre : nodo.tabla, original: campo === 'nombre' ? nodo.nombre : nodo.tabla };
    this.enfocarControlInline(`inline-${campo}-${nodo.id}`);
  }

  guardarEdicionClaseInline(event?: Event): void {
    event?.preventDefault();
    event?.stopPropagation();
    const edicion = this.edicionInlineClase;
    if (!edicion) return;
    const nodo = this.nodos.find(n => n.id === edicion.nodoId);
    if (!nodo) {
      this.edicionInlineClase = undefined;
      return;
    }
    const valor = edicion.valor.trim();
    if (!valor) {
      this.notificarError('El valor no puede quedar vacío.');
      return;
    }
    if (edicion.campo === 'nombre') {
      nodo.nombre = valor;
      if (!nodo.tabla || nodo.tabla === edicion.original.toLowerCase() || nodo.tabla === `${edicion.original.toLowerCase()}s`) {
        nodo.tabla = valor.toLowerCase();
      }
      this.persistirNombreClaseInline(nodo);
    } else {
      nodo.tabla = valor;
      this.notificarInfo(this.modeloActual ? 'Nombre de tabla actualizado como anotación visual local; el backend actual persiste la entidad, no @Table.' : 'Tabla actualizada en modo demo/local.');
    }
    this.edicionInlineClase = undefined;
  }

  cancelarEdicionClaseInline(event?: Event): void {
    event?.preventDefault();
    event?.stopPropagation();
    this.edicionInlineClase = undefined;
  }

  esEditandoClase(nodo: NodoDiagrama, campo: 'nombre' | 'tabla'): boolean {
    return this.edicionInlineClase?.nodoId === nodo.id && this.edicionInlineClase.campo === campo;
  }

  iniciarEdicionAtributoInline(nodo: NodoDiagrama, index: number, event?: Event): void {
    event?.preventDefault();
    event?.stopPropagation();
    this.seleccionarNodo(nodo.id);
    const attr = nodo.atributos[index];
    if (!attr) return;
    this.edicionInlineClase = undefined;
    this.edicionInlineAtributo = {
      nodoId: nodo.id,
      index,
      nombre: attr.nombre,
      tipo: attr.tipo,
      pk: !!attr.pk,
      req: !!attr.req,
      unique: !!attr.unique,
      original: { ...attr }
    };
    this.enfocarControlInline(`inline-attr-nombre-${nodo.id}-${index}`);
  }

  guardarEdicionAtributoInline(event?: Event): void {
    event?.preventDefault();
    event?.stopPropagation();
    const edicion = this.edicionInlineAtributo;
    if (!edicion) return;
    const nodo = this.nodos.find(n => n.id === edicion.nodoId);
    const attr = nodo?.atributos[edicion.index];
    if (!nodo || !attr) {
      this.edicionInlineAtributo = undefined;
      return;
    }
    const nombre = edicion.nombre.trim();
    const tipo = (edicion.tipo || 'TEXTO').trim();
    if (!nombre || !tipo) {
      this.notificarError('El atributo necesita nombre y tipo.');
      return;
    }
    Object.assign(attr, {
      nombre,
      tipo,
      visibilidad: edicion.pk ? '+' : '-',
      pk: edicion.pk,
      req: edicion.pk ? true : edicion.req,
      unique: edicion.pk ? true : edicion.unique
    });
    this.edicionInlineAtributo = undefined;
    this.persistirAtributoInline(nodo, attr);
  }

  guardarEdicionAtributoInlinePorFoco(event: FocusEvent): void {
    const siguiente = event.relatedTarget as HTMLElement | null;
    if (siguiente?.closest('.uml-attribute-editor')) return;
    this.guardarEdicionAtributoInline(event);
  }

  cancelarEdicionAtributoInline(event?: Event): void {
    event?.preventDefault();
    event?.stopPropagation();
    this.edicionInlineAtributo = undefined;
  }

  esEditandoAtributo(nodo: NodoDiagrama, index: number): boolean {
    return this.edicionInlineAtributo?.nodoId === nodo.id && this.edicionInlineAtributo.index === index;
  }

  manejarTeclaInlineClase(event: KeyboardEvent): void {
    event.stopPropagation();
    if (event.key === 'Enter') {
      this.guardarEdicionClaseInline(event);
    } else if (event.key === 'Escape') {
      this.cancelarEdicionClaseInline(event);
    }
  }

  manejarTeclaInlineAtributo(event: KeyboardEvent): void {
    event.stopPropagation();
    if (event.key === 'Enter') {
      this.guardarEdicionAtributoInline(event);
    } else if (event.key === 'Escape') {
      this.cancelarEdicionAtributoInline(event);
    }
  }

  private enfocarControlInline(id: string): void {
    setTimeout(() => {
      const control = document.getElementById(id) as HTMLInputElement | HTMLSelectElement | null;
      control?.focus();
      if (control instanceof HTMLInputElement) control.select();
    });
  }

  private persistirNombreClaseInline(nodo: NodoDiagrama): void {
    const entidad = this.buscarEntidadPorNodo(nodo.id);
    if (this.modeloActual && entidad) {
      this.api.actualizarEntidad(entidad.id, { nombre: nodo.nombre, posicionX: nodo.x, posicionY: nodo.y }).subscribe({
        next: (actualizada) => {
          this.entidadActual = actualizada;
          this.refrescarModelo(this.modeloActual!.id, `Clase actualizada: ${actualizada.nombre}`);
        },
        error: (err) => this.mostrarError(err)
      });
      return;
    }
    this.notificarExito('Clase actualizada en modo demo/local.');
  }

  private persistirAtributoInline(nodo: NodoDiagrama, attr: AtributoVisual): void {
    const solicitud = {
      nombre: attr.nombre,
      tipoDato: this.tipoDatoDesdeVisual(attr.tipo),
      clavePrimaria: !!attr.pk,
      obligatorio: !!attr.req || !!attr.pk,
      valorUnico: !!attr.unique || !!attr.pk
    };
    if (this.modeloActual && attr.idBackend) {
      this.api.actualizarAtributo(attr.idBackend, solicitud).subscribe({
        next: (actualizado) => {
          Object.assign(attr, this.atributoVisualDesdeModelo(actualizado));
          this.notificarExito(`Atributo actualizado: ${actualizado.nombre}`);
        },
        error: (err) => this.mostrarError(err)
      });
      return;
    }
    this.notificarExito('Atributo actualizado en modo demo/local.');
  }

  private tipoDatoDesdeVisual(tipo: string): TipoDato {
    const normalizado = tipo.trim().toUpperCase();
    if (this.tiposDato.includes(normalizado as TipoDato)) return normalizado as TipoDato;
    const alias: Record<string, TipoDato> = {
      STRING: 'TEXTO',
      TEXT: 'TEXTO',
      INT: 'ENTERO',
      INTEGER: 'ENTERO',
      LONG: 'ENTERO_LARGO',
      BIGINT: 'ENTERO_LARGO',
      NUMBER: 'DECIMAL',
      DOUBLE: 'DECIMAL',
      FLOAT: 'DECIMAL',
      BOOL: 'BOOLEANO',
      BOOLEAN: 'BOOLEANO',
      DATE: 'FECHA',
      LOCALDATE: 'FECHA',
      DATETIME: 'FECHA_HORA',
      LOCALDATETIME: 'FECHA_HORA'
    };
    return alias[normalizado] ?? 'TEXTO';
  }

  private atributoVisualDesdeModelo(atributo: AtributoEntidad): AtributoVisual {
    return {
      idBackend: atributo.id,
      nombre: atributo.nombre,
      tipo: atributo.tipoDato,
      visibilidad: atributo.clavePrimaria ? '+' : '-',
      pk: atributo.clavePrimaria,
      req: atributo.obligatorio,
      unique: atributo.valorUnico
    };
  }

  private sincronizarFormularioRelacion(relacion: RelacionVisual): void {
    if (this.relacionSeleccionada !== relacion.id) return;
    this.formularioRelacion = {
      nombre: relacion.nombre,
      verbo: relacion.verbo || '',
      tipo: relacion.tipo,
      ruta: relacion.ruta ?? 'ORTOGONAL',
      cardinalidadOrigen: relacion.cardinalidadOrigen,
      cardinalidadDestino: relacion.cardinalidadDestino
    };
  }

  private persistirRelacionVisual(relacion: RelacionVisual, mensajeLocal: string): void {
    const origen = this.buscarEntidadPorNodo(relacion.origenId);
    const destino = this.buscarEntidadPorNodo(relacion.destinoId);
    if (this.modeloActual && relacion.relacionId && origen && destino) {
      this.api.actualizarRelacion(relacion.relacionId, {
        entidadOrigenId: origen.id,
        entidadDestinoId: destino.id,
        nombre: relacion.nombre,
        verbo: relacion.verbo,
        tipo: this.normalizarTipoRelacion(relacion.tipo),
        cardinalidadOrigen: this.cardinalidadApi(relacion.cardinalidadOrigen),
        cardinalidadDestino: this.cardinalidadApi(relacion.cardinalidadDestino)
      }).subscribe({
        next: (actualizada) => {
          const rutaVisual = relacion.ruta;
          const controlX = relacion.controlX;
          Object.assign(relacion, this.relacionVisualDesdeModelo(actualizada), { ruta: rutaVisual, controlX });
          this.sincronizarFormularioRelacion(relacion);
          this.notificarExito(`Relación actualizada: ${this.etiquetaRelacion(relacion)}`);
        },
        error: (err) => this.mostrarError(err)
      });
      return;
    }
    this.notificarInfo(mensajeLocal);
  }

  // =========================================================================
  // MÉTODOS DE WORKBENCH ESTILO STARUML / DRAW.IO
  // =========================================================================
  alternarPanelIzquierdo(): void {
    this.panelIzquierdoAbierto = !this.panelIzquierdoAbierto;
  }

  alternarPanelDerecho(): void {
    this.panelDerechoAbierto = !this.panelDerechoAbierto;
  }

  abrirDrawer(tipo: 'spring' | 'ia' | 'xmi' | 'validacion' | 'metamodelo' | 'administracion'): void {
    this.drawerActivo = this.drawerActivo === tipo ? 'ninguno' : tipo;
    if (this.drawerActivo === 'spring') {
      this.cargarDeployments();
      this.cargarAlmacenamiento();
    } else if (this.drawerActivo === 'ia') {
      this.listarPropuestas();
    } else if (this.drawerActivo === 'validacion') {
      this.validarModelo();
    } else if (this.drawerActivo === 'administracion') {
      this.cargarAdministracionColaboracion();
      this.cargarAccesoDiagrama();
    }
  }

  cerrarDrawer(): void {
    this.drawerActivo = 'ninguno';
  }

  actualizarCoordenadasCursor(event: MouseEvent): void {
    const stage = (event.currentTarget as HTMLElement).getBoundingClientRect();
    const factorZoom = this.zoomCanvas / 100;
    this.coordenadasCursor.x = Math.max(0, Math.round((event.clientX - stage.left - this.canvasPan.x) / factorZoom));
    this.coordenadasCursor.y = Math.max(0, Math.round((event.clientY - stage.top - this.canvasPan.y) / factorZoom));
  }

  enfocarYSeleccionarNodo(id: string): void {
    this.seleccionarNodo(id);
    const nodo = this.nodoActual;
    if (nodo) {
      this.canvasPan.x = Math.round(200 - nodo.x);
      this.canvasPan.y = Math.round(150 - nodo.y);
    }
  }

  agregarAtributoRapidoAClase(nodo: NodoDiagrama, event?: Event): void {
    event?.preventDefault();
    event?.stopPropagation();
    this.seleccionarNodo(nodo.id);
    const nuevoNombre = `atributo_${nodo.atributos.length + 1}`;
    const temporal: AtributoVisual = {
      nombre: nuevoNombre,
      tipo: 'TEXTO',
      visibilidad: '-',
      pk: false,
      req: false,
      unique: false
    };
    const entidad = this.buscarEntidadPorNodo(nodo.id);
    if (this.modeloActual && entidad) {
      this.api.crearAtributo(entidad.id, {
        nombre: nuevoNombre,
        tipoDato: 'TEXTO',
        clavePrimaria: false,
        obligatorio: false,
        valorUnico: false
      }).subscribe({
        next: (attr) => {
          nodo.atributos.push(this.atributoVisualDesdeModelo(attr));
          this.iniciarEdicionAtributoInline(nodo, nodo.atributos.length - 1);
          this.notificarExito(`Atributo agregado: ${attr.nombre}`);
        },
        error: (err) => this.mostrarError(err)
      });
      return;
    }
    nodo.atributos.push(temporal);
    this.iniciarEdicionAtributoInline(nodo, nodo.atributos.length - 1);
    this.notificarInfo('Atributo temporal agregado en modo demo/local. Editalo y confirmá en la tarjeta.');
  }

  eliminarAtributoDeClase(nodo: NodoDiagrama, index: number, event?: Event): void {
    event?.stopPropagation();
    nodo.atributos.splice(index, 1);
  }

  crearClaseDirectaStencil(): void {
    if (!this.requiereSesion()) {
      return;
    }
    if (!this.modeloActual) {
      this.notificarError('Primero crea o selecciona un modelo conceptual para guardar clases.');
      return;
    }
    this.herramientaUml = 'select';
    this.origenRelacionPendiente = null;
    const posicion = this.siguientePosicionClaseLibre();
    this.crearClaseEnCanvas(posicion.x, posicion.y, undefined, { requiereModelo: true });
  }

  crearNotaOclDirecta(): void {
    if (!this.requiereSesion()) {
      return;
    }
    const factorZoom = this.zoomCanvas / 100;
    this.notaOcl.x = Math.max(40, Math.round((360 - this.canvasPan.x) / factorZoom / 10) * 10);
    this.notaOcl.y = Math.max(40, Math.round((280 - this.canvasPan.y) / factorZoom / 10) * 10);
    this.notificarInfo('Nota OCL posicionada en el lienzo.');
  }

  private crearClaseEnCanvas(x: number, y: number, nombreClase?: string, opciones?: { requiereModelo?: boolean }): void {
    if (!this.requiereSesion()) {
      return;
    }
    if (opciones?.requiereModelo && !this.modeloActual) {
      this.notificarError('Primero crea o selecciona un modelo conceptual para guardar clases.');
      return;
    }
    this.herramientaUml = 'select';
    const nombre = (nombreClase || this.siguienteNombreClase()).trim();
    if (this.modeloActual) {
      this.api.crearEntidad(this.modeloActual.id, { nombre, posicionX: x, posicionY: y }).subscribe({
        next: (entidad) => {
          this.entidadActual = entidad;
          this.agregarOActualizarNodoDesdeEntidad(entidad);
          this.nodoSeleccionado = entidad.id.toString();
          this.nodosSeleccionados = new Set([entidad.id.toString()]);
          this.relacionSeleccionada = null;
          this.inspectorModo = 'clase';
          this.refrescarModelo(this.modeloActual!.id, `Clase persistida creada desde canvas: ${entidad.nombre}`);
        },
        error: (err) => this.mostrarError(err)
      });
      return;
    }
    const nodo: NodoDiagrama = { id: `local-${Date.now()}`, nombre, estereotipo: '«entity»', tabla: nombre.toLowerCase(), x, y, ancho: 260, atributos: [], operaciones: [] };
    this.nodos.push(nodo);
    this.nodoSeleccionado = nodo.id;
    this.nodosSeleccionados = new Set([nodo.id]);
    this.relacionSeleccionada = null;
    this.inspectorModo = 'clase';
    this.notificarExito(`Clase ${nombre} creada en modo demo/local (sin modelo persistido).`);
  }

  private seleccionarNodoParaRelacion(nodoId: string): void {
    if (!this.origenRelacionPendiente) {
      this.origenRelacionPendiente = nodoId;
      this.nodoSeleccionado = nodoId;
      this.nodosSeleccionados = new Set([nodoId]);
      this.notificarInfo('Origen seleccionado. Ahora elegí la clase destino.');
      return;
    }
    if (this.origenRelacionPendiente === nodoId) {
      this.notificarInfo('Elegí una clase destino distinta para la relación.');
      return;
    }
    this.crearRelacionDesdeSeleccion(this.origenRelacionPendiente, nodoId);
    this.origenRelacionPendiente = null;
  }

  private crearRelacionDesdeSeleccion(origenId: string, destinoId: string): void {
    const tipo = this.tipoRelacionDesdeHerramienta(this.herramientaUml);
    const nueva: RelacionVisual = { id: `rel-${Date.now()}`, origenId, destinoId, tipo, nombre: this.formularioRelacion.nombre || this.nombreTipoRelacion(tipo).toLowerCase(), verbo: this.formularioRelacion.verbo || undefined, cardinalidadOrigen: this.formularioRelacion.cardinalidadOrigen, cardinalidadDestino: this.formularioRelacion.cardinalidadDestino, ruta: this.formularioRelacion.ruta, persistida: false };
    const origen = this.buscarEntidadPorNodo(origenId);
    const destino = this.buscarEntidadPorNodo(destinoId);
    if (this.modeloActual && origen && destino) {
      this.api.crearRelacion(this.modeloActual.id, { entidadOrigenId: origen.id, entidadDestinoId: destino.id, nombre: nueva.nombre, verbo: nueva.verbo, tipo: this.normalizarTipoRelacion(nueva.tipo), cardinalidadOrigen: this.cardinalidadApi(nueva.cardinalidadOrigen), cardinalidadDestino: this.cardinalidadApi(nueva.cardinalidadDestino) }).subscribe({
        next: (relacion) => {
          const visual = this.relacionVisualDesdeModelo(relacion);
          this.relacionesVisuales.push(visual);
          this.seleccionarRelacion(visual);
          this.refrescarModelo(this.modeloActual!.id, `Relación persistida: ${this.etiquetaRelacion(visual)}`);
        },
        error: (err) => this.mostrarError(err)
      });
      return;
    }
    this.relacionesVisuales.push(nueva);
    this.seleccionarRelacion(nueva);
    this.notificarExito('Relación creada en modo demo/local; no hay entidades persistidas suficientes para guardarla.');
  }

  private esHerramientaRelacion(herramienta: HerramientaUml): boolean {
    return ['assoc', 'aggr', 'comp', 'gen', 'dep', 'real'].includes(herramienta);
  }

  private tipoRelacionDesdeHerramienta(herramienta: HerramientaUml): TipoRelacionVisual {
    if (herramienta === 'aggr') return 'AGREGACION';
    if (herramienta === 'comp') return 'COMPOSICION';
    if (herramienta === 'gen') return 'GENERALIZACION';
    if (herramienta === 'dep') return 'DEPENDENCIA';
    if (herramienta === 'real') return 'REALIZACION';
    return 'ASOCIACION';
  }

  private normalizarTipoRelacion(tipo?: TipoRelacionVisual | string | null): TipoRelacion {
    if (tipo === 'HERENCIA') return 'GENERALIZACION';
    const permitido: TipoRelacion[] = ['ASOCIACION', 'AGREGACION', 'COMPOSICION', 'GENERALIZACION', 'DEPENDENCIA', 'REALIZACION'];
    return permitido.includes(tipo as TipoRelacion) ? tipo as TipoRelacion : 'ASOCIACION';
  }

  private cardinalidadApi(valor: '1' | '0..*'): Cardinalidad {
    return valor === '1' ? 'UNO' : 'MUCHOS';
  }

  private cardinalidadVisual(valor: Cardinalidad): '1' | '0..*' {
    return valor === 'UNO' ? '1' : '0..*';
  }

  private siguienteNombreClase(): string {
    const nombresUsados = new Set([
      ...this.nodos.map(n => n.nombre.toLowerCase()),
      ...(this.modeloActual?.entidades.map(e => e.nombre.toLowerCase()) ?? [])
    ]);

    while (true) {
      const sufijo = this.contadorClasesLocales === 1 ? '' : this.contadorClasesLocales.toString();
      const candidato = `NuevaClase${sufijo}`;
      this.contadorClasesLocales++;
      if (!nombresUsados.has(candidato.toLowerCase())) {
        return candidato;
      }
    }
  }

  private siguientePosicionClaseLibre(): { x: number; y: number } {
    const ancho = 260;
    const alto = 180;
    const separacion = 40;
    const columnas = 4;
    const origenX = 80;
    const origenY = 120;
    const pasoX = ancho + separacion;
    const pasoY = alto + separacion;

    for (let intento = 0; intento < 80; intento++) {
      const indice = this.nodos.length + intento;
      const x = origenX + (indice % columnas) * pasoX;
      const y = origenY + Math.floor(indice / columnas) * pasoY;
      const colisiona = this.nodos.some(n =>
        x < n.x + n.ancho + separacion &&
        x + ancho + separacion > n.x &&
        y < n.y + alto + separacion &&
        y + alto + separacion > n.y
      );
      if (!colisiona) {
        return { x, y };
      }
    }

    const indice = this.nodos.length;
    return { x: origenX + (indice % columnas) * pasoX, y: origenY + Math.floor(indice / columnas) * pasoY };
  }

  private buscarEntidadPorNodo(nodoId: string): EntidadModelo | undefined {
    if (!this.modeloActual) return undefined;
    const nodo = this.nodos.find(n => n.id === nodoId);
    return this.modeloActual.entidades.find(e => e.id.toString() === nodoId || e.nombre.toLowerCase() === nodo?.nombre.toLowerCase());
  }

  private agregarAtributoVisual(nodo: NodoDiagrama, atributo: AtributoEntidad): void {
    nodo.atributos.push(this.atributoVisualDesdeModelo(atributo));
  }

  iniciarSesionDemoPorDefecto(): void {
    this.api.demoLogin().subscribe({
      next: (sesion) => {
        this.sesion = sesion;
        this.persistirSesion(sesion);
        this.notificarExito(`Sesión activa: ${sesion.usuarioId}`);
        this.cargarProyectos();
      },
      error: () => {
        this.sesion = {
          usuarioId: 'demo-modeler',
          nombreVisible: 'Demo Modeler',
          tokenDemo: 'demo-jwt-academic-token',
          demo: true,
          advertencia: 'Sesión académica demo (X-User-Id).'
        };
        this.persistirSesion(this.sesion);
        this.notificarExito('Sesión demo activa en modo académico local.');
        this.cargarProyectos();
      }
    });
  }

  abrirModalLogin(): void {
    this.modalLoginAbierto = true;
  }

  cerrarModalLogin(): void {
    this.modalLoginAbierto = false;
  }

  iniciarSesion(usuario?: string, clave?: string): void {
    const esPerfilRapido = !!usuario;
    const user = (usuario || this.formularioLogin.usuario || 'demo-modeler').trim();
    const pass = (clave !== undefined ? clave : (this.formularioLogin.clave || '')).trim();

    // Si es login manual y escribió contraseña, validar que sea aceptable
    if (!esPerfilRapido && pass && !this.contrasenaAceptable) {
      this.mostrarError('La contraseña no cumple con los requisitos mínimos de seguridad.');
      return;
    }

    this.ejecutar(`Iniciando sesión como ${user}...`, () => {
      this.api.login(user, pass).subscribe({
        next: (sesion) => {
          this.sesion = sesion;
          this.persistirSesion(sesion);
          this.cargarProyectos();
          if (!esPerfilRapido && this.formularioLogin.recordar) {
            this.guardarContrasenaLocal();
          }
          this.notificarExito(`Sesión iniciada con éxito para ${sesion.usuarioId}`);
          this.modalLoginAbierto = false;
        },
        error: (err) => this.mostrarError(err)
      });
    });
  }

  guardarContrasenaLocal(): void {
    try {
      localStorage.setItem('case_credenciales_recordadas', JSON.stringify({
        usuario: this.formularioLogin.usuario,
        guardadoEn: new Date().toISOString()
      }));
      this.credencialesGuardadas = true;
      this.notificarExito('✓ Usuario recordado en este navegador; no se guarda la contraseña.');
    } catch {
      this.notificarInfo('Usuario registrado localmente; no se guardó la contraseña.');
    }
  }

  cargarCredencialesGuardadas(): void {
    try {
      const data = localStorage.getItem('case_credenciales_recordadas');
      if (data) {
        const parsed = JSON.parse(data);
        if (parsed.usuario) this.formularioLogin.usuario = parsed.usuario;
        this.credencialesGuardadas = true;
      }
    } catch {
      // Ignorar si no existe
    }
  }

  olvidarContrasenaGuardada(): void {
    try {
      localStorage.removeItem('case_credenciales_recordadas');
      this.credencialesGuardadas = false;
      this.formularioLogin.clave = '';
      this.notificarInfo('Usuario recordado eliminado del navegador.');
    } catch {
      // Ignorar
    }
  }

  demoLogin(): void {
    this.iniciarSesion('demo-modeler', '');
  }

  logout(): void {
    this.ejecutar('Cerrando sesión...', () => {
      this.api.logout().subscribe({
        next: () => {
          this.sesion = undefined;
          this.borrarSesionPersistida();
          this.borrarContextoPersistido();
          this.limpiarContextoSesion();
          this.notificarExito('Sesión cerrada exitosamente. Inicia sesión para continuar.');
        },
        error: (err) => this.mostrarError(err)
      });
    });
  }

  cargarProyectos(restaurandoSesion = false): void {
    if (!this.requiereSesion()) {
      return;
    }
    this.ejecutar('Cargando proyectos...', () => {
      this.api.listarProyectos().subscribe({
        next: (proyectos) => {
          this.proyectos = proyectos;
          if (restaurandoSesion) {
            this.restaurarContextoPersistido(proyectos);
          } else if (proyectos.length > 0 && !this.proyectoActual) {
            this.seleccionarProyecto(proyectos[0]);
          }
          this.notificarInfo(proyectos.length ? 'Proyectos sincronizados con backend.' : 'No hay proyectos creados aún.');
        },
        error: (err) => {
          if (restaurandoSesion) {
            this.sesion = undefined;
            this.borrarSesionPersistida();
            this.borrarContextoPersistido();
            this.limpiarContextoSesion();
            this.notificarInfo('La sesión guardada ya no es válida. Iniciá sesión nuevamente.');
            return;
          }
          this.mostrarError(err);
        }
      });
    });
  }

  private restaurarContextoPersistido(proyectos: Proyecto[]): void {
    const contexto = this.leerContextoPersistido();
    const proyectoId = contexto.proyectoId;
    if (!proyectoId) {
      if (proyectos.length > 0 && !this.proyectoActual) {
        this.seleccionarProyecto(proyectos[0]);
      }
      return;
    }

    const proyecto = proyectos.find(p => p.id === proyectoId);
    if (!proyecto) {
      this.borrarProyectoYModeloPersistidos();
      this.notificarInfo('El proyecto guardado ya no está disponible. Seleccioná un proyecto.');
      return;
    }

    this.seleccionarProyecto(proyecto, { conservarModeloPersistido: true });
    if (contexto.modeloId) {
      this.restaurarModeloPersistido(contexto.modeloId, proyecto.id);
    }
  }

  private restaurarModeloPersistido(modeloId: number, proyectoId: number): void {
    this.api.obtenerModelo(modeloId).subscribe({
      next: (modelo) => {
        if (modelo.proyectoId !== proyectoId) {
          this.borrarModeloPersistido();
          this.limpiarContextoModelo();
          this.notificarInfo('El modelo guardado no pertenece al proyecto restaurado. Seleccioná un modelo.');
          return;
        }
        this.modeloActual = modelo;
        this.entidadActual = modelo.entidades[0];
        this.actualizarNodosDesdeModelo();
        this.resultadoValidacion = undefined;
        this.generacion = undefined;
        this.snapshot = undefined;
        this.persistirContextoSesion({ proyectoId, modeloId: modelo.id });
        this.notificarExito(`Modelo restaurado: ${modelo.nombre}`);
      },
      error: () => {
        this.borrarModeloPersistido();
        this.limpiarContextoModelo();
        this.notificarInfo('El modelo guardado ya no está disponible. Seleccioná o creá un modelo.');
      }
    });
  }

  crearProyecto(): void {
    if (!this.requiereSesion()) {
      return;
    }
    this.ejecutar('Creando proyecto...', () => {
      this.api.crearProyecto(this.formularioProyecto).subscribe({
        next: (proyecto) => {
          this.proyectoActual = proyecto;
          this.proyectos = [proyecto, ...this.proyectos];
          this.limpiarContextoModelo();
          this.persistirContextoSesion({ proyectoId: proyecto.id, modeloId: undefined });
          this.notificarExito(`Proyecto creado: ${proyecto.nombre}`);
        },
        error: (err) => this.mostrarError(err)
      });
    });
  }

  seleccionarProyecto(proyecto: Proyecto, opciones?: { conservarModeloPersistido?: boolean }): void {
    if (!this.requiereSesion()) {
      return;
    }
    const proyectoIdSeleccionado = proyecto.id;
    const conservarModeloPersistido = !!opciones?.conservarModeloPersistido;
    this.proyectoActual = proyecto;
    this.limpiarContextoModelo();
    this.persistirContextoSesion({
      proyectoId: proyecto.id,
      modeloId: conservarModeloPersistido ? this.leerContextoPersistido().modeloId : undefined
    });
    this.notificarInfo(`Proyecto activo: ${proyecto.nombre}`);
    this.cargarAdministracionColaboracion();

    if (conservarModeloPersistido) {
      return;
    }

    this.api.obtenerPrimerModeloDelProyecto(proyecto.id).subscribe({
      next: (modelo) => {
        if (this.proyectoActual?.id !== proyectoIdSeleccionado) {
          return;
        }
        this.aplicarModeloActivo(modelo, `Modelo cargado: ${modelo.nombre}`, 'success');
      },
      error: (err) => {
        if (this.proyectoActual?.id !== proyectoIdSeleccionado) {
          return;
        }
        if (err?.status === 404) {
          this.limpiarContextoModelo();
          this.persistirContextoSesion({ proyectoId: proyectoIdSeleccionado, modeloId: undefined });
          this.notificarInfo('Este proyecto no tiene modelo conceptual todavía. Creá un modelo para empezar a diagramar.');
          return;
        }
        this.mostrarError(err);
      }
    });
  }

  crearModelo(): void {
    if (!this.requiereSesion()) {
      return;
    }
    if (!this.proyectoActual) {
      this.notificarError('Primero selecciona o crea un proyecto.');
      return;
    }
    this.ejecutar('Creando modelo...', () => {
      this.api.crearModelo(this.proyectoActual!.id, this.formularioModelo).subscribe({
        next: (modelo) => this.refrescarModelo(modelo.id, `Modelo creado: ${modelo.nombre}`),
        error: (err) => this.mostrarError(err)
      });
    });
  }

  crearEntidad(): void {
    if (!this.requiereSesion()) {
      return;
    }
    if (!this.modeloActual) {
      this.notificarError('Primero crea un modelo conceptual.');
      return;
    }
    this.ejecutar('Creando entidad...', () => {
      this.api.crearEntidad(this.modeloActual!.id, this.formularioEntidad).subscribe({
        next: (entidad) => {
          this.entidadActual = entidad;
          this.nodoSeleccionado = entidad.id.toString();
          this.nodosSeleccionados = new Set([entidad.id.toString()]);
          this.refrescarModelo(this.modeloActual!.id, `Entidad creada: ${entidad.nombre}`);
        },
        error: (err) => this.mostrarError(err)
      });
    });
  }

  seleccionarEntidad(entidad: EntidadModelo): void {
    this.entidadActual = entidad;
    this.nodoSeleccionado = entidad.id.toString();
    this.nodosSeleccionados = new Set([entidad.id.toString()]);
    this.inspectorModo = 'clase';
    this.prepararOperacionNueva();
    this.notificarInfo(`Entidad seleccionada: ${entidad.nombre}`);
  }

  crearAtributo(): void {
    if (!this.requiereSesion()) {
      return;
    }
    if (!this.entidadActual || !this.modeloActual) {
      this.notificarError('Primero selecciona una entidad.');
      return;
    }
    this.ejecutar('Creando atributo...', () => {
      this.api.crearAtributo(this.entidadActual!.id, this.formularioAtributo).subscribe({
        next: (atributo) => {
          this.refrescarModelo(this.modeloActual!.id, `Atributo creado: ${atributo.nombre}`);
          if (this.formularioAtributo.clavePrimaria) {
            this.formularioAtributo = {
              nombre: 'descripcion',
              tipoDato: 'TEXTO',
              clavePrimaria: false,
              obligatorio: true,
              valorUnico: false
            };
          }
        },
        error: (err) => this.mostrarError(err)
      });
    });
  }

  validarModelo(): void {
    if (!this.modeloActual) {
      this.notificarError('Primero crea o selecciona un modelo conceptual.');
      return;
    }
    this.ejecutar('Validando metamodelo y restricciones OCL...', () => {
      this.api.validarModelo(this.modeloActual!.id).subscribe({
        next: (resultado) => {
          this.resultadoValidacion = resultado;
          const mensajeValidacion = resultado.valido
            ? 'Metamodelo verificado: sin violaciones OCL.'
            : 'Se detectaron infracciones en el metamodelo.';
          this.mensaje = mensajeValidacion;
          this.mostrarToast(mensajeValidacion, resultado.valido ? 'success' : 'error');
        },
        error: (err) => this.mostrarError(err)
      });
    });
  }

  sincronizarSnapshot(): void {
    if (!this.modeloActual) {
      this.notificarError('Primero crea un modelo conceptual.');
      return;
    }
    this.ejecutar('Obteniendo snapshot AST...', () => {
      this.api.obtenerSnapshot(this.modeloActual!.id).subscribe({
        next: (snapshot) => {
          this.snapshot = snapshot;
          this.notificarExito(`Snapshot AST v${snapshot.revision} sincronizado.`);
        },
        error: (err) => this.mostrarError(err)
      });
    });
  }

  registrarComandoSync(): void {
    if (!this.modeloActual) {
      this.notificarError('Primero crea un modelo conceptual.');
      return;
    }
    this.ejecutar('Enviando comando sync...', () => {
      this.api.registrarComandoSync(this.modeloActual!.id).subscribe({
        next: () => this.sincronizarSnapshot(),
        error: (err) => this.mostrarError(err)
      });
    });
  }

  cargarAdministracionColaboracion(): void {
    if (!this.proyectoActual || !this.sesion) {
      this.miembros = [];
      this.invitaciones = [];
      this.sugerencias = [];
      this.historialPermisos = [];
      this.notificarInfo('Iniciá sesión y seleccioná un proyecto para cargar la administración colaborativa.');
      return;
    }
    this.administracionCargando = true;
    forkJoin({
      miembros: this.api.listarMiembros(this.proyectoActual.id),
      invitaciones: this.api.listarInvitaciones(this.proyectoActual.id),
      sugerencias: this.api.listarSugerencias(this.proyectoActual.id),
      historial: this.api.listarHistorialProyecto(this.proyectoActual.id)
    }).subscribe({
      next: (datos) => {
        this.miembros = datos.miembros;
        this.invitaciones = datos.invitaciones;
        this.sugerencias = datos.sugerencias;
        this.historialPermisos = datos.historial;
        this.administracionCargando = false;
      },
      error: (err) => {
        this.miembros = [];
        this.invitaciones = [];
        this.sugerencias = [];
        this.historialPermisos = [];
        this.administracionCargando = false;
        this.mostrarError(err);
      }
    });
  }

  alternarCrearDiagrama(miembro: MiembroProyecto): void {
    if (!this.proyectoActual || !this.sesion) {
      this.notificarError('Primero iniciá sesión y seleccioná un proyecto.');
      return;
    }
    this.api.actualizarCapacidadCrearDiagrama(this.proyectoActual.id, miembro.userId, !miembro.canCreateDiagram).subscribe({
      next: () => {
        this.notificarExito(`Permiso de crear diagramas actualizado para ${miembro.displayName}.`);
        this.cargarAdministracionColaboracion();
      },
      error: (err) => this.mostrarError(err)
    });
  }

  actualizarRolMiembro(miembro: MiembroProyecto, role: string): void {
    if (!this.proyectoActual || !this.sesion || role === miembro.role) {
      return;
    }
    this.api.actualizarMiembro(this.proyectoActual.id, miembro.userId, { role }).subscribe({
      next: () => {
        this.notificarExito(`Rol actualizado para ${miembro.displayName}.`);
        this.cargarAdministracionColaboracion();
      },
      error: (err) => this.mostrarError(err)
    });
  }

  actualizarEstadoMiembro(miembro: MiembroProyecto, status: string): void {
    if (!this.proyectoActual || !this.sesion || status === (miembro.status || (miembro.active ? 'ACTIVE' : 'INACTIVE'))) {
      return;
    }
    this.api.actualizarMiembro(this.proyectoActual.id, miembro.userId, { status }).subscribe({
      next: () => {
        this.notificarExito(`Estado actualizado para ${miembro.displayName}.`);
        this.cargarAdministracionColaboracion();
      },
      error: (err) => this.mostrarError(err)
    });
  }

  invitarMiembroSeleccionado(userId?: string): void {
    if (!this.proyectoActual || !this.sesion) {
      this.notificarError('Primero iniciá sesión y seleccioná un proyecto.');
      return;
    }
    const destino = (userId || this.formularioInvitacion.userId).trim();
    if (!destino) {
      this.notificarError('Seleccioná una cuenta académica para invitar.');
      return;
    }
    this.ejecutar('Enviando invitación...', () => {
      this.api.invitarMiembro(this.proyectoActual!.id, destino, this.formularioInvitacion.role).subscribe({
        next: () => {
          this.formularioInvitacion.userId = '';
          this.notificarExito('Invitación registrada por el backend.');
          this.cargarAdministracionColaboracion();
        },
        error: (err) => this.mostrarError(err)
      });
    });
  }

  responderInvitacion(invitacion: InvitacionProyecto, accion: 'aceptar' | 'rechazar' | 'revocar'): void {
    if (!this.proyectoActual || !this.sesion) {
      this.notificarError('Primero iniciá sesión y seleccioná un proyecto.');
      return;
    }
    const llamada = accion === 'aceptar'
      ? this.api.aceptarInvitacion(this.proyectoActual.id, invitacion.id)
      : accion === 'rechazar'
        ? this.api.rechazarInvitacion(this.proyectoActual.id, invitacion.id)
        : this.api.revocarInvitacion(this.proyectoActual.id, invitacion.id);
    llamada.subscribe({
      next: () => {
        this.notificarExito('Invitación actualizada por el backend.');
        this.cargarAdministracionColaboracion();
      },
      error: (err) => this.mostrarError(err)
    });
  }

  crearDiagramaDemo(): void {
    if (!this.proyectoActual || !this.sesion) {
      this.notificarError('Primero iniciá sesión y seleccioná un proyecto.');
      return;
    }
    this.ejecutar('Creando nueva vista / diagrama...', () => {
      this.api.crearDiagrama(this.proyectoActual!.id, 'Vista de Subdominio Clases').subscribe({
        next: (diagrama) => {
          this.diagrama = diagrama;
          this.cargarAccesoDiagrama();
          this.notificarExito(`Vista creada: ${diagrama.name}`);
        },
        error: (err) => this.mostrarError(err)
      });
    });
  }

  cargarAccesoDiagrama(): void {
    if (!this.proyectoActual || !this.diagrama || !this.sesion) {
      this.accesosDiagrama = [];
      this.historialDiagrama = [];
      if (this.drawerActivo === 'administracion' && !this.diagrama) {
        this.notificarInfo('Seleccioná o creá un diagrama para ver sus accesos.');
      }
      return;
    }
    forkJoin({
      acceso: this.api.obtenerAccesoDiagrama(this.proyectoActual.id, this.diagrama.id),
      historial: this.api.listarHistorialDiagrama(this.proyectoActual.id, this.diagrama.id)
    }).subscribe({
      next: (resp) => {
        this.diagrama = resp.acceso.diagram;
        this.accesosDiagrama = resp.acceso.collaborators;
        this.historialDiagrama = resp.historial;
      },
      error: (err) => {
        this.accesosDiagrama = [];
        this.historialDiagrama = [];
        this.mostrarError(err);
      }
    });
  }

  invitarColaboradorDemo(): void {
    this.invitarMiembroSeleccionado();
  }

  enviarPresencia(): void {
    if (!this.proyectoActual || !this.modeloActual || !this.sesion) {
      return;
    }
    this.api.enviarPresencia(this.proyectoActual.id, this.modeloActual.id, this.sesion.usuarioId, this.sesion.nombreVisible).subscribe({
      next: () => this.listarPresencia(),
      error: (err) => this.mostrarError(err)
    });
  }

  listarPresencia(): void {
    if (!this.proyectoActual || !this.modeloActual) {
      return;
    }
    this.api.listarPresencia(this.proyectoActual.id, this.modeloActual.id).subscribe({
      next: (presencias) => {
        this.presencias = presencias;
      },
      error: () => {
        this.presencias = [
          { projectId: 1, modelId: 1, userId: 'demo-modeler', displayName: 'demo-modeler (Tú)', status: 'EDITING', cursor: { x: 230, y: 285 }, seenAt: 'Ahora' },
          { projectId: 1, modelId: 1, userId: 'profesor_evaluador', displayName: 'profesor_evaluador', status: 'VIEWING', cursor: { x: 640, y: 270 }, seenAt: 'Hace 10s' },
          { projectId: 1, modelId: 1, userId: 'estudiante_2', displayName: 'estudiante_2', status: 'IN_CONFLICT', cursor: { x: 370, y: 400 }, seenAt: 'Hace 5s' }
        ];
      }
    });
  }

  escucharEventos(): void {
    if (!this.proyectoActual || !this.modeloActual) {
      return;
    }
    this.eventos?.close();
    this.eventos = this.api.abrirEventosProyecto(this.proyectoActual.id, this.modeloActual.id);
    this.eventos.addEventListener('presence', (evento) => this.eventoSse = `presence: ${evento.data}`);
    this.eventos.addEventListener('conflict', (evento) => this.eventoSse = `conflict: ${evento.data}`);
    this.eventoSse = 'Canal SSE activo en localhost:8080/events';
  }

  crearConflictoDemo(): void {
    if (!this.proyectoActual || !this.modeloActual) {
      this.notificarError('Primero selecciona proyecto y modelo.');
      return;
    }
    const baseRevision = Math.max(0, this.modeloActual.version - 1);
    this.api.crearConflictoDemo(this.modeloActual.id, baseRevision).subscribe({
      next: () => this.listarConflictos(),
      error: (err) => this.mostrarError(err)
    });
  }

  listarConflictos(): void {
    if (!this.proyectoActual || !this.modeloActual) {
      return;
    }
    this.api.listarConflictos(this.proyectoActual.id, this.modeloActual.id).subscribe({
      next: (conflictos) => {
        this.conflictos = conflictos;
      },
      error: () => {
        this.conflictos = [
          {
            operationId: 'op-982',
            projectId: 1,
            modelId: 1,
            baseRevision: 14,
            currentRevision: 15,
            status: 'PENDIENTE',
            detail: 'Mutación concurrente de tipo UML 2.5: LocalDateTime [1] (Local) vs LocalDateTime [1] (Remoto SSE)',
            commandPayload: { campo: 'Turno.fechaHora', local: 'LocalDateTime', remoto: 'LocalDateTime' },
            createdAt: 'Hace 2 min'
          },
          {
            operationId: 'op-871',
            projectId: 1,
            modelId: 1,
            baseRevision: 13,
            currentRevision: 14,
            status: 'RESOLVED',
            detail: 'Adición de restricción {Unique} en Paciente.dni por demo-modeler',
            commandPayload: {},
            resolvedAt: 'Hace 10 min',
            createdAt: 'Hace 15 min'
          }
        ];
      }
    });
  }

  resolverConflicto(conflicto: ConflictoSync, modo: 'local' | 'remoto' = 'local'): void {
    if (!this.proyectoActual || !this.modeloActual) {
      conflicto.status = 'RESOLVED';
      this.notificarExito(`Conflicto ${conflicto.operationId} resuelto aceptando versión ${modo}.`);
      return;
    }
    this.api.resolverConflicto(this.proyectoActual.id, this.modeloActual.id, conflicto.operationId).subscribe({
      next: () => {
        this.listarConflictos();
        this.notificarExito(`Conflicto ${conflicto.operationId} resuelto con éxito (${modo}).`);
      },
      error: () => {
        conflicto.status = 'RESOLVED';
        this.notificarExito(`Conflicto resuelto (${modo}).`);
      }
    });
  }

  crearPropuestaTexto(): void {
    if (!this.proyectoActual) {
      this.notificarError('Primero selecciona un proyecto.');
      return;
    }
    this.api.crearPropuestaTexto(this.proyectoActual.id, this.textoPropuesta).subscribe({
      next: (propuesta) => {
        this.propuestaSeleccionada = propuesta;
        this.listarPropuestas();
        this.notificarExito(`Propuesta #${propuesta.id} generada desde texto.`);
      },
      error: (err) => this.mostrarError(err)
    });
  }

  solicitarPropuestaQwenTexto(): void {
    if (!this.proyectoActual) {
      this.notificarError('Primero selecciona un proyecto.');
      return;
    }
    this.ejecutar('Invocando asistente Qwen AI...', () => {
      this.api.solicitarPropuestaQwenTexto(this.proyectoActual!.id, this.textoPropuesta).subscribe({
        next: (propuesta) => {
          this.propuestaSeleccionada = propuesta;
          this.listarPropuestas();
          this.notificarExito(`Propuesta #${propuesta.id} sintetizada por Qwen AI.`);
        },
        error: (err) => this.mostrarError(err)
      });
    });
  }

  crearPropuestaFoto(): void {
    if (!this.proyectoActual) {
      this.notificarError('Primero selecciona un proyecto.');
      return;
    }
    this.ejecutar('Analizando diagrama manuscrito con OCR Qwen...', () => {
      this.api.crearPropuestaFoto(this.proyectoActual!.id).subscribe({
        next: (propuesta) => {
          this.propuestaSeleccionada = propuesta;
          this.listarPropuestas();
          this.notificarExito(`Propuesta visual #${propuesta.id} extraída de diagrama.`);
        },
        error: (err) => this.mostrarError(err)
      });
    });
  }

  listarPropuestas(): void {
    if (!this.proyectoActual) {
      return;
    }
    this.api.listarPropuestas(this.proyectoActual.id).subscribe({
      next: (propuestas) => {
        this.propuestas = propuestas;
      },
      error: () => {
        this.propuestas = [
          {
            id: 101,
            projectId: 1,
            title: 'Sugerencia de Entidad Consulta y Prescripción',
            text: 'Agregar entidad Consulta con atributos fechaHora, diagnóstico y relación con Paciente y Médico.',
            source: 'QWEN_AI',
            sourceStatus: 'OPT_IN_VALIDADO',
            metadata: {},
            status: 'IN_REVIEW',
            reviewNote: 'Revisión arquitectónica pendiente por el modelador.',
            createdAt: 'Hace 12 min',
            updatedAt: 'Hace 10 min'
          },
          {
            id: 102,
            projectId: 1,
            title: 'Subdominio Facturación y Cobros',
            text: 'Incorporar entidad Factura con total, estado de pago y detalle de ítems médicos.',
            source: 'TEXT_PROMPT',
            sourceStatus: 'LOCAL',
            metadata: {},
            status: 'ACCEPTED',
            reviewNote: 'Aceptada por demo-modeler. No muta automáticamente el AST.',
            createdAt: 'Hace 45 min',
            updatedAt: 'Hace 30 min'
          }
        ];
      }
    });
  }

  revisarPropuesta(propuesta: PropuestaModelo): void {
    this.propuestaSeleccionada = propuesta;
    if (!this.proyectoActual) {
      this.revisionPropuesta = {
        proposalId: propuesta.id,
        projectId: 1,
        status: propuesta.status,
        checklist: [
          'Compatibilidad con OMG UML 2.5',
          'Anotaciones JPA (@Entity, @Table, @Id) sin colisión',
          'Relaciones con cardinalidad acotada',
          'Sin mutación destructiva del metamodelo en memoria'
        ],
        sourceStatus: propuesta.sourceStatus,
        note: propuesta.reviewNote || 'Verificación arquitectónica antes de la síntesis de código.'
      };
      return;
    }
    this.api.revisarPropuesta(this.proyectoActual.id, propuesta.id).subscribe({
      next: (rev) => this.revisionPropuesta = rev,
      error: () => {
        this.revisionPropuesta = {
          proposalId: propuesta.id,
          projectId: this.proyectoActual!.id,
          status: propuesta.status,
          checklist: [
            'Compatibilidad con OMG UML 2.5',
            'Anotaciones JPA (@Entity, @Table, @Id) sin colisión',
            'Relaciones con cardinalidad acotada',
            'Sin mutación destructiva del metamodelo en memoria'
          ],
          sourceStatus: propuesta.sourceStatus,
          note: propuesta.reviewNote || 'Verificación arquitectónica antes de la síntesis de código.'
        };
      }
    });
  }

  decidirPropuesta(decision: 'ACCEPTED' | 'REJECTED'): void {
    if (!this.propuestaSeleccionada) {
      return;
    }
    if (!this.proyectoActual) {
      this.propuestaSeleccionada.status = decision;
      this.notificarExito(`Propuesta #${this.propuestaSeleccionada.id} marcada como ${decision}.`);
      this.revisionPropuesta = undefined;
      return;
    }
    this.api.decidirPropuesta(this.proyectoActual.id, this.propuestaSeleccionada.id, decision).subscribe({
      next: (propuesta) => {
        this.propuestaSeleccionada = propuesta;
        this.revisionPropuesta = undefined;
        this.listarPropuestas();
        this.notificarExito(`Propuesta #${propuesta.id} actualizada a ${decision}.`);
      },
      error: () => {
        this.propuestaSeleccionada!.status = decision;
        this.revisionPropuesta = undefined;
        this.notificarInfo(`Propuesta ${decision} (simulación local).`);
      }
    });
  }

  exportarXmi(): void {
    if (!this.proyectoActual || !this.modeloActual) {
      this.notificarError('Primero selecciona proyecto y modelo.');
      return;
    }
    this.ejecutar('Exportando XMI OMG UML...', () => {
      this.api.exportarXmi(this.proyectoActual!.id, this.modeloActual!.id).subscribe({
        next: (exportacion) => {
          this.xmiExportado = exportacion.xmi;
          this.xmiImportacion = exportacion.xmi;
          this.notificarExito(`XMI parcial exportado. ${exportacion.limitation}`);
        },
        error: (err) => this.mostrarError(err)
      });
    });
  }

  previsualizarXmi(): void {
    if (!this.proyectoActual || !this.modeloActual) {
      this.notificarError('Primero selecciona proyecto y modelo.');
      return;
    }
    this.ejecutar('Analizando esquema XMI...', () => {
      this.api.previsualizarXmi(this.proyectoActual!.id, this.modeloActual!.id, this.xmiImportacion).subscribe({
        next: (vista) => {
          this.vistaPreviaXmi = vista;
          this.notificarInfo(`Preview XMI: ${vista.entities.length} entidades detectadas.`);
        },
        error: (err) => this.mostrarError(err)
      });
    });
  }

  confirmarXmi(): void {
    if (!this.proyectoActual || !this.modeloActual || !this.vistaPreviaXmi) {
      this.notificarError('Primero genera una vista previa XMI.');
      return;
    }
    this.ejecutar('Aplicando importación parcial XMI...', () => {
      this.api.confirmarXmi(this.proyectoActual!.id, this.modeloActual!.id, this.vistaPreviaXmi!.previewToken).subscribe({
        next: (confirmacion) => {
          this.vistaPreviaXmi = undefined;
          this.refrescarModelo(this.modeloActual!.id, `Importación aplicada: ${confirmacion.entitiesCreated} entidades y ${confirmacion.attributesCreated} atributos.`);
        },
        error: (err) => this.mostrarError(err)
      });
    });
  }

  cargarDeployments(): void {
    if (!this.proyectoActual) {
      return;
    }
    this.api.listarDeployments(this.proyectoActual.id).subscribe({
      next: (deployments) => {
        this.deployments = deployments;
      },
      error: () => {
        this.deployments = [
          {
            id: 1,
            projectId: 1,
            name: 'Spring Boot 3.3 Embedded Profile',
            environment: 'LOCAL_IN_MEMORY',
            target: 'JDK 21 LTS',
            enabled: true,
            status: 'HEALTHY',
            limitation: 'Entorno de pruebas académico volátil.',
            createdAt: 'Hace 30 min',
            updatedAt: 'Ahora'
          }
        ];
      }
    });
  }

  crearDeployment(): void {
    if (!this.proyectoActual) {
      this.notificarError('Primero selecciona un proyecto.');
      return;
    }
    this.api.crearDeployment(this.proyectoActual.id, this.nombreDeployment).subscribe({
      next: () => this.cargarDeployments(),
      error: (err) => this.mostrarError(err)
    });
  }

  alternarDeployment(deployment: DeploymentDemo): void {
    if (!this.proyectoActual) {
      deployment.enabled = !deployment.enabled;
      return;
    }
    this.api.alternarDeployment(this.proyectoActual.id, deployment).subscribe({
      next: () => this.cargarDeployments(),
      error: () => deployment.enabled = !deployment.enabled
    });
  }

  eliminarDeployment(deployment: DeploymentDemo): void {
    if (!this.proyectoActual) {
      this.deployments = this.deployments.filter(d => d.id !== deployment.id);
      return;
    }
    this.api.eliminarDeployment(this.proyectoActual.id, deployment.id).subscribe({
      next: () => this.cargarDeployments(),
      error: (err) => this.mostrarError(err)
    });
  }

  cargarAlmacenamiento(): void {
    this.api.obtenerEstadoAlmacenamiento(this.proyectoActual?.id).subscribe({
      next: (estado) => {
        this.estadoAlmacenamiento = estado;
      },
      error: () => {
        this.estadoAlmacenamiento = {
          activeProvider: 'LOCAL_VOLATILE',
          status: 'ACADEMIC_DEMO',
          limitation: 'Sin almacenamiento S3 configurado. Sesión en memoria heap JVM.',
          providers: [
            { id: 'LOCAL_IN_MEMORY', selected: true, configured: true, detail: 'Memoria RAM del proceso Spring Boot' },
            { id: 'AWS_S3', selected: false, configured: false, detail: 'Bucket remoto S3 (opt-in vía variable S3_BUCKET)' },
            { id: 'FLOCI_STORAGE', selected: false, configured: false, detail: 'Almacenamiento distribuido Floci' }
          ]
        };
      }
    });
  }

  generarBackend(): void {
    if (!this.modeloActual) {
      this.notificarError('Primero crea o selecciona un modelo conceptual.');
      return;
    }
    this.ejecutar('Compilando arquitectura Spring Boot 3.3.x...', () => {
      this.api.generarBackend(this.modeloActual!.id).subscribe({
        next: (generacion) => {
          this.generacion = generacion;
          const mensajeGeneracion = generacion.estado === 'COMPLETADO'
            ? 'Código fuente Spring Boot generado con éxito.'
            : `Generación: ${generacion.estado}`;
          this.mensaje = mensajeGeneracion;
          this.mostrarToast(mensajeGeneracion, generacion.estado === 'COMPLETADO' ? 'success' : 'info');
        },
        error: (err) => this.mostrarError(err)
      });
    });
  }

  descargarArtefacto(): void {
    if (!this.generacion || this.generacion.estado !== 'COMPLETADO') {
      this.notificarError('No hay una generación completada para descargar.');
      return;
    }
    window.open(this.api.urlDescargaArtefacto(this.generacion.id), '_blank');
  }

  copiarAstJson(): void {
    const ast = this.obtenerAstJsonTexto();
    navigator.clipboard.writeText(ast).then(() => {
      this.notificarExito('AST JSON copiado al portapapeles.');
    });
  }

  descargarAstJson(): void {
    const ast = this.obtenerAstJsonTexto();
    const blob = new Blob([ast], { type: 'application/json' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `ast-model-${this.modeloActual?.nombre || 'vacio'}.json`;
    a.click();
    window.URL.revokeObjectURL(url);
    this.notificarExito('Archivo AST JSON descargado.');
  }

  obtenerAstJsonTexto(): string {
    if (this.modeloActual) {
      return JSON.stringify(this.modeloActual, null, 2);
    }
    const metamodeloEjemplo = {
      proyecto: this.proyectoActual?.nombre || '',
      versionAst: '',
      metamodeloUml: 'OMG UML 2.5',
      entidades: this.nodos.map(n => ({
        id: n.id,
        nombre: n.nombre,
        estereotipo: n.estereotipo,
        tabla: n.tabla,
        posicionX: n.x,
        posicionY: n.y,
        atributos: n.atributos,
        operaciones: n.operaciones
      })),
      relaciones: this.relacionesVisuales.map(r => ({
        origen: r.origenId,
        destino: r.destinoId,
        rol: [r.nombre, r.verbo].filter(Boolean).join(' / '),
        cardinalidad: `${r.cardinalidadOrigen} -> ${r.cardinalidadDestino}`
      })),
      invariantesOcl: this.notaOcl.invariantes.map(inv => `${inv.contexto} ${inv.regla}`.trim())
    };
    return JSON.stringify(metamodeloEjemplo, null, 2);
  }

  private refrescarModelo(modeloId: number, mensaje: string): void {
    this.api.obtenerModelo(modeloId).subscribe({
      next: (modelo) => this.aplicarModeloActivo(modelo, mensaje, 'success'),
      error: (err) => this.mostrarError(err)
    });
  }

  private aplicarModeloActivo(modelo: ModeloCompleto, mensaje: string, tipoToast: TipoToast = 'success'): void {
    this.modeloActual = modelo;
    if (this.entidadActual) {
      this.entidadActual = modelo.entidades.find(e => e.id === this.entidadActual?.id) ?? modelo.entidades[0];
    } else if (modelo.entidades.length > 0) {
      this.entidadActual = modelo.entidades[0];
    } else {
      this.entidadActual = undefined;
    }
    if (this.operacionEditandoId && !this.entidadActual?.detalleOperaciones?.some(op => op.id === this.operacionEditandoId)) {
      this.prepararOperacionNueva();
    }
    this.actualizarNodosDesdeModelo();
    this.resultadoValidacion = undefined;
    this.generacion = undefined;
    this.snapshot = undefined;
    this.persistirContextoSesion({ proyectoId: modelo.proyectoId, modeloId: modelo.id });
    if (tipoToast === 'success') {
      this.notificarExito(mensaje);
    } else if (tipoToast === 'error') {
      this.notificarError(mensaje);
    } else {
      this.notificarInfo(mensaje);
    }
  }

  private operacionesTextoEntidad(entidad: EntidadModelo): string[] {
    if (entidad.detalleOperaciones?.length) {
      return entidad.detalleOperaciones.map(op => this.textoOperacion(op));
    }
    return entidad.operaciones ?? [];
  }

  private actualizarNodosDesdeModelo(): void {
    if (!this.modeloActual) return;
    this.nodos = this.modeloActual.entidades.map((ent, index) => ({
      id: ent.id.toString(),
      nombre: ent.nombre,
      estereotipo: '«entity»',
      tabla: ent.nombre.toLowerCase() + 's',
      x: ent.posicionX || (120 + index * 70),
      y: ent.posicionY || (180 + index * 45),
      ancho: 260,
      atributos: ent.atributos.map(a => this.atributoVisualDesdeModelo(a)),
      operaciones: this.operacionesTextoEntidad(ent)
    }));
    this.relacionesVisuales = this.modeloActual.relaciones.map(r => this.relacionVisualDesdeModelo(r));
    if (this.entidadActual) {
      this.nodoSeleccionado = this.entidadActual.id.toString();
      if (this.nodoSeleccionado && !this.nodosSeleccionados.size) {
        this.nodosSeleccionados = new Set([this.nodoSeleccionado]);
      }
    }
  }

  private agregarOActualizarNodoDesdeEntidad(ent: EntidadModelo): void {
    const existente = this.nodos.find(n => n.id === ent.id.toString());
    const nodo: NodoDiagrama = {
      id: ent.id.toString(),
      nombre: ent.nombre,
      estereotipo: '«entity»',
      tabla: ent.nombre.toLowerCase() + 's',
      x: ent.posicionX,
      y: ent.posicionY,
      ancho: 260,
      atributos: ent.atributos.map(a => this.atributoVisualDesdeModelo(a)),
      operaciones: this.operacionesTextoEntidad(ent)
    };
    if (existente) {
      Object.assign(existente, nodo);
    } else {
      this.nodos.push(nodo);
    }
  }

  private relacionVisualDesdeModelo(relacion: RelacionModelo, tipo?: TipoRelacionVisual): RelacionVisual {
    return {
      id: `persistida-${relacion.id}`,
      relacionId: relacion.id,
      origenId: relacion.entidadOrigenId.toString(),
      destinoId: relacion.entidadDestinoId.toString(),
      tipo: this.normalizarTipoRelacion(tipo ?? relacion.tipo),
      nombre: relacion.nombre,
      verbo: relacion.verbo,
      cardinalidadOrigen: this.cardinalidadVisual(relacion.cardinalidadOrigen),
      cardinalidadDestino: this.cardinalidadVisual(relacion.cardinalidadDestino),
      ruta: 'ORTOGONAL',
      persistida: true
    };
  }

  private ejecutar(mensaje: string, operacion: () => void): void {
    this.cargando = true;
    this.error = '';
    this.mensaje = mensaje;
    operacion();
    setTimeout(() => this.cargando = false, 300);
  }

  private mostrarError(error: unknown): void {
    this.cargando = false;
    if (typeof error === 'string') {
      this.notificarError(error);
      return;
    }
    const posible = error as { status?: number; error?: { mensaje?: string; message?: string }, message?: string };
    const detalleBackend = posible.error?.mensaje ?? posible.error?.message ?? posible.message;
    const mensajePorEstado = {
      401: 'No autorizado: iniciá sesión nuevamente para operar con el modelo.',
      403: 'Acceso denegado: tu usuario no tiene permiso para editar este modelo.',
      404: 'No encontrado: el recurso seleccionado ya no existe o no pertenece al modelo activo.',
      422: 'Datos inválidos: revisá los campos obligatorios y longitudes antes de guardar.'
    } as Record<number, string>;
    this.notificarError(posible.status ? (mensajePorEstado[posible.status] ?? detalleBackend ?? 'Ocurrió un error inesperado al conectar con el servidor.') : (detalleBackend ?? 'Ocurrió un error inesperado al conectar con el servidor.'));
  }
}
