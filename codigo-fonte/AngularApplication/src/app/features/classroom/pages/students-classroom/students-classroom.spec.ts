import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StudentsClassroom } from './students-classroom';

describe('StudentsClassroom', () => {
  let component: StudentsClassroom;
  let fixture: ComponentFixture<StudentsClassroom>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StudentsClassroom]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StudentsClassroom);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
