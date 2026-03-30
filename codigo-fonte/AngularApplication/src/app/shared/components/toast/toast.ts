import { Component, OnInit } from '@angular/core';
import { ChangeDetectorRef } from '@angular/core';
import { NotificationMessage } from '../../../core/models/notification.model';
import { NotificationService } from '../../../core/services/notification/notification.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast.html',
  styleUrl: './toast.sass',
})
export class Toast implements OnInit {
  toasts: NotificationMessage[] = [];

  constructor(
    private notification: NotificationService,
    private changeDetectorRef: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.notification.onNotify((msg) => {
      this.toasts.push(msg);

      this.changeDetectorRef.detectChanges();

      setTimeout(() => {
        this.toasts.shift();
        this.changeDetectorRef.detectChanges();
      }, 2500);
    });
  }
}
