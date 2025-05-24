/**
 * Interface for contact items in the chat list
 */
export interface Contact {
  id: number;
  uuid: string;
  name: string;
  avatar: string;
  lastMessage: string;
  time: string;
  status: 'online' | 'offline' | 'away';
} 