import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateSchoolAdmin } from './create-school-admin';

describe('CreateSchoolAdmin', () => {
  let component: CreateSchoolAdmin;
  let fixture: ComponentFixture<CreateSchoolAdmin>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateSchoolAdmin]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateSchoolAdmin);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
