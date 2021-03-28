export abstract class Response {
    fileExists: boolean;
    fileFormatOk: boolean;
    errorMessage: string;
    error: boolean;
}


export class BVResponse extends Response {
    nbBassins: number;
}

export class EXUResponse extends Response {
    nbCreeks: number;
    nbZones: number;
}

export class DECResponse extends Response {
    nbZones: number;
    nbBVs: number;
    nbDecanteurs: number;
}
