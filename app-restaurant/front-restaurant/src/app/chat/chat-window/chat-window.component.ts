import { Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChanges, inject, OnDestroy, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MessageCardComponent } from '../message-card/message-card.component';
import { MessageInputComponent } from '../message-input/message-input.component';
import { FormsModule } from '@angular/forms';
import { Message } from '../../shared/models/message.interface';
import { WebSocketService, WebSocketMessage } from '../../shared/services/websocket.service';
import { v4 as uuidv4 } from 'uuid';

@Component({
  selector: 'app-chat-window',
  imports: [CommonModule, MessageCardComponent, MessageInputComponent, FormsModule],
  templateUrl: './chat-window.component.html',
  styleUrl: './chat-window.component.css',
  standalone: true
})
export class ChatWindowComponent implements OnInit, OnChanges, OnDestroy {
  private webSocketService = inject(WebSocketService);
    @Input() messages: Message[] = [];
  @Input() sessionId = '';
  @Input() isLoading = false;
  @Output() send = new EventEmitter<string>();
  @Output() recordingStateChange = new EventEmitter<boolean>();
  @Output() turnComplete = new EventEmitter<void>();
  @Output() wsMessageReceived = new EventEmitter<{text: string, isComplete: boolean, isTranscript?: boolean}>();

  isRecording = false;
  wsMessages: WebSocketMessage[] = [];
  private currentBotMessageId: string | null = null;
  private processedMessageIds = new Set<string>();

  constructor() {
    // Subscribe to WebSocket messages
    effect(() => {
      this.wsMessages = this.webSocketService.messages();
      
      // Process new WebSocket messages
      if (this.wsMessages.length > 0) {
        console.log('Received WebSocket messages:', this.wsMessages);
        this.processWebSocketMessages();
      }
    });
  }

  ngOnInit(): void {
    // Generate a random session ID if none is provided
    if (!this.sessionId) {
      this.sessionId = Math.random().toString(36).substring(2, 15);
      console.log('Generated new sessionId:', this.sessionId);
    }
    
    this.prepareSessionId();
  }  ngOnChanges(changes: SimpleChanges): void {
    // Detect changes in the sessionId property (room change)
    if (changes['sessionId'] && !changes['sessionId'].firstChange) {
      const currentValue = changes['sessionId'].currentValue;
      const previousValue = changes['sessionId'].previousValue;
      
      if (currentValue !== previousValue) {
        console.log(`SessionId changed from ${previousValue} to ${currentValue}`);
        // Reset message tracking for new session
        this.currentBotMessageId = null;
        this.processedMessageIds.clear();
        this.prepareSessionId();
      }
    }
    
    // Depuración detallada de cambios en mensajes
    if (changes['messages']) {
      console.log('===== CAMBIOS EN MENSAJES =====');
      console.log('Previo:', changes['messages'].previousValue?.length || 0, 'mensajes');
      console.log('Actual:', changes['messages'].currentValue?.length || 0, 'mensajes');
      
      // Mostrar detalle de cada mensaje actual
      if (changes['messages'].currentValue) {
        changes['messages'].currentValue.forEach((msg: any, index: number) => {
          console.log(`Mensaje ${index}: ${msg.sender} - ${msg.text.substring(0, 20)}...`);
        });
      }
      
      console.log('¿Es primer cambio?', changes['messages'].firstChange);
      console.log('¿Es mismo array?', changes['messages'].previousValue === changes['messages'].currentValue);
      console.log('Current messages count:', this.messages.length);
      console.log('================================');
    }
  }
  
  ngOnDestroy(): void {
    // Disconnect WebSocket when component is destroyed
    this.webSocketService.disconnect();
  }
    private prepareSessionId(): void {
    // Ensure we have a valid session ID
    if (!this.sessionId) {
      // Generate a random session ID as in the static example
      this.sessionId = Math.random().toString().substring(10);
      console.log('Generated new sessionId:', this.sessionId);
    }
    
    console.log('Preparing sessionId:', this.sessionId);
    // DON'T auto-connect WebSocket for text messages
    // WebSocket will only be used for audio mode when user activates microphone
  }
  private processWebSocketMessages(): void {
    this.wsMessages.forEach(wsMessage => {
      // Skip if already processed
      if (wsMessage.id && this.processedMessageIds.has(wsMessage.id)) {
        return;
      }

      // Handle text messages (both plain and transcript)
      if ((wsMessage.mime_type === 'text/plain' || wsMessage.mime_type === 'text/transcript') && wsMessage.data) {
        this.handleTextMessage(wsMessage);
      }      // Handle turn complete
      if (wsMessage.turn_complete) {
        // Emit final completion of the message
        this.wsMessageReceived.emit({
          text: '',
          isComplete: true // Message is complete
        });
        
        this.currentBotMessageId = null;
        this.turnComplete.emit();
        console.log('WebSocket turn completed');
      }

      // Mark as processed
      if (wsMessage.id) {
        this.processedMessageIds.add(wsMessage.id);
      }
    });
  }  private handleTextMessage(wsMessage: WebSocketMessage): void {
    console.log('Handling WebSocket text message:', wsMessage);
    
    // Check if this is a transcript message (user speech) or a regular message
    const isTranscript = wsMessage.mime_type === 'text/transcript';
    
    // If it's a transcript, emit it as a special transcript message
    if (isTranscript) {
      this.wsMessageReceived.emit({
        text: wsMessage.data,
        isComplete: false,
        isTranscript: true  // Mark this as a transcript message
      });
      console.log('Emitted transcript message from WebSocket');
      return;
    }
    
    // For regular text messages
    if (!this.currentBotMessageId) {
      this.currentBotMessageId = wsMessage.id || uuidv4();
      
      // Emit the new message to be handled by parent component
      this.wsMessageReceived.emit({
        text: wsMessage.data,
        isComplete: false // First chunk of the message
      });
      
      console.log('Created new bot message from WS:', this.currentBotMessageId);
    } else {
      // Append to existing bot message - emit an update
      this.wsMessageReceived.emit({
        text: wsMessage.data,
        isComplete: false // Continuation of the message
      });
      
      console.log('Appended to existing bot message from WS');
    }
  }
  addMessage(text: string) {
    console.log('ChatWindow: addMessage called with text:', text);
    
    // NO modificamos el array de mensajes localmente
    // Los mensajes deben ser controlados por el componente padre
    // El componente padre debe agregar el mensaje al array y volver a pasarlo
    
    // Emitir el mensaje para ser manejado por el componente padre
    this.send.emit(text);
    
    console.log('ChatWindow: después de emit, mensajes actuales:', this.messages.length);
  }
  
  onRecordingChange(isRecording: boolean) {
    this.isRecording = isRecording;
    this.recordingStateChange.emit(isRecording);
  }
}

