import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  AccesoDiagrama,
  AtributoEntidad,
  ConflictoSync,
  ConfirmacionXmi,
  DeploymentDemo,
  DiagramaProyecto,
  EntidadModelo,
  EstadoAlmacenamiento,
  EventoPermiso,
  ExportacionXmi,
  Generacion,
  InvitacionProyecto,
  MiembroProyecto,
  ModeloCompleto,
  ModeloConceptual,
  PresenciaProyecto,
  PropuestaModelo,
  Proyecto,
  RelacionModelo,
  RespuestaAccesoDiagrama,
  RespuestaComandoProyecto,
  RevisionPropuesta,
  ResultadoValidacion,
  OperacionEntidad,
  SesionDemo,
  SnapshotModelo,
  SolicitudAtributo,
  SolicitudComandoProyecto,
  SolicitudEntidad,
  SolicitudModelo,
  SolicitudOperacion,
  SolicitudProyecto,
  SolicitudRelacion,
  SugerenciaCuenta,
  VistaPreviaXmi
} from './modelos-case';

@Injectable({ providedIn: 'root' })
export class ServicioApiCase {
  private readonly baseUrl = 'http://localhost:8080';

  constructor(private readonly http: HttpClient) {}

  login(usuario: string, clave?: string): Observable<SesionDemo> {
    return this.http.post<SesionDemo>(`${this.baseUrl}/auth/login`, { usuario, clave: clave || '' });
  }

  demoLogin(): Observable<SesionDemo> {
    return this.http.post<SesionDemo>(`${this.baseUrl}/auth/demo-login`, {});
  }

  logout(): Observable<unknown> {
    return this.http.post(`${this.baseUrl}/auth/logout`, {});
  }

  crearProyecto(solicitud: SolicitudProyecto): Observable<Proyecto> {
    return this.http.post<Proyecto>(`${this.baseUrl}/proyectos`, solicitud);
  }

  listarProyectos(): Observable<Proyecto[]> {
    return this.http.get<Proyecto[]>(`${this.baseUrl}/proyectos`);
  }

  crearModelo(proyectoId: number, solicitud: SolicitudModelo): Observable<ModeloConceptual> {
    return this.http.post<ModeloConceptual>(`${this.baseUrl}/proyectos/${proyectoId}/modelos`, solicitud);
  }

  obtenerModelo(modeloId: number): Observable<ModeloCompleto> {
    return this.http.get<ModeloCompleto>(`${this.baseUrl}/modelos/${modeloId}`);
  }

  obtenerPrimerModeloDelProyecto(proyectoId: number): Observable<ModeloCompleto> {
    return this.http.get<ModeloCompleto>(`${this.baseUrl}/proyectos/${proyectoId}/modelo`);
  }

  crearEntidad(modeloId: number, solicitud: SolicitudEntidad): Observable<EntidadModelo> {
    return this.http.post<EntidadModelo>(`${this.baseUrl}/modelos/${modeloId}/entidades`, solicitud);
  }

  actualizarEntidad(entidadId: number, solicitud: SolicitudEntidad): Observable<EntidadModelo> {
    return this.http.put<EntidadModelo>(`${this.baseUrl}/entidades/${entidadId}`, solicitud);
  }

  crearAtributo(entidadId: number, solicitud: SolicitudAtributo): Observable<AtributoEntidad> {
    return this.http.post<AtributoEntidad>(`${this.baseUrl}/entidades/${entidadId}/atributos`, solicitud);
  }

  actualizarAtributo(atributoId: number, solicitud: SolicitudAtributo): Observable<AtributoEntidad> {
    return this.http.put<AtributoEntidad>(`${this.baseUrl}/atributos/${atributoId}`, solicitud);
  }

  crearOperacion(entidadId: number, solicitud: SolicitudOperacion): Observable<OperacionEntidad> {
    return this.http.post<OperacionEntidad>(`${this.baseUrl}/entidades/${entidadId}/operaciones`, solicitud);
  }

  actualizarOperacion(operacionId: number, solicitud: SolicitudOperacion): Observable<OperacionEntidad> {
    return this.http.put<OperacionEntidad>(`${this.baseUrl}/operaciones/${operacionId}`, solicitud);
  }

  eliminarOperacion(operacionId: number): Observable<unknown> {
    return this.http.delete(`${this.baseUrl}/operaciones/${operacionId}`);
  }

  crearRelacion(modeloId: number, solicitud: SolicitudRelacion): Observable<RelacionModelo> {
    return this.http.post<RelacionModelo>(`${this.baseUrl}/modelos/${modeloId}/relaciones`, solicitud);
  }

  actualizarRelacion(relacionId: number, solicitud: SolicitudRelacion): Observable<RelacionModelo> {
    return this.http.put<RelacionModelo>(`${this.baseUrl}/relaciones/${relacionId}`, solicitud);
  }

  eliminarRelacion(relacionId: number): Observable<unknown> {
    return this.http.delete(`${this.baseUrl}/relaciones/${relacionId}`);
  }

  validarModelo(modeloId: number): Observable<ResultadoValidacion> {
    return this.http.post<ResultadoValidacion>(`${this.baseUrl}/modelos/${modeloId}/validacion`, {});
  }

