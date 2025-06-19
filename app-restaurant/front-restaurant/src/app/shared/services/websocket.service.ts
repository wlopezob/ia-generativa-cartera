import { Injectable, signal } from '@angular/core';
import { environment } from '../../../environments/environment';

export interface WebSocketMessage {
  id?: string; // Added for message tracking like in static implementation
  mime_type: string;
  data: string;
  turn_complete?: boolean;
  interrupted?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private socket: WebSocket | null = null;
  private micStream: MediaStream | null = null;
  
  // Audio player components (like static implementation)
  private audioPlayerContext: AudioContext | null = null;
  private audioPlayerNode: AudioWorkletNode | null = null;
  
  // Audio recorder components (like static implementation)  
  private audioRecorderContext: AudioContext | null = null;
  private audioRecorderNode: AudioWorkletNode | null = null;
  private isAudioWorkletReady = false;

  public isConnected = signal(false);
  public isRecording = signal(false);
  public isPlaying = signal(false);
  public messages = signal<WebSocketMessage[]>([]);
  public currentMessageId = signal<string | null>(null);

  constructor() {}

  // Use the same URL strategy as static implementation
  private getWsUrl(): string {
    return environment.wsUrl;
  }

  connect(sessionId: string, isAudio: boolean = false): void {
    // Close any existing connection
    this.disconnect();

    // Create WebSocket URL using the same strategy as static implementation
    const wsUrl = this.getWsUrl() + "/" + sessionId + "?is_audio=" + isAudio;
    
    console.log('Connecting to WebSocket URL:', wsUrl);
    
    // Si es modo audio, inicializar el AudioWorklet
    if (isAudio) {
      this.initializeAudioWorklet();
    }
    
    // Connect websocket
    try {
      this.socket = new WebSocket(wsUrl);

      // Handle connection open
      this.socket.onopen = () => {
        console.log('WebSocket connection opened');
        this.isConnected.set(true);
      };

      // Handle incoming messages
      this.socket.onmessage = (event) => {
        try {
          const messageFromServer = JSON.parse(event.data) as WebSocketMessage;
          console.log('[AGENT TO CLIENT] ', messageFromServer);

          // Check if the turn is complete
          if (messageFromServer.turn_complete) {
            this.currentMessageId.set(null);
            // Limpiar el buffer de audio cuando termina el turno
            if (this.audioPlayerNode) {
              this.audioPlayerNode.port.postMessage({ command: 'endOfAudio' });
            }
            this.isPlaying.set(false);
            return;
          }

          // Check for interrupt message
          if (messageFromServer.interrupted) {
            console.log('Message was interrupted');
            this.stopAudioPlayback();
            return;
          }

          // Handle different message types
          if (messageFromServer.mime_type === 'audio/pcm') {
            // Enviar directamente al AudioWorklet
            if (this.audioPlayerNode && this.isAudioWorkletReady) {
              const audioData = this.base64ToArray(messageFromServer.data);
              this.audioPlayerNode.port.postMessage(audioData);
              this.isPlaying.set(true);
            }
          } else if (messageFromServer.mime_type === 'text/plain') {
            console.log('Received text message:', messageFromServer.data);
            
            // Add message to the list - similar to static implementation
            // Add a new message for a new turn
            if (this.currentMessageId() == null) {
              const newMessageId = Math.random().toString(36).substring(7);
              this.currentMessageId.set(newMessageId);
              // Start new message
              this.messages.update(msgs => [...msgs, { 
                id: newMessageId,
                mime_type: 'text/plain', 
                data: messageFromServer.data,
                turn_complete: false 
              }]);
            } else {
              // Append to existing message
              this.messages.update(msgs => {
                const currentId = this.currentMessageId();
                return msgs.map(msg => 
                  msg.id === currentId 
                    ? { ...msg, data: msg.data + messageFromServer.data }
                    : msg
                );
              });
            }
          } else if (messageFromServer.mime_type === 'text/transcript') {
            // Handle transcript messages (user's speech transcript)
            console.log('Received transcript:', messageFromServer.data);
            
            // Add transcript as a new message with a special ID
            // This will ensure it's displayed as user's message in the chat
            this.messages.update(msgs => [...msgs, { 
              id: 'transcript-' + Date.now(),
              mime_type: 'text/transcript', 
              data: messageFromServer.data,
              turn_complete: false 
            }]);
          }
        } catch (error) {
          console.error('Error processing WebSocket message:', error);
        }
      };

      // Handle connection close
      this.socket.onclose = () => {
        console.log('WebSocket connection closed');
        this.isConnected.set(false);
        
        // Attempt to reconnect after 5 seconds (same as static implementation)
        setTimeout(() => {
          if (!this.isConnected()) {
            console.log('Reconnecting...');
            this.connect(sessionId, isAudio);
          }
        }, 5000);
      };

      // Handle errors
      this.socket.onerror = (error) => {
        console.error('WebSocket error:', error);
      };
    } catch (error) {
      console.error('Error connecting to WebSocket:', error);
    }
  }

