export class ComputingResult {

	error: boolean = false;
	errorMsg: string = "";

	bytesXlsInProgress: boolean = false;
	bytesDbfInProgress: boolean = false;
	xlsComputationOk: boolean = false;
	dbfComputationOk: boolean = false;

	objRetComputeProgress: number = 0;
	retComputeProgress: number = 0;
	cassisComputeProgress: number = 0;
	q100ComputeProgress: number = 0;

	objRetComputeOk: boolean = false;
	retComputeOk: boolean = false;
	cassisComputeOk: boolean = false;
	q100ComputeOk: boolean = false;

	xlsComputeProgress: number = 0;
	dbfComputeProgress: number = 0;

}