  generarBackend(modeloId: number): Observable<Generacion> {
    return this.http.post<Generacion>(`${this.baseUrl}/modelos/${modeloId}/generaciones`, {});
  }

  obtenerSnapshot(modeloId: number): Observable<SnapshotModelo> {
    return this.http.get<SnapshotModelo>(`${this.baseUrl}/modelos/${modeloId}/sync/snapshot`);
  }

  registrarComandoSync(modeloId: number): Observable<unknown> {
    return this.http.post(`${this.baseUrl}/modelos/${modeloId}/sync/commands`, { tipo: 'demo-refresh', payload: {} });
  }

  listarMiembros(projectId: number): Observable<MiembroProyecto[]> {
    return this.http.get<MiembroProyecto[]>(`${this.baseUrl}/projects/${projectId}/members`);
  }

  listarInvitaciones(projectId: number): Observable<InvitacionProyecto[]> {
    return this.http.get<InvitacionProyecto[]>(`${this.baseUrl}/projects/${projectId}/invitations`);
  }

  listarSugerencias(projectId: number): Observable<SugerenciaCuenta[]> {
    return this.http.get<SugerenciaCuenta[]>(`${this.baseUrl}/projects/${projectId}/account-suggestions`);
  }

  listarHistorialProyecto(projectId: number): Observable<EventoPermiso[]> {
    return this.http.get<EventoPermiso[]>(`${this.baseUrl}/projects/${projectId}/permissions/history`);
  }

  enviarComandoProyecto(projectId: number, comando: SolicitudComandoProyecto): Observable<RespuestaComandoProyecto> {
    return this.http.post<RespuestaComandoProyecto>(`${this.baseUrl}/projects/${projectId}/commands`, {
      type: comando.type,
      tipo: comando.type,
      payload: comando.payload ?? {}
    });
  }

  invitarMiembro(projectId: number, userId: string, role: string): Observable<RespuestaComandoProyecto> {
    return this.enviarComandoProyecto(projectId, { type: 'INVITE_MEMBER', payload: { userId, role } });
  }

  aceptarInvitacion(projectId: number, invitationId: string): Observable<RespuestaComandoProyecto> {
    return this.enviarComandoProyecto(projectId, { type: 'ACCEPT_INVITATION', payload: { invitationId } });
  }

  rechazarInvitacion(projectId: number, invitationId: string): Observable<RespuestaComandoProyecto> {
    return this.enviarComandoProyecto(projectId, { type: 'REJECT_INVITATION', payload: { invitationId } });
  }

  revocarInvitacion(projectId: number, invitationId: string): Observable<RespuestaComandoProyecto> {
    return this.enviarComandoProyecto(projectId, { type: 'REVOKE_INVITATION', payload: { invitationId } });
  }

  actualizarMiembro(projectId: number, userId: string, solicitud: { role?: string; status?: string; displayName?: string; canCreateDiagram?: boolean }): Observable<MiembroProyecto> {
    return this.http.patch<MiembroProyecto>(`${this.baseUrl}/projects/${projectId}/members/${userId}`, solicitud);
  }

  actualizarCapacidadCrearDiagrama(projectId: number, userId: string, enabled: boolean): Observable<MiembroProyecto> {
    return this.http.put<MiembroProyecto>(`${this.baseUrl}/projects/${projectId}/members/${userId}/capabilities/create-diagram`, { enabled });
  }

  crearDiagrama(projectId: number, name: string): Observable<DiagramaProyecto> {
    return this.http.post<DiagramaProyecto>(`${this.baseUrl}/projects/${projectId}/diagrams`, { name });
  }

  obtenerAccesoDiagrama(projectId: number, viewId: number): Observable<RespuestaAccesoDiagrama> {
    return this.http.get<RespuestaAccesoDiagrama>(`${this.baseUrl}/projects/${projectId}/diagrams/${viewId}/access`);
  }

  listarHistorialDiagrama(projectId: number, viewId: number): Observable<EventoPermiso[]> {
    return this.http.get<EventoPermiso[]>(`${this.baseUrl}/projects/${projectId}/diagrams/${viewId}/permissions/history`);
  }

  actualizarColaboradorDiagrama(projectId: number, viewId: number, userId: string, solicitud: { role?: string; canEdit?: boolean; canComment?: boolean }): Observable<AccesoDiagrama> {
    return this.http.put<AccesoDiagrama>(`${this.baseUrl}/projects/${projectId}/diagrams/${viewId}/collaborators/${userId}`, solicitud);
  }

  enviarPresencia(projectId: number, modelId: number, userId: string, displayName: string): Observable<PresenciaProyecto> {
    return this.http.post<PresenciaProyecto>(`${this.baseUrl}/projects/${projectId}/sync/presence?modelId=${modelId}`, {
      userId,
      displayName,
      status: 'online',
      cursor: { panel: 'frontend' }
    });
  }

  listarPresencia(projectId: number, modelId: number): Observable<PresenciaProyecto[]> {
    return this.http.get<PresenciaProyecto[]>(`${this.baseUrl}/projects/${projectId}/sync/presence?modelId=${modelId}`);
  }

