import { Component, Output, EventEmitter, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-message-input',
  imports: [FormsModule],
  templateUrl: './message-input.component.html',
  styleUrl: './message-input.component.css',
  standalone: true
})
export class MessageInputComponent {
  messageText = '';
  @Input() isLoading = false;
  @Output() send = new EventEmitter<string>();

  onSend() {
    if (this.messageText.trim() && !this.isLoading) {
      this.send.emit(this.messageText);
      this.messageText = '';
    }
  }
}
