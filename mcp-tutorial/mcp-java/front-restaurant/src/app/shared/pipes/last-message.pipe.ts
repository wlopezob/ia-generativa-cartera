import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'lastMessage',
  standalone: true
})
export class LastMessagePipe implements PipeTransform {
  transform(value: string, sender?: string, maxLength: number = 25): string {
    if (!value) {
      return 'Sin mensajes';
    }

    // Remove markdown characters
    let cleanText = value
      .replace(/\*\*/g, '') // bold
      .replace(/\*/g, '')   // italic
      .replace(/```[^`]*```/g, '[Código]') // code blocks
      .replace(/`([^`]+)`/g, '$1')  // inline code
      .replace(/\[([^\]]+)\]\([^)]+\)/g, '$1') // links
      .replace(/#/g, '')   // headings
      .replace(/>/g, '')   // blockquotes
      .replace(/\n/g, ' '); // new lines

    // If the sender is provided and it's not the user, prefix with sender name
    let result = '';
    if (sender && sender !== 'Tú') {
      result = `${sender}: `;
      // Reduce maxLength to account for sender prefix
      maxLength = Math.max(maxLength - sender.length - 2, 10);
    }

    if (cleanText.length > maxLength) {
      result += cleanText.substring(0, maxLength) + '...';
    } else {
      result += cleanText;
    }

    return result;
  }
} 