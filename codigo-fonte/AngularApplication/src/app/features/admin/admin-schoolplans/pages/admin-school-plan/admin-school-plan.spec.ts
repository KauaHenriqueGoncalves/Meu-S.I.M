import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminSchoolPlan } from './admin-school-plan';

describe('AdminSchoolPlan', () => {
  let component: AdminSchoolPlan;
  let fixture: ComponentFixture<AdminSchoolPlan>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminSchoolPlan]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminSchoolPlan);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