  // Inicializar AudioWorklet para reproducción fluida usando archivos externos
  private async initializeAudioWorklet(): Promise<void> {
    try {
      if (!this.audioPlayerContext) {
        const AudioContextClass = window.AudioContext || (window as any).webkitAudioContext;
        this.audioPlayerContext = new AudioContextClass({ sampleRate: 24000 });
      }

      // Cargar el procesador desde archivo externo (como en static)
      await this.audioPlayerContext.audioWorklet.addModule('/assets/worklets/pcm-player-processor.js');

      // Crear el nodo AudioWorklet
      this.audioPlayerNode = new AudioWorkletNode(this.audioPlayerContext, 'pcm-player-processor');
      this.audioPlayerNode.connect(this.audioPlayerContext.destination);
      this.isAudioWorkletReady = true;

      console.log('AudioWorklet initialized successfully');
    } catch (error) {
      console.error('Error initializing AudioWorklet:', error);
      // Fallback al método anterior si falla
      this.isAudioWorkletReady = false;
    }
  }

  disconnect(): void {
    if (this.socket) {
      this.socket.close();
      this.socket = null;
    }
    this.isConnected.set(false);
    this.stopAudioRecording();
    this.stopAudioPlayback();
    
    // Limpiar AudioWorklets y contextos
    if (this.audioPlayerNode) {
      this.audioPlayerNode.disconnect();
      this.audioPlayerNode = null;
    }
    
    if (this.audioPlayerContext) {
      this.audioPlayerContext.close().catch(err => {
        console.error('Error al cerrar AudioPlayer:', err);
      });
      this.audioPlayerContext = null;
    }
    
    this.isAudioWorkletReady = false;
  }

  sendMessage(message: WebSocketMessage): void {
    if (this.socket && this.socket.readyState === WebSocket.OPEN) {
      const messageJson = JSON.stringify(message);
      this.socket.send(messageJson);
      console.log('[CLIENT TO AGENT]', message);
    } else {
      console.error('WebSocket is not connected');
    }
  }

  // Method to request microphone permission without starting recording
  async requestMicrophonePermission(): Promise<boolean> {
    if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
      console.error('This browser does not support the Media Devices API');
      return false;
    }
    
