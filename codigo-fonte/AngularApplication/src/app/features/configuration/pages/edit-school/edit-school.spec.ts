import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditSchool } from './edit-school';

describe('EditSchool', () => {
  let component: EditSchool;
  let fixture: ComponentFixture<EditSchool>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditSchool]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditSchool);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
