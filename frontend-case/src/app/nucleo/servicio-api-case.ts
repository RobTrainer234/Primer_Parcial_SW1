import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  AtributoEntidad,
  EntidadModelo,
  Generacion,
  ModeloCompleto,
  ModeloConceptual,
  Proyecto,
  ResultadoValidacion,
  SolicitudAtributo,
  SolicitudEntidad,
  SolicitudModelo,
  SolicitudProyecto
} from './modelos-case';

@Injectable({ providedIn: 'root' })
export class ServicioApiCase {
  private readonly baseUrl = 'http://localhost:8080';

  constructor(private readonly http: HttpClient) {}

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

  crearEntidad(modeloId: number, solicitud: SolicitudEntidad): Observable<EntidadModelo> {
    return this.http.post<EntidadModelo>(`${this.baseUrl}/modelos/${modeloId}/entidades`, solicitud);
  }

  crearAtributo(entidadId: number, solicitud: SolicitudAtributo): Observable<AtributoEntidad> {
    return this.http.post<AtributoEntidad>(`${this.baseUrl}/entidades/${entidadId}/atributos`, solicitud);
  }

  validarModelo(modeloId: number): Observable<ResultadoValidacion> {
    return this.http.post<ResultadoValidacion>(`${this.baseUrl}/modelos/${modeloId}/validacion`, {});
  }

  generarBackend(modeloId: number): Observable<Generacion> {
    return this.http.post<Generacion>(`${this.baseUrl}/modelos/${modeloId}/generaciones`, {});
  }

  urlDescargaArtefacto(generacionId: number): string {
    return `${this.baseUrl}/generaciones/${generacionId}/artefacto`;
  }
}
