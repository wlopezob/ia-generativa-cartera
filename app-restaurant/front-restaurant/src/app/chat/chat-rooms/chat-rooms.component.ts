import { Component, ChangeDetectorRef, inject } from '@angular/core';
import { v4 as uuidv4 } from 'uuid';
import { ChatWindowComponent } from '../chat-window/chat-window.component';
import { ChatRoom } from '../../shared/models/chat-room.interface';
import { Message } from '../../shared/models/message.interface';
import { RestaurantChatService } from '../../shared/services/restaurant-chat.service';
import { ChatWithMemoryRequest } from '../../shared/models/chat-with-memory-request.interface';
import { ConfirmModalComponent } from '../../shared/components/confirm-modal/confirm-modal.component';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { WebSocketService } from '../../shared/services/websocket.service';

@Component({
  selector: 'app-chat-rooms',
  standalone: true,
  imports: [ChatWindowComponent, ConfirmModalComponent],
  templateUrl: './chat-rooms.component.html',
  styleUrls: ['./chat-rooms.component.css']
})
export class ChatRoomsComponent {
  private webSocketService = inject(WebSocketService);
  
  rooms: ChatRoom[] = [];
  activeRoomIndex = 0;
  showDeleteConfirmModal = false;
  roomToDeleteIndex: number | null = null;
  constructor(
    private cdr: ChangeDetectorRef,
    private restaurantChatService: RestaurantChatService
  ) {}

  addRoom() {
    const roomId = uuidv4();
    console.log('Creating new room with UUID:', roomId);
    
    this.rooms.push({
      uuid: roomId,
      name: `Sala ${this.rooms.length + 1}`,
      messages: [], // Garantiza que cada sala tenga su propio array de mensajes
      lastMessage: 'Sin mensajes',
      lastMessageSender: '',
      lastTime: '-',
      isLoading: false
    });
    this.activeRoomIndex = this.rooms.length - 1;
  }

  confirmDeleteRoom(index: number) {
    this.roomToDeleteIndex = index;
    this.showDeleteConfirmModal = true;
  }

  cancelDeleteRoom() {
    this.showDeleteConfirmModal = false;
    this.roomToDeleteIndex = null;
  }

  deleteRoomConfirmed() {
    if (this.roomToDeleteIndex !== null) {
      this.rooms.splice(this.roomToDeleteIndex, 1);
      if (this.activeRoomIndex >= this.rooms.length) {
        this.activeRoomIndex = Math.max(0, this.rooms.length - 1);
      }
      this.showDeleteConfirmModal = false;
      this.roomToDeleteIndex = null;
    }
  }

  removeRoom(index: number) {
    this.confirmDeleteRoom(index);
  }

  setActiveRoom(index: number) {
    console.log(`Changing active room from ${this.activeRoomIndex} to ${index}`);
    console.log(`Room UUID: ${this.rooms[index].uuid}`);
    this.activeRoomIndex = index;
    // Forzar detección de cambios para asegurar que el sessionId se actualice
    this.cdr.detectChanges();
  }  handleSendMessage(text: string) {
    if (this.rooms.length === 0) {
      return;
    }
    
    // Prevent sending if already loading
    const room = this.rooms[this.activeRoomIndex];
    if (room.isLoading) {
      return;
    }
    
    const roomUuid = room.uuid;
    const now = new Date();
    const time = now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    
    console.log(`Message sent: "${text}" in room: ${room.uuid}`);
    
    // Set loading state for this specific room
    room.isLoading = true;
      // Add user message to room
    const userMessage: Message = {
      uuid: uuidv4(),
      sender: 'Tú',
      avatar: 'https://randomuser.me/api/portraits/men/1.jpg',
      time,
      text,
      images: [],
      link: '',
      file: null
    };
      console.log('Adding user message to room:', room.uuid);
    console.log('User message:', userMessage);
    
    room.messages.push(userMessage);
    // Force array reference change for change detection
    room.messages = [...room.messages];
    room.lastMessage = text;
    room.lastMessageSender = userMessage.sender;
    room.lastTime = time;
    
    console.log('Room messages after adding user message:', room.messages.length);
    
    // Force change detection after adding user message
    this.cdr.detectChanges();
    
    // Create request for REST API
    const request: ChatWithMemoryRequest = {
      message: text,
      sessionId: roomUuid,
      userId: 'default-user',
      sessionName: room.name
    };
    
    // Send to REST API
    this.restaurantChatService.sendMessageWithMemory(request)
      .pipe(
        catchError(error => {
          console.error('Error sending message:', error);
          
          // Find the room by UUID in case user has switched rooms
          const targetRoom = this.rooms.find(r => r.uuid === roomUuid);
          if (targetRoom) {
            // Add error message
            const errorMessage: Message = {
              uuid: uuidv4(),
              sender: 'Sistema',
              avatar: '/robot-avatar.svg',
              time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
              text: 'Error al comunicarse con el servidor. Por favor, intenta de nuevo más tarde.',
              images: [],
              link: '',
              file: null
            };
            
            targetRoom.messages.push(errorMessage);
            targetRoom.lastMessage = errorMessage.text;
            targetRoom.lastMessageSender = errorMessage.sender;
            targetRoom.lastTime = errorMessage.time;
            
            // End loading state
            targetRoom.isLoading = false;
          }
            return of(null);
        })
      )
      .subscribe(response => {
        console.log('API Response received:', response);
        
        // Find the room by UUID in case user has switched rooms
        const targetRoom = this.rooms.find(r => r.uuid === roomUuid);
        if (!targetRoom) {
          console.log('Target room not found for UUID:', roomUuid);
          return;
        }
        
        if (!response) {
          console.log('No response data received');
          return;
        }
        
        console.log('Processing response for room:', targetRoom.uuid);
        
        // End loading state
        targetRoom.isLoading = false;
        
        // Add bot response message
        const botMessage: Message = {
          uuid: uuidv4(),
          sender: 'ChefAPP',
          avatar: '/robot-avatar.svg',
          time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
          text: response.response,
          images: [],
          link: '',
          file: null
        };
          console.log('Adding bot message:', botMessage);
        
        targetRoom.messages.push(botMessage);
        // Force array reference change for change detection
        targetRoom.messages = [...targetRoom.messages];
        targetRoom.lastMessage = botMessage.text;
        targetRoom.lastMessageSender = botMessage.sender;
        targetRoom.lastTime = botMessage.time;
        
        console.log('Room messages after adding bot response:', targetRoom.messages.length);
        
        // Trigger change detection to ensure UI updates
        this.cdr.detectChanges();
      });
  }
  handleTurnComplete() {
    if (this.rooms.length > 0) {
      const room = this.rooms[this.activeRoomIndex];
      room.isLoading = false;
      console.log('Turn completed for room:', room.uuid);
    }
  }
    // WebSocket message handling for audio mode
  handleWebSocketMessage(event: {text: string, isComplete: boolean, isTranscript?: boolean}) {
    console.log('WebSocket message received:', event);
    
    if (this.rooms.length === 0) {
      return;
    }
    
    const room = this.rooms[this.activeRoomIndex];
    const roomUuid = room.uuid;

    // Si el mensaje es una transcripción (lo que dice el usuario)
    if (event.isTranscript) {
      this.addTranscriptMessage(room, event.text);
      return;
    }
    
    // Si este es un mensaje nuevo (no está completo y es el primer trozo)
    if (!event.isComplete && !this.currentWsMessageId) {
      this.startNewWsMessage(room, event.text);
    }
    // Si es continuación de un mensaje existente
    else if (!event.isComplete && this.currentWsMessageId) {
      this.appendToWsMessage(room, event.text);
    }
    // Si es el fin de un mensaje
    else if (event.isComplete) {
      // Solo marcar el mensaje como completo
      this.currentWsMessageId = null;
      room.isLoading = false;
      
      // Forzar la detección de cambios
      this.cdr.detectChanges();
    }
  }

