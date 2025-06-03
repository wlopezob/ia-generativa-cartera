import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Contact } from '../../shared/models/contact.interface';
import { v4 as uuidv4 } from 'uuid';

@Component({
  selector: 'app-chat-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './chat-list.component.html',
  styleUrl: './chat-list.component.css'
})
export class ChatListComponent {
  @Output() chatDeleted = new EventEmitter<number>();
  
  contacts: Contact[] = [
    {
      id: 1,
      uuid: uuidv4(),
      name: 'Jasmine Thompson',
      avatar: 'https://randomuser.me/api/portraits/women/44.jpg',
      lastMessage: 'Had they visited Rome before',
      time: '45 min',
      status: 'online'
    },
    {
      id: 2,
      uuid: uuidv4(),
      name: 'Mathias Devos',
      avatar: 'https://randomuser.me/api/portraits/men/45.jpg',
      lastMessage: 'Hey, how\'s it going?',
      time: '2 days',
      status: 'offline'
    }
  ];
  
  deleteChat(index: number, event: Event): void {
    event.stopPropagation();
    if (confirm('¿Estás seguro de eliminar este chat?')) {
      this.contacts.splice(index, 1);
      this.chatDeleted.emit(index);
    }
  }
  
  addNewChat(): void {
    // This functionality already exists elsewhere, we're just updating the design
  }
}
