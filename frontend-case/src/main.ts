import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { bootstrapApplication } from '@angular/platform-browser';
import { provideAnimations } from '@angular/platform-browser/animations';
import { ComponenteRaiz } from './app/componente-raiz';
import { interceptorIdentidadAcademica } from './app/nucleo/interceptor-identidad-academica';

bootstrapApplication(ComponenteRaiz, {
  providers: [provideHttpClient(withInterceptors([interceptorIdentidadAcademica])), provideAnimations()]
}).catch((error) => console.error(error));
