package org.r1.gde.controller;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadBVFileResponse {
    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;
    private int nbBassins;

}
