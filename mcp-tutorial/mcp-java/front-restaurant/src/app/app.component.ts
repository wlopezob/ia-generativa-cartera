import { Component, OnInit, HostListener, OnDestroy, PLATFORM_ID, Inject, ApplicationRef } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { TopbarComponent } from './layout/topbar/topbar.component';
import { ChatWindowComponent } from './chat/chat-window/chat-window.component';
import { ConfirmModalComponent } from './shared/components/confirm-modal/confirm-modal.component';
import { SidebarOrchestratorComponent } from './shared/components/sidebar-orchestrator/sidebar-orchestrator.component';
import { LoaderComponent } from './shared/components/loader/loader.component';
import { NgIf, NgStyle, CommonModule, isPlatformBrowser } from '@angular/common';
import { v4 as uuidv4 } from 'uuid';
import { ChatRoom } from './shared/models/chat-room.interface';
import { Message } from './shared/models/message.interface';
import { RestaurantChatService } from './shared/services/restaurant-chat.service';
import { UIStateService } from './shared/services/ui-state.service';
import { AuthService } from './shared/services/auth.service';
import { ChatWithMemoryRequest } from './shared/models/chat-with-memory-request.interface';
import { catchError, delay, take } from 'rxjs/operators';
import { of, Subscription, timer } from 'rxjs';
import { trigger, transition, style, animate } from '@angular/animations';

