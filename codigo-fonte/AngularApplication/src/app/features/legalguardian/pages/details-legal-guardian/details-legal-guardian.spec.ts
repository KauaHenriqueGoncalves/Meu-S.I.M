import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailsLegalGuardian } from './details-legal-guardian';

describe('DetailsLegalGuardian', () => {
  let component: DetailsLegalGuardian;
  let fixture: ComponentFixture<DetailsLegalGuardian>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetailsLegalGuardian]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DetailsLegalGuardian);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
