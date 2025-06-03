import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ChatWithMemoryRequest } from '../models/chat-with-memory-request.interface';
import { ChatResponse } from '../models/chat-response.interface';
import { environment } from '../../../environments/environment';

@Injectable({ 
  providedIn: 'root' 
})
export class RestaurantChatService {
  private readonly apiBaseUrl = environment.apiBaseUrl;
  private readonly chatMemoryEndpoint = '/api/restaurant/chat/memory';

  constructor(private http: HttpClient) {}

  /**
   * Envía un mensaje al chat con memoria del restaurante
   * @param request Datos del mensaje y sesión
   * @returns Observable con la respuesta del chat
   */
  sendMessageWithMemory(request: ChatWithMemoryRequest): Observable<ChatResponse> {
    const url = `${this.apiBaseUrl}${this.chatMemoryEndpoint}`;
    return this.http.post<ChatResponse>(url, request);
  }
} 