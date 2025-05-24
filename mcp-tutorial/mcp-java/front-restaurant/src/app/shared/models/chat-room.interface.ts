import { Message } from './message.interface';

/**
 * Interface for chat room items
 */
export interface ChatRoom {
  uuid: string;
  name: string;
  messages: Message[];
  lastMessage: string;
  lastMessageSender?: string;
  lastTime: string;
  avatar?: string;
  isLoading?: boolean;
} 