import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MySubscriptions } from './my-subscriptions';

describe('MySubscriptions', () => {
  let component: MySubscriptions;
  let fixture: ComponentFixture<MySubscriptions>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MySubscriptions]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MySubscriptions);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
