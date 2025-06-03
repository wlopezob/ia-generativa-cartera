import { Component, ChangeDetectorRef } from '@angular/core';
import { v4 as uuidv4 } from 'uuid';
import { ChatWindowComponent } from '../chat-window/chat-window.component';
import { ChatRoom } from '../../shared/models/chat-room.interface';
import { Message } from '../../shared/models/message.interface';
import { LegalacoChatService } from '../../shared/services/legalaco-chat.service';
import { LegalacoChatRequest } from '../../shared/models/legalaco-chat-request.interface';
import { ConfirmModalComponent } from '../../shared/components/confirm-modal/confirm-modal.component';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-chat-rooms',
  standalone: true,
  imports: [ChatWindowComponent, ConfirmModalComponent],
  templateUrl: './chat-rooms.component.html',
  styleUrls: ['./chat-rooms.component.css']
})
export class ChatRoomsComponent {
  rooms: ChatRoom[] = [];
  activeRoomIndex = 0;
  showDeleteConfirmModal = false;
  roomToDeleteIndex: number | null = null;

  constructor(
    private cdr: ChangeDetectorRef,
    private legalacoChat: LegalacoChatService
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
  }
  
  handleSendMessage(text: string) {
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
    
    // Create user message
    const message: Message = {
      uuid: uuidv4(),
      sender: 'Tú',
      avatar: 'https://randomuser.me/api/portraits/men/1.jpg',
      time,
      text,
      images: [],
      link: '',
      file: null
    };
    
    room.messages.push(message);
    room.lastMessage = text;
    room.lastMessageSender = message.sender;
    room.lastTime = time;
    
    // Create request for API
    const request: LegalacoChatRequest = {
      question: text,
      sessionId: roomUuid
    };
    
    // Send to API using the service
    this.legalacoChat.sendMessage(request)
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
              avatar: 'assets/error-icon.png',
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
        // Find the room by UUID in case user has switched rooms
        const targetRoom = this.rooms.find(r => r.uuid === roomUuid);
        if (!targetRoom || !response) {
          return;
        }
        
        // End loading state
        targetRoom.isLoading = false;
        
        // Add response messages
        response.answers.forEach(answer => {
          const botMessage: Message = {
            uuid: uuidv4(),
            sender: 'legalaco',
            avatar: 'assets/robot-avatar.png',
            time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
            text: answer,
            images: [],
            link: '',
            file: null
          };
          
          targetRoom.messages.push(botMessage);
          targetRoom.lastMessage = botMessage.text;
          targetRoom.lastMessageSender = botMessage.sender;
          targetRoom.lastTime = botMessage.time;
        });
        
        // Force change detection
        this.cdr.detectChanges();
      });
  }
} 