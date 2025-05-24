import { Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MessageCardComponent } from '../message-card/message-card.component';
import { MessageInputComponent } from '../message-input/message-input.component';
import { FormsModule } from '@angular/forms';
import { Message } from '../../shared/models/message.interface';

@Component({
  selector: 'app-chat-window',
  imports: [CommonModule, MessageCardComponent, MessageInputComponent, FormsModule],
  templateUrl: './chat-window.component.html',
  styleUrl: './chat-window.component.css'
})
export class ChatWindowComponent implements OnInit, OnChanges {
  @Input() messages: Message[] = [];
  @Input() sessionId = '';
  @Input() isLoading = false;
  @Output() send = new EventEmitter<string>();

  constructor() {}

  ngOnInit(): void {
    // Si no se ha proporcionado un sessionId, generamos uno nuevo
    if (!this.sessionId) {
      console.log('Warning: No sessionId provided');
    } else {
      console.log('Initial sessionId:', this.sessionId);
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    // Detectar cambios en la propiedad sessionId (cambio de sala)
    if (changes['sessionId'] && !changes['sessionId'].firstChange) {
      const currentValue = changes['sessionId'].currentValue;
      const previousValue = changes['sessionId'].previousValue;
      
      if (currentValue !== previousValue) {
        console.log(`SessionId changed from ${previousValue} to ${currentValue}`);
      }
    }
  }

  addMessage(text: string) {
    // Just emit the message to be handled by parent component
    this.send.emit(text);
  }
}
