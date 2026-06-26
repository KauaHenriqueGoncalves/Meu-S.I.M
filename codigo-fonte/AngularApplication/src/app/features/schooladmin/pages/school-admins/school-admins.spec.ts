import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SchoolAdmins } from './school-admins';

describe('SchoolAdmins', () => {
  let component: SchoolAdmins;
  let fixture: ComponentFixture<SchoolAdmins>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SchoolAdmins]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SchoolAdmins);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