    try {
      console.log('Requesting microphone permissions...');
      const stream = await navigator.mediaDevices.getUserMedia({
        audio: true
      });
      
      // Immediately stop the stream, we just wanted permissions
      stream.getTracks().forEach(track => track.stop());
      console.log('Microphone permissions granted');
      return true;
    } catch (error) {
      console.error('Error requesting microphone permissions:', error);
      return false;
    }
  }

  async startAudioRecording(): Promise<void> {
    if (this.isRecording()) return;
    if (!this.socket || this.socket.readyState !== WebSocket.OPEN) return;

    console.log('Starting audio recording...');
    
    try {
      // Request microphone with same settings as static implementation
      this.micStream = await navigator.mediaDevices.getUserMedia({ 
        audio: { channelCount: 1 } 
      });
      
      // Create AudioContext with same settings as static implementation
      const AudioContextClass = window.AudioContext || (window as any).webkitAudioContext;
      this.audioRecorderContext = new AudioContextClass({ sampleRate: 16000 });
      console.log("AudioContext sample rate:", this.audioRecorderContext.sampleRate);

      // Load AudioWorklet module
      await this.audioRecorderContext.audioWorklet.addModule('/assets/worklets/pcm-recorder-processor.js');

      // Create AudioWorkletNode and connect microphone
      this.audioRecorderNode = new AudioWorkletNode(this.audioRecorderContext, 'pcm-recorder-processor');
      const source = this.audioRecorderContext.createMediaStreamSource(this.micStream);
      source.connect(this.audioRecorderNode);

      this.audioRecorderNode.port.onmessage = (event) => {
        if (!this.isConnected() || !this.isRecording()) return;
        const inputData = event.data;
        const pcmData = this.convertFloat32ToPCM(inputData);
        this.sendMessage({
          mime_type: 'audio/pcm',
          data: this.arrayBufferToBase64(pcmData)
        });
      };

      this.isRecording.set(true);
      console.log('Audio recording started successfully');
    } catch (error) {
      console.error('Error starting audio recording:', error);
      throw error;
    }
  }

  stopAudioRecording(): void {
    console.log('WebSocketService: stopAudioRecording called');
    
    if (this.audioRecorderNode) {
      this.audioRecorderNode.disconnect();
      this.audioRecorderNode = null;
    }
    if (this.micStream) {
      this.micStream.getTracks().forEach(track => track.stop());
      this.micStream = null;
    }
    if (this.audioRecorderContext) {
      try {
        this.audioRecorderContext.close();
      } catch (error) {
        console.error('Error closing audioRecorderContext:', error);
      }
      this.audioRecorderContext = null;
    }
    this.isRecording.set(false);
    console.log('WebSocketService: recording stopped, state updated');
  }

  // Detiene la reproducción de audio
  stopAudioPlayback(): void {
    // Limpiar el buffer de audio
    if (this.audioPlayerNode) {
      this.audioPlayerNode.port.postMessage({ command: 'endOfAudio' });
    }
    
    this.isPlaying.set(false);
  }
  
  // Calcula el nivel de volumen de un buffer de audio
  private calculateVolumeLevel(buffer: Float32Array): number {
    let sum = 0;
    for (let i = 0; i < buffer.length; i++) {
      sum += Math.abs(buffer[i]);
    }
    return sum / buffer.length;
  }

  // Convert Float32 samples to 16-bit PCM
  private convertFloat32ToPCM(inputData: Float32Array): ArrayBuffer {
    const pcm16 = new Int16Array(inputData.length);
    for (let i = 0; i < inputData.length; i++) {
      // Multiply by 0x7fff (32767) to scale the float value to 16-bit PCM range
      pcm16[i] = inputData[i] * 0x7fff;
    }
    // Return the underlying ArrayBuffer
    return pcm16.buffer;
  }

  // Encode an array buffer with Base64
  private arrayBufferToBase64(buffer: ArrayBuffer): string {
    let binary = '';
    const bytes = new Uint8Array(buffer);
    const len = bytes.byteLength;
    for (let i = 0; i < len; i++) {
      binary += String.fromCharCode(bytes[i]);
    }
    return window.btoa(binary);
  }

  // Decode Base64 data to Array
  private base64ToArray(base64: string): ArrayBuffer {
    const binaryString = window.atob(base64);
    const len = binaryString.length;
    const bytes = new Uint8Array(len);
    for (let i = 0; i < len; i++) {
      bytes[i] = binaryString.charCodeAt(i);
    }
    return bytes.buffer;
  }

  // Method to start audio mode (similar to static implementation)
  async startAudio(): Promise<void> {
    try {
      console.log('Starting audio system...');
      
      // Initialize audio player worklet (like static implementation)
      await this.initializeAudioWorklet();
      
      // Request microphone permissions early (like static implementation)
      const hasPermission = await this.requestMicrophonePermission();
      if (!hasPermission) {
        throw new Error('Microphone permission denied');
      }
      
      console.log('Audio system initialized successfully');
    } catch (error) {
      console.error('Error starting audio mode:', error);
      throw error;
    }
  }
}