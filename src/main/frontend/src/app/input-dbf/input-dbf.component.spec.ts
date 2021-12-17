import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InputDbfComponent } from './input-dbf.component';

describe('InputDbfComponent', () => {
  let component: InputDbfComponent;
  let fixture: ComponentFixture<InputDbfComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InputDbfComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InputDbfComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
