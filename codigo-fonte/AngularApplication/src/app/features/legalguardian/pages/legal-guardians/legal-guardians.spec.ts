import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LegalGuardians } from './legal-guardians';

describe('LegalGuardians', () => {
  let component: LegalGuardians;
  let fixture: ComponentFixture<LegalGuardians>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LegalGuardians]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LegalGuardians);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
