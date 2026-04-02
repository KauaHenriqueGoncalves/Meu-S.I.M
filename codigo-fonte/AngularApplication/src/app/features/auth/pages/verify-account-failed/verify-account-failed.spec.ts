import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VerifyAccountFailed } from './verify-account-failed';

describe('VerifyAccountFailed', () => {
  let component: VerifyAccountFailed;
  let fixture: ComponentFixture<VerifyAccountFailed>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VerifyAccountFailed]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VerifyAccountFailed);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
