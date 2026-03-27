export interface NotificationMessage {
  type: 'success' | 'error' | 'warning' | 'info';
  text: string;
}