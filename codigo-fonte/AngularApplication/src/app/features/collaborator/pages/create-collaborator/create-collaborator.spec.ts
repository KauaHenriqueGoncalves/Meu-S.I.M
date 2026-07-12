import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateCollaborator } from './create-collaborator';

describe('CreateCollaborator', () => {
  let component: CreateCollaborator;
  let fixture: ComponentFixture<CreateCollaborator>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateCollaborator]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateCollaborator);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
