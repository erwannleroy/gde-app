export abstract class Response {
    fileExists: boolean;
    fileFormatOk: boolean;
    errorMessage: string;
    error: boolean;
}

export class MeteoResponse extends Response {
    result: boolean;
}

export class BVDECResponse extends Response {
    nbBassins: number;
}

export class BVEXUResponse extends Response {
    nbCreeks: number;
    nbZones: number;
}

export class EXUResponse extends Response {
}

export class DECResponse extends Response {
    nbZones: number;
    nbBVs: number;
    nbDecanteurs: number;
}
