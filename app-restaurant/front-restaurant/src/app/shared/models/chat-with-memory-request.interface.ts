export interface ChatWithMemoryRequest {
  message: string;
  sessionId: string;
  userId?: string;
  sessionName?: string;
} 