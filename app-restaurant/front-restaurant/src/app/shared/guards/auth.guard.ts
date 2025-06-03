import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Observable, from, timer } from 'rxjs';
import { mergeMap, tap } from 'rxjs/operators';

/**
 * Guard que verifica si un usuario está autenticado
 * y redirecciona al login si no lo está
 */
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Si ya está autenticado, permitir acceso inmediato
  if (authService.isAuthenticated()) {
    return true;
  }
  
  // Verificar si existe un elemento de precarga para evitar parpadeos
  const preloader = document.querySelector('.app-loading');
  
  // Retornar un Observable para dar tiempo a la animación
  return new Observable<boolean>(observer => {
    // Tiempo mínimo antes de redireccionar para evitar parpadeos
    // El tiempo es mayor si hay un preloader visible
    const delayTime = preloader ? 800 : 300;
    
    // Esperar el tiempo mínimo antes de redireccionar
    timer(delayTime).subscribe(() => {
      // Si en este punto ya se autenticó (podría haber cambiado), permitir acceso
      if (authService.isAuthenticated()) {
        observer.next(true);
        observer.complete();
        return;
      }
      
      // Redireccionar al login con la URL de retorno
      router.navigate(['/login'], { 
        queryParams: { returnUrl: state.url }
      });
      
      observer.next(false);
      observer.complete();
    });
  });
}; 