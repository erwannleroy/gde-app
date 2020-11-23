import { TestBed } from '@angular/core/testing';

import { GdeService } from './gde.service';

describe('GdeServiceService', () => {
  let service: GdeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GdeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
