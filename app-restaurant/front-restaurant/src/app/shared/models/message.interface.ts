/**
 * Interface for chat messages
 */
export interface Message {
  uuid: string;
  sender: string;
  avatar: string;
  time: string;
  text: string;
  images: string[];
  link: string;
  file: any | null;
} 