import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminBillingDiscounts } from './admin-billing-discounts';

describe('AdminBillingDiscounts', () => {
  let component: AdminBillingDiscounts;
  let fixture: ComponentFixture<AdminBillingDiscounts>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminBillingDiscounts]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminBillingDiscounts);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
