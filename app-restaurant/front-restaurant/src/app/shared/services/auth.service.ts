import { Injectable, PLATFORM_ID, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { catchError, map, tap, delay } from 'rxjs/operators';
import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';

interface SecretKeyRequest {
  secretKey: string;
}

interface SecretKeyResponse {
  secretInterno: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly SECRET_KEY = 'legalaco_secret_token';
  private readonly SECRET_INTERNO_KEY = 'legalaco_secret_interno';
  private authSubject = new BehaviorSubject<boolean>(false);
  private isBrowser: boolean;
  
  // Observable for components to subscribe to auth state changes
  public isAuthenticated$: Observable<boolean> = this.authSubject.asObservable();
  
  constructor(
    private http: HttpClient,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
    this.checkInitialAuthStatus();
  }
  
  /**
   * Checks if there's a saved token on app initialization
   */
  private checkInitialAuthStatus(): void {
    if (!this.isBrowser) {
      this.authSubject.next(false);
      return;
    }
    
    const savedSecretInterno = localStorage.getItem(this.SECRET_INTERNO_KEY);
    if (savedSecretInterno) {
      this.authSubject.next(true);
    } else {
      this.authSubject.next(false);
    }
  }
  
  /**
   * Generates a UUID v4
   * @returns A UUID string
   */
  private generateUUID(): string {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
      const r = Math.random() * 16 | 0;
      const v = c === 'x' ? r : (r & 0x3 | 0x8);
      return v.toString(16);
    });
  }
  
  /**
   * Validates secret key with mocked authentication
   * @param secretKey The secret key to validate
   * @returns Observable with validation result
   */
  validateSecret(secretKey: string): Observable<SecretKeyResponse> {
    if (!secretKey || secretKey.trim() === '') {
      return throwError(() => new Error('El token secreto es requerido'));
    }
    
    // Mock validation - accept any non-empty secret key
    if (secretKey.length < 4) {
      return throwError(() => new Error('El token debe tener al menos 4 caracteres'));
    }
    
    // Generate a mock UUID token
    const mockSecretInterno = this.generateUUID();
    const response: SecretKeyResponse = { secretInterno: mockSecretInterno };
    
    // Simulate network delay and return successful response
    return of(response).pipe(
      delay(500), // Simulate 500ms network delay
      tap(response => {
        if (response.secretInterno) {
          if (this.isBrowser) {
            localStorage.setItem(this.SECRET_KEY, secretKey);
            localStorage.setItem(this.SECRET_INTERNO_KEY, response.secretInterno);
          }
          this.authSubject.next(true);
        }
      })
    );
  }
  
  /**
   * Get the stored secret key
   * @returns The secret key or null if not found
   */
  getSecretKey(): string | null {
    if (!this.isBrowser) {
      return null;
    }
    
    return localStorage.getItem(this.SECRET_KEY);
  }
  
  /**
   * Get the stored secret interno
   * @returns The secret interno or null if not found
   */
  getSecretInterno(): string | null {
    if (!this.isBrowser) {
      return null;
    }
    
    return localStorage.getItem(this.SECRET_INTERNO_KEY);
  }
  
  /**
   * Check if the user is currently authenticated
   * @returns True if authenticated, false otherwise
   */
  isAuthenticated(): boolean {
    if (!this.isBrowser) {
      return false;
    }
    
    return !!localStorage.getItem(this.SECRET_INTERNO_KEY);
  }
  
  /**
   * Log out the user by removing the secret tokens
   */
  logout(): void {
    if (this.isBrowser) {
      localStorage.removeItem(this.SECRET_KEY);
      localStorage.removeItem(this.SECRET_INTERNO_KEY);
    }
    this.authSubject.next(false);
    this.router.navigate(['/login']);
  }
} 