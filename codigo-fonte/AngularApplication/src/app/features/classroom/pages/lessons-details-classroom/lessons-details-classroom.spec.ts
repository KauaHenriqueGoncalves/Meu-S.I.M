import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LessonsDetailsClassroom } from './lessons-details-classroom';

describe('LessonsDetailsClassroom', () => {
  let component: LessonsDetailsClassroom;
  let fixture: ComponentFixture<LessonsDetailsClassroom>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LessonsDetailsClassroom]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LessonsDetailsClassroom);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