  crearConflictoDemo(modelId: number, baseRevision: number): Observable<unknown> {
    return this.http.post(`${this.baseUrl}/modelos/${modelId}/sync/commands`, {
      tipo: 'demo-conflict',
      payload: { operationId: `demo-${Date.now()}`, baseRevision }
    });
  }

  listarConflictos(projectId: number, modelId: number): Observable<ConflictoSync[]> {
    return this.http.get<ConflictoSync[]>(`${this.baseUrl}/projects/${projectId}/models/${modelId}/sync/conflicts`);
  }

  resolverConflicto(projectId: number, modelId: number, operationId: string): Observable<ConflictoSync> {
    return this.http.post<ConflictoSync>(`${this.baseUrl}/projects/${projectId}/models/${modelId}/sync/conflicts/${operationId}/resolve`, { resolution: 'accepted-current' });
  }

  listarPropuestas(projectId: number): Observable<PropuestaModelo[]> {
    return this.http.get<PropuestaModelo[]>(`${this.baseUrl}/projects/${projectId}/proposals`);
  }

  crearPropuestaTexto(projectId: number, text: string): Observable<PropuestaModelo> {
    return this.http.post<PropuestaModelo>(`${this.baseUrl}/projects/${projectId}/proposals`, {
      title: 'Propuesta textual',
      text,
      source: 'TEXT',
      sourceStatus: 'READY_FOR_REVIEW',
      metadata: { frontend: 'angular-demo' }
    });
  }

  solicitarPropuestaQwenTexto(projectId: number, text: string): Observable<PropuestaModelo> {
    return this.http.post<PropuestaModelo>(`${this.baseUrl}/projects/${projectId}/proposals/ai-text/qwen`, {
      text,
      requestedBy: 'angular-demo'
    });
  }

  revisarPropuesta(projectId: number, proposalId: number): Observable<RevisionPropuesta> {
    return this.http.get<RevisionPropuesta>(`${this.baseUrl}/projects/${projectId}/proposals/${proposalId}/review`);
  }

  decidirPropuesta(projectId: number, proposalId: number, decision: 'ACCEPTED' | 'REJECTED'): Observable<PropuestaModelo> {
    return this.http.post<PropuestaModelo>(`${this.baseUrl}/projects/${projectId}/proposals/${proposalId}/review-decision`, { decision, reviewer: 'frontend-demo' });
  }

  exportarXmi(projectId: number, modelId: number): Observable<ExportacionXmi> {
    return this.http.post<ExportacionXmi>(`${this.baseUrl}/projects/${projectId}/models/${modelId}/xmi/export`, {});
  }

  previsualizarXmi(projectId: number, modelId: number, xmi: string): Observable<VistaPreviaXmi> {
    return this.http.post<VistaPreviaXmi>(`${this.baseUrl}/projects/${projectId}/models/${modelId}/xmi/import/preview`, { xmi });
  }

  confirmarXmi(projectId: number, modelId: number, previewToken: string): Observable<ConfirmacionXmi> {
    return this.http.post<ConfirmacionXmi>(`${this.baseUrl}/projects/${projectId}/models/${modelId}/xmi/import/confirm`, { previewToken, confirmPartialImport: true });
  }

  listarDeployments(projectId: number): Observable<DeploymentDemo[]> {
    return this.http.get<DeploymentDemo[]>(`${this.baseUrl}/projects/${projectId}/deployments`);
  }

  crearDeployment(projectId: number, name: string): Observable<DeploymentDemo> {
    return this.http.post<DeploymentDemo>(`${this.baseUrl}/projects/${projectId}/deployments`, { name, environment: 'demo', target: 'local-artifact' });
  }

  alternarDeployment(projectId: number, deployment: DeploymentDemo): Observable<DeploymentDemo> {
    const action = deployment.enabled ? 'disable' : 'enable';
    return this.http.post<DeploymentDemo>(`${this.baseUrl}/projects/${projectId}/deployments/${deployment.id}/${action}`, {});
  }

  eliminarDeployment(projectId: number, deploymentId: number): Observable<unknown> {
    return this.http.delete(`${this.baseUrl}/projects/${projectId}/deployments/${deploymentId}`);
  }

  obtenerEstadoAlmacenamiento(projectId?: number): Observable<EstadoAlmacenamiento> {
    const path = projectId ? `/projects/${projectId}/storage/status` : '/storage/status';
    return this.http.get<EstadoAlmacenamiento>(`${this.baseUrl}${path}`);
  }

  crearPropuestaFoto(projectId: number): Observable<PropuestaModelo> {
    const formData = new FormData();
    return this.http.post<PropuestaModelo>(`${this.baseUrl}/projects/${projectId}/proposals/photo`, formData);
  }

  abrirEventosProyecto(projectId: number, modelId: number): EventSource {
    return new EventSource(`${this.baseUrl}/projects/${projectId}/sync/events?modelId=${modelId}`);
  }

  urlDescargaArtefacto(generacionId: number): string {
    return `${this.baseUrl}/generaciones/${generacionId}/artefacto`;
  }
}
