import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AboutClassroom } from './about-classroom';

describe('AboutClassroom', () => {
  let component: AboutClassroom;
  let fixture: ComponentFixture<AboutClassroom>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AboutClassroom]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AboutClassroom);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
