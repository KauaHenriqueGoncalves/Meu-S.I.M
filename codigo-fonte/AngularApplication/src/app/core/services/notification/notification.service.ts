import { Injectable } from '@angular/core';
import { NotificationMessage } from '../../models/notification.model';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private listeners: ((message: NotificationMessage) => void)[] = [];

  notify(message: NotificationMessage) {
    this.listeners.forEach(listener => listener(message));
  }

  onNotify(listener: (message: NotificationMessage) => void) {
    this.listeners.push(listener);
  }
}
