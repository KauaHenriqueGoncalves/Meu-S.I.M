import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailsSchoolAdmin } from './details-school-admin';

describe('DetailsSchoolAdmin', () => {
  let component: DetailsSchoolAdmin;
  let fixture: ComponentFixture<DetailsSchoolAdmin>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetailsSchoolAdmin]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DetailsSchoolAdmin);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
