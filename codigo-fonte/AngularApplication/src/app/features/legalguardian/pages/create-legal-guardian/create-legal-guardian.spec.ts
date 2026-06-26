import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateLegalGuardian } from './create-legal-guardian';

describe('CreateLegalGuardian', () => {
  let component: CreateLegalGuardian;
  let fixture: ComponentFixture<CreateLegalGuardian>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateLegalGuardian]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateLegalGuardian);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
