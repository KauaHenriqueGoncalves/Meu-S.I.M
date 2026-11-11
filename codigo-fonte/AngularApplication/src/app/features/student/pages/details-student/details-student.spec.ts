import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailsStudent } from './details-student';

describe('DetailsStudent', () => {
  let component: DetailsStudent;
  let fixture: ComponentFixture<DetailsStudent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetailsStudent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DetailsStudent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
