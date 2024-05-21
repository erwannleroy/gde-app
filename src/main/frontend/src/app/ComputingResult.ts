export class ComputingResult {

	error: boolean = false;
	errorMsg: string = "";

	bytesXlsInProgress: boolean = false;
	bytesDbfInProgress: boolean = false;
	xlsComputationOk: boolean = false;
	perfDbfComputationOk: boolean = false;
	debitDbfComputationOk: boolean = false;

	objRetComputeProgress: number = 0;
	retComputeProgress: number = 0;
	cassisComputeProgress: number = 0;
	q100ComputeProgress: number = 0;

	objRetComputeOk: boolean = false;
	retComputeOk: boolean = false;
	cassisComputeOk: boolean = false;
	q100ComputeOk: boolean = false;

	xlsComputeProgress: number = 0;
	perfDbfComputeProgress: number = 0;
	debitDbfComputeProgress: number = 0;

	inDbfBVDecWarns: string[];
	inDbfDecWarns: string[];
	inDbfBVExuWarns: string[];
	inDbfExuWarns: string[];
	paramWarns: string[];
	objRetWarns: string[];
	retBassinsWarns: string[];
	q100Warns: string[];
	cassisWarns: string[];
	bvDecDBFWarns: string[];
	exuDBFWarns: string[];
	
}