@Component({
  selector: 'app-root',
  imports: [
    RouterOutlet, 
    TopbarComponent, 
    ChatWindowComponent,
    ConfirmModalComponent,
    SidebarOrchestratorComponent,
    LoaderComponent,
    NgIf,
    NgStyle,
    CommonModule
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
  standalone: true,
  animations: [
    trigger('fadeAnimation', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('300ms', style({ opacity: 1 })),
      ]),
      transition(':leave', [
        animate('300ms', style({ opacity: 0 })),
      ]),
    ]),
  ],
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'Chef APP';
  rooms: ChatRoom[] = [];
  activeRoomIndex = 0;
  showDeleteConfirmModal = false;
  roomToDeleteIndex: number | null = null;
  sidebarVisible = true;
  isMobile = false;
  isAuthenticated = false;
  isLoading = true;
  
  private subscription = new Subscription();
  private isBrowser: boolean;
  
  constructor(
    private restaurantChat: RestaurantChatService,
    private uiState: UIStateService,
    private authService: AuthService,
    private appRef: ApplicationRef,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
    
    // Inicializar el estado de autenticación de manera sincrónica
    this.isAuthenticated = this.authService.isAuthenticated();
    
    if (this.isBrowser) {
      this.checkScreenSize();
    } else {
      // Valores por defecto para SSR
      this.isMobile = false;
      this.sidebarVisible = true;
    }
  }
  
  @HostListener('window:resize')
  onResize(): void {
    if (this.isBrowser) {
      this.checkScreenSize();
    }
  }
  
  checkScreenSize(): void {
    if (!this.isBrowser) return;
    
    const wasMobile = this.isMobile;
    this.isMobile = window.innerWidth < 768;
    
    // Si cambia de escritorio a móvil, ocultar sidebar
    if (!wasMobile && this.isMobile) {
      this.uiState.setSidebarVisible(false);
    }
    // Si cambia de móvil a escritorio, mostrar sidebar (pero respetando la selección del usuario)
    else if (wasMobile && !this.isMobile && !this.sidebarVisible) {
      this.uiState.setSidebarVisible(true);
    }
  }
  
  toggleSidebar(): void {
    this.uiState.toggleSidebar();
  }
  
  private removeInitialLoader(): void {
    if (!this.isBrowser) return;
    
    // Remover el pre-loader del index.html
    const loader = document.querySelector('.app-loading');
    if (loader) {
      loader.classList.add('loaded');
      // Dar un tiempo para que termine la animación de salida
      setTimeout(() => {
        if (loader.parentNode) {
          loader.parentNode.removeChild(loader);
        }
      }, 500);
    }
  }
  
  ngOnInit(): void {
    // Mostrar loading inicialmente
    this.isLoading = true;
    
    if (this.isBrowser) {
      // Agregar clase para evitar parpadeo
      document.body.classList.add('app-initializing');
    }
    
    // Usar timer para asegurar el tiempo mínimo de carga y evitar parpadeos
    const minLoadingTime$ = timer(800);
    
    // Subscribe to authentication state
    this.subscription.add(
      this.authService.isAuthenticated$
        .pipe(
          // Asegurar que al menos pase el tiempo mínimo de carga
          delay(300)
        )
        .subscribe(isAuth => {
          this.isAuthenticated = isAuth;
          
          // Desactivar el loading después de verificar la autenticación y tiempo mínimo
          minLoadingTime$.pipe(take(1)).subscribe(() => {
            // Quitar el pre-loader del index.html
            this.removeInitialLoader();
            
            // Esperar a que termine la transición
            setTimeout(() => {
              this.isLoading = false;
              
              if (this.isBrowser) {
                // Quitar clase para permitir transiciones
                document.body.classList.remove('app-initializing');
              }
              
              // Forzar detección de cambios
              this.appRef.tick();
            }, 200);
          });
        })
    );
    
    // Suscripción al estado del sidebar
    this.subscription.add(
      this.uiState.sidebarVisible$.subscribe(visible => {
        this.sidebarVisible = visible;
        
        // Añadir/quitar clase a body para prevenir scroll cuando el sidebar está abierto en móvil
        if (this.isBrowser && this.isMobile) {
          if (visible) {
            document.body.classList.add('sidebar-open');
          } else {
            document.body.classList.remove('sidebar-open');
          }
        }
      })
    );
  }
  
  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  addInitialRooms() {
    // Añadir salas iniciales sin necesidad de mostrar el modal
    for (let i = 0; i < 3; i++) {
      const num = this.rooms.length + 1;
      const roomId = uuidv4();
      
      this.rooms.push({
        uuid: roomId,
        name: `Sala ${num}`,
        messages: [],
        lastMessage: 'Sin mensajes',
        lastMessageSender: '',
        lastTime: '-',
        isLoading: false
      });
    }
    this.activeRoomIndex = 0;
  }

  showAddRoomDialog() {
    // Crear sala directamente sin mostrar modal
    this.addRoom('Chef APP');
  }

  addRoom(lawType: string) {
    if (!lawType || lawType.trim() === '') {
      lawType = 'Nueva Sala';
    }
    
    const roomId = uuidv4();
    console.log('Creating new room with UUID:', roomId);
    
    // Crear nueva sala con nombre truncado si es necesario
    const newRoom: ChatRoom = {
      uuid: roomId,
      name: lawType,
      messages: [],
      lastMessage: 'Sin mensajes',
      lastMessageSender: '',
      lastTime: '-',
      isLoading: false
    };
    
    // Create a new array to ensure change detection triggers
    this.rooms = [...this.rooms, newRoom];
    
    // Asegurarse de que el índice sea válido
    this.activeRoomIndex = this.rooms.length - 1;
    
    // En móvil, mostrar el chat automáticamente después de crear una sala
    if (this.isMobile) {
      this.uiState.setSidebarVisible(false);
    }
  }

  confirmDeleteRoom(index: number) {
    if (index < 0 || index >= this.rooms.length) {
      console.error(`Invalid room index: ${index}`);
      return;
    }
    
    this.roomToDeleteIndex = index;
    this.showDeleteConfirmModal = true;
  }

  cancelDeleteRoom() {
    this.showDeleteConfirmModal = false;
    this.roomToDeleteIndex = null;
  }

  deleteRoomConfirmed() {
    if (this.roomToDeleteIndex !== null && 
        this.roomToDeleteIndex >= 0 && 
        this.roomToDeleteIndex < this.rooms.length) {
      
      // Guardar el índice actual antes de eliminar
      const wasActive = this.activeRoomIndex === this.roomToDeleteIndex;
      const oldActiveIndex = this.activeRoomIndex;
      
      // Create a new array without the room being deleted (immutable operation)
      this.rooms = [
        ...this.rooms.slice(0, this.roomToDeleteIndex),
        ...this.rooms.slice(this.roomToDeleteIndex + 1)
      ];
      
      // Ajustar el índice activo
      if (this.rooms.length === 0) {
        // No hay más salas
        this.activeRoomIndex = 0;
      } else if (wasActive) {
        // Si la sala eliminada era la activa, activar la siguiente o la última
        this.activeRoomIndex = Math.min(this.roomToDeleteIndex, this.rooms.length - 1);
      } else if (oldActiveIndex > this.roomToDeleteIndex) {
        // Si la sala activa estaba después de la eliminada, ajustar el índice
        this.activeRoomIndex = oldActiveIndex - 1;
      }
      // En otro caso, mantener el mismo índice activo
      
      this.showDeleteConfirmModal = false;
      this.roomToDeleteIndex = null;
    }
  }

  removeRoom(index: number) {
    this.confirmDeleteRoom(index);
  }

  setActiveRoom(index: number) {
    if (index >= 0 && index < this.rooms.length) {
      this.activeRoomIndex = index;
      
      // En móviles, cerrar automáticamente el sidebar al seleccionar una sala
      if (this.isMobile) {
        this.uiState.setSidebarVisible(false);
      }
    } else {
      console.error(`Invalid room index: ${index}`);
    }
  }

  handleMessage(text: string) {
    if (this.rooms.length === 0 || this.activeRoomIndex >= this.rooms.length) {
      console.error('No active room available for sending message');
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
    const request: ChatWithMemoryRequest = {
      message: text,
      sessionId: roomUuid
    };
    
    // Send to API using the service
    this.restaurantChat.sendMessageWithMemory(request)
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
              avatar: 'robot-avatar.svg',
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
        
        // Add response message (new API returns single response string)
        const botMessage: Message = {
          uuid: uuidv4(),
          sender: 'Chef AI',
          avatar: 'robot-avatar.svg',
          time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
          text: response.response,
          images: [],
          link: '',
          file: null
        };
        
        targetRoom.messages.push(botMessage);
        targetRoom.lastMessage = botMessage.text;
        targetRoom.lastMessageSender = botMessage.sender;
        targetRoom.lastTime = botMessage.time;
      });
  }
}
