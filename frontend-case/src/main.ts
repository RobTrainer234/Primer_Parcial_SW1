import { bootstrapApplication } from '@angular/platform-browser';
import { provideHttpClient } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { ComponenteRaiz } from './app/componente-raiz';

bootstrapApplication(ComponenteRaiz, {
  providers: [provideHttpClient(), provideAnimations()]
}).catch((error) => console.error(error));