  // Método para agregar un mensaje de transcripción (lo que dice el usuario)
  private addTranscriptMessage(room: ChatRoom, text: string) {
    if (!text.trim()) return;
    
    const now = new Date();
    const time = now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    
    // Crear mensaje del usuario con la transcripción
    const userMessage: Message = {
      uuid: uuidv4(),
      sender: 'Tú',
      avatar: 'https://randomuser.me/api/portraits/men/1.jpg',
      time,
      text: `🎤 ${text}`, // Añadir icono de micrófono para distinguirlo
      images: [],
      link: '',
      file: null
    };
    
    console.log('Añadiendo mensaje de transcripción:', userMessage);
    
    // Añadir a los mensajes
    room.messages.push(userMessage);
    room.messages = [...room.messages]; // Forzar cambio de referencia
    room.lastMessage = text;
    room.lastMessageSender = userMessage.sender;
    room.lastTime = time;
    
    // Forzar la detección de cambios
    this.cdr.detectChanges();
  }
  
  private currentWsMessageId: string | null = null;
  
  private startNewWsMessage(room: ChatRoom, text: string) {
    if (!text.trim()) return;
    
    // Set loading state
    room.isLoading = true;
    
    // Create new bot message
    const botMessage: Message = {
      uuid: uuidv4(),
      sender: 'ChefAPP',
      avatar: '/robot-avatar.svg',
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      text: text,
      images: [],
      link: '',
      file: null
    };
    
    // Save the ID to append to this message later
    this.currentWsMessageId = botMessage.uuid;
    
    console.log('Adding new WebSocket bot message:', botMessage);
    
    // Add to messages
    room.messages.push(botMessage);
    room.messages = [...room.messages]; // Force array reference change
    room.lastMessage = text;
    room.lastMessageSender = botMessage.sender;
    room.lastTime = botMessage.time;
    
    // Trigger change detection
    this.cdr.detectChanges();
  }
  
  private appendToWsMessage(room: ChatRoom, text: string) {
    if (!this.currentWsMessageId || !text.trim()) return;
    
    // Find current message
    const messageIndex = room.messages.findIndex(m => m.uuid === this.currentWsMessageId);
    if (messageIndex === -1) {
      console.error('WebSocket message not found to append to:', this.currentWsMessageId);
      return;
    }
    
    // Update the message
    const updatedMessages = [...room.messages];
    updatedMessages[messageIndex] = {
      ...updatedMessages[messageIndex],
      text: updatedMessages[messageIndex].text + text
    };
    
    // Update room
    room.messages = updatedMessages;
    room.lastMessage = updatedMessages[messageIndex].text;
    
    // Trigger change detection
    this.cdr.detectChanges();
  }
    // Manage recording state
  handleRecordingStateChange(isRecording: boolean) {
    console.log('Recording state changed:', isRecording);
    
    if (this.rooms.length === 0) {
      return;
    }
    
    const room = this.rooms[this.activeRoomIndex];
    
    // Al iniciar la grabación, mostrar indicador de carga
    // Al detener la grabación, mantener el indicador de carga
    // ya que aún esperamos procesar la transcripción
    if (isRecording) {
      room.isLoading = true;
      console.log('Room loading state set to true for recording');
    } else {
      // No desactivamos el estado de carga aquí, ya que seguimos esperando la respuesta
      console.log('Recording stopped, waiting for server response...');
    }
    
    this.cdr.detectChanges();
  }
}