import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-payment-status-success',
  imports: [],
  templateUrl: './payment-status-success.html',
  styleUrl: './../payment-status.sass',
})
export class PaymentStatusSuccess implements OnInit {
  paymentId: string | null = null;
  externalReference: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.paymentId = params['payment_id'];
      this.externalReference = params['external_reference'];
    });
  }

  goToMySubscription(): void {
    this.router.navigate(['/app/my-subscriptions']);
  }
}
