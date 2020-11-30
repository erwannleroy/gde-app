package org.r1.gde;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@CrossOrigin("http://localhost:4200")
public class GDEController {

	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	private GDEService gdeService;

	@Autowired
	private GDEComputer gdeComputer;

	@PostMapping("/upload-bv-file")
	public ResponseEntity<BVResponse> uploadBVFile(@RequestParam("bv") MultipartFile file) {
		BVResponse bvResponse = this.gdeService.giveBVFile(file);
		
		return ResponseEntity.status(HttpStatus.OK).body(bvResponse);
	}
	
	@PostMapping("/upload-dec-file")
	public ResponseEntity<DECResponse> uploadDECFile(@RequestParam("dec") MultipartFile file) {
		DECResponse decResponse = this.gdeService.giveDECFile(file);
		
		return ResponseEntity.status(HttpStatus.OK).body(decResponse);
	}
	
	@PostMapping("/upload-bv-file-by-path")
	public BVResponse uploadFileByPath(@RequestParam("bv") String bvFilePath) {

		BVResponse result = this.gdeService.giveBVFilePath(bvFilePath);

		return result;
	}

	@GetMapping("/get-result")
	public ResponseEntity<ComputingResult> getComputeResult() {
		return ResponseEntity.status(HttpStatus.OK).body(gdeComputer.getComputingResult());
	}
	
	@GetMapping("/get-result-bytes")
	public ResponseEntity<byte[]> getComputeResultBytes() {
		HttpHeaders header = new HttpHeaders();
	    header.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
	    header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=gde.xlsx");
	    byte[] bytes = gdeComputer.getComputingResult().getXls();
	    header.setContentLength(bytes.length);
		return ResponseEntity.status(HttpStatus.OK).headers(header).body(bytes);
	}
	
	

}
