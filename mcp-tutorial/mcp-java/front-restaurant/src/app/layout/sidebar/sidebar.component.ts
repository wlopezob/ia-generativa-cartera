import { Component, Input, Output, EventEmitter } from '@angular/core';
import { ChatRoom } from '../../shared/models/chat-room.interface';
import { LastMessagePipe } from '../../shared/pipes/last-message.pipe';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css',
  standalone: true,
  imports: [LastMessagePipe]
})
export class SidebarComponent {
  @Input() rooms: ChatRoom[] = [];
  @Input() activeRoomIndex = 0;
  @Output() addRoom = new EventEmitter<void>();
  @Output() removeRoom = new EventEmitter<number>();
  @Output() setActiveRoom = new EventEmitter<number>();

  /**
   * Extrae las iniciales de un nombre de sala para mostrar en el avatar
   * @param name Nombre de la sala
   * @returns Iniciales para mostrar en el avatar (máximo 2 caracteres)
   */
  getInitials(name: string): string {
    if (!name) return '?';
    
    // Extraer iniciales para mejores resultados visuales
    const words = name.split(' ');
    if (words.length >= 2) {
      // Si hay al menos dos palabras, usar las iniciales de ambas
      return (words[0].charAt(0) + words[1].charAt(0)).toUpperCase();
    } else if (name.length > 1) {
      // Si es una sola palabra larga, usar los dos primeros caracteres
      return name.substring(0, 2).toUpperCase();
    } else {
      // Si es una sola letra, usar esa letra
      return name.charAt(0).toUpperCase();
    }
  }

  /**
   * Formatea el nombre de la sala para mostrar en la lista
   * @param name Nombre completo de la sala
   * @returns Nombre formateado (máximo 25 caracteres)
   */
  formatRoomName(name: string): string {
    if (!name) return 'Sala sin nombre';
    
    // Si el nombre es muy largo, truncarlo para mejor visualización
    const maxLength = 25;
    if (name.length > maxLength) {
      return name.substring(0, maxLength) + '...';
    }
    return name;
  }
}
