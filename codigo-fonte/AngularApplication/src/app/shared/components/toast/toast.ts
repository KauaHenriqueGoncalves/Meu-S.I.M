import { Component } from '@angular/core';
import { ChangeDetectorRef } from '@angular/core';
import { NotificationMessage } from '../../../core/models/notification.model';
import { NotificationService } from '../../../core/services/notification/notification.service';

@Component({
  selector: 'app-toast',
  imports: [],
  templateUrl: './toast.html',
  styleUrl: './toast.sass',
})
export class Toast {
  toasts: NotificationMessage[] = [];

  constructor(
    private notification: NotificationService,
    private changeDetectorRef: ChangeDetectorRef
  ) {
    this.notification.onNotify((msg) => {
      this.toasts.push(msg);

      setTimeout(() => {
        this.toasts.shift();
        this.changeDetectorRef.detectChanges();
      }, 2500);
    });
  }
}
