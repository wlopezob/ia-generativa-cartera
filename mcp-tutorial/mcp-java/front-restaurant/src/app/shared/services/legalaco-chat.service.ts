import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LegalacoChatRequest } from '../models/legalaco-chat-request.interface';
import { LegalacoChatResponse } from '../models/legalaco-chat-response.interface';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class LegalacoChatService {
  private readonly apiBaseUrl = environment.apiBaseUrl;
  private readonly apiEndpoint = '/legalaco/chat';

  constructor(private http: HttpClient) {}

  sendMessage(request: LegalacoChatRequest): Observable<LegalacoChatResponse> {
    const url = `${this.apiBaseUrl}${this.apiEndpoint}`;
    return this.http.post<LegalacoChatResponse>(url, request);
  }
} 