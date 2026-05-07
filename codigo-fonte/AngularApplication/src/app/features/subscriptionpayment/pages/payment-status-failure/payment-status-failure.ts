import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

//?collection_id=null&collection_status=null&payment_id=null&status=null&external_reference=67f5a99d-17ac-4f4d-b359-c86487b58530&payment_type=null&merchant_order_id=null&preference_id=3253136173-565dd684-10a1-4852-b4ee-dc647055bdbd&site_id=MLB&processing_mode=aggregator&merchant_account_id=null

@Component({
  selector: 'app-payment-status-failure',
  imports: [],
  templateUrl: './payment-status-failure.html',
  styleUrl: './../payment-status.sass',
})
export class PaymentStatusFailure {
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

  viewDetail(): void {
    const id: string | null = this.externalReference;
    this.router.navigate(['/app/subscription-detail'], {
      state: { id }
    });
  }

  goToMySubscription(): void {
    this.router.navigate(['/app/my-subscriptions']);
  }
}
