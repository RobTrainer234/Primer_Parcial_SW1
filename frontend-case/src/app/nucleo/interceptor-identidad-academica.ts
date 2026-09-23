import { HttpInterceptorFn } from '@angular/common/http';

interface SesionAcademicaPersistida {
  usuarioId?: string;
  tokenDemo?: string;
}

const CLAVE_SESION_PERSISTIDA = 'case_sesion_activa';

export const interceptorIdentidadAcademica: HttpInterceptorFn = (req, next) => {
  const sesion = leerSesionPersistida();
  if (!sesion?.usuarioId || !sesion.tokenDemo) {
    return next(req);
  }

  return next(req.clone({
    setHeaders: {
      Authorization: `Bearer ${sesion.tokenDemo}`,
      'X-User-Id': sesion.usuarioId
    }
  }));
};

function leerSesionPersistida(): SesionAcademicaPersistida | undefined {
  try {
    const raw = localStorage.getItem(CLAVE_SESION_PERSISTIDA);
    if (!raw) return undefined;
    const parsed = JSON.parse(raw) as SesionAcademicaPersistida;
    if (!parsed.usuarioId || !parsed.tokenDemo) return undefined;
    return parsed;
  } catch {
    return undefined;
  }
}
