import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Message } from '../../shared/models/message.interface';
import { MarkdownModule } from 'ngx-markdown';

@Component({
  selector: 'app-message-card',
  imports: [CommonModule, MarkdownModule],
  templateUrl: './message-card.component.html',
  styleUrl: './message-card.component.css'
})
export class MessageCardComponent {
  @Input() message!: Message;
}
