import { Component, Output, EventEmitter, Input, inject, OnInit, OnDestroy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { WebSocketService } from '../../shared/services/websocket.service';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-message-input',
  imports: [FormsModule],
  templateUrl: './message-input.component.html',
  styleUrl: './message-input.component.css',
  standalone: true
})
export class MessageInputComponent implements OnInit, OnDestroy {
  private webSocketService = inject(WebSocketService);
  
  messageText = '';
  isRecording = false;
  isPlaying = false;
  
  @Input() isLoading = false;
  @Input() sessionId = '';
  @Output() send = new EventEmitter<string>();
  @Output() recordingChange = new EventEmitter<boolean>();

  private playingSubscription: any;
  
  ngOnInit() {
    // Suscribirse al estado de reproducción del servicio
    this.updatePlayingState();
  }
  
  ngOnDestroy() {
    // Limpiar suscripciones
    this.updatePlayingState(true);
  }
  
  private updatePlayingState(unsubscribe = false) {
    if (unsubscribe && this.playingSubscription) {
      clearInterval(this.playingSubscription);
      this.playingSubscription = null;
      return;
    }
    
    // Usar interval para verificar el estado de reproducción regularmente
    if (!this.playingSubscription) {
      this.playingSubscription = setInterval(() => {
        this.isPlaying = this.webSocketService.isPlaying();
      }, 100);
    }
  }

  onSend() {
    if ((this.messageText.trim() || this.isRecording) && !this.isLoading && !this.isPlaying) {
      // If we have text, send it to parent component for handling
      if (this.messageText.trim()) {
        // Emit the message for parent component to handle REST API call
        this.send.emit(this.messageText);
        this.messageText = '';
      }
      
      // If we're recording, stop it when sending
      if (this.isRecording) {
        this.toggleRecording();
      }
    }
  }
  
  toggleRecording() {
    // Si está reproduciendo audio, no permitir cambios
    if (this.isPlaying) {
      return;
    }
    
    console.log('Toggling recording, current state:', this.isRecording);
    
    if (!this.isRecording) {
      // Start recording
      this.startRecording();
    } else {
      // Stop recording
      this.stopRecording();
    }
  }
  
  private async startRecording() {
    console.log('Starting recording, sessionId:', this.sessionId);
    
    if (!this.sessionId) {
      console.error('No sessionId defined to start recording');
      return;
    }
    
    try {
      // Step 1: Initialize audio system first (like static implementation)
      console.log('Initializing audio system...');
      await this.webSocketService.startAudio();
      
      // Step 2: Connect WebSocket with audio enabled (like static implementation) 
      console.log('Connecting WebSocket with audio enabled...');
      this.webSocketService.connect(this.sessionId, true);
      
      // Small delay to allow connection to establish
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      if (!this.webSocketService.isConnected()) {
        console.error('Failed to connect WebSocket');
        return;
      }
      
      // Step 3: Start audio recording
      console.log('Starting audio recording...');
      await this.webSocketService.startAudioRecording();
      
      this.isRecording = true;
      this.recordingChange.emit(true);
      console.log('Recording started successfully');
    } catch (error) {
      console.error('Error starting recording:', error);
      this.isRecording = false;
      this.recordingChange.emit(false);
      // Show user-friendly error message
      alert('Error al activar el micrófono. Por favor, asegúrate de que has dado permisos de micrófono al navegador.');
    }
  }
  
  private stopRecording() {
    console.log('Stopping recording');
    
    // Primero detener la grabación de audio
    this.webSocketService.stopAudioRecording();
    
    // Actualizar estado de UI inmediatamente
    this.isRecording = false;
    this.recordingChange.emit(false);
    
    console.log('Recording stopped successfully');
  }
}
