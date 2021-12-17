import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OutputXlsComponent } from './output-xls.component';

describe('OutputXlsComponent', () => {
  let component: OutputXlsComponent;
  let fixture: ComponentFixture<OutputXlsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OutputXlsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OutputXlsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
