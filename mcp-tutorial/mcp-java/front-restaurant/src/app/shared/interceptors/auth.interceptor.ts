import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

/**
 * Auth interceptor that adds the secret interno token to outgoing requests
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  
  // Get the secret interno from auth service
  const secretInterno = authService.getSecretInterno();
  
  // Clone the request and add the authorization header if token exists
  if (secretInterno) {
    const authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${secretInterno}`
      }
    });
    return next(authReq);
  }
  
  return next(req);
}; 