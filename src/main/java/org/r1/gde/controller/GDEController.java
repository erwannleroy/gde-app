package org.r1.gde.controller;

import java.time.LocalDateTime;

import org.r1.gde.service.ComputingResult;
import org.r1.gde.service.GDEComputer;
import org.r1.gde.service.GDEService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
//@CrossOrigin("http://localhost:8123")
@RequestMapping("gde")
@EnableScheduling
public class GDEController {

	@Autowired
	private GDEService gdeService;

	@Autowired
	private GDEComputer gdeComputer;

	private LocalDateTime lastPing;

	@PostMapping("/upload-bv-decanteurs-file")
	public ResponseEntity<BVDecanteurResponse> uploadBVDecanteurFile(@RequestParam("bv") MultipartFile file) {
		BVDecanteurResponse bvResponse = this.gdeService.giveBVDecanteurFile(file);

		return ResponseEntity.status(HttpStatus.OK).body(bvResponse);
	}

	@PostMapping("/upload-decanteurs-file")
	public ResponseEntity<DecanteurResponse> uploadDecanteurFile(@RequestParam("dec") MultipartFile file) {
		DecanteurResponse decResponse = this.gdeService.giveDecanteurFile(file);

		return ResponseEntity.status(HttpStatus.OK).body(decResponse);
	}

	@PostMapping("/upload-bv-exutoires-file")
	public ResponseEntity<BVExutoireResponse> uploadBVExutoireFile(@RequestParam("exu") MultipartFile file) {
		log.info("upload-bv-exutoires-file");
		BVExutoireResponse exuResponse = this.gdeService.giveBVExutoireFile(file);

		return ResponseEntity.status(HttpStatus.OK).body(exuResponse);
	}

	@PostMapping("/upload-bv-file-by-path")
	public BVDecanteurResponse uploadFileByPath(@RequestParam("bv") String bvFilePath) {

		BVDecanteurResponse result = this.gdeService.giveBVFilePath(bvFilePath);

		return result;
	}

	
	
	@PostMapping("/ping")
	public ResponseEntity ping() {
		this.lastPing = LocalDateTime.now();
		return new ResponseEntity(HttpStatus.OK);
	}

	@PostMapping("/reset")
	public ResponseEntity reset() {
		log.debug("appel de reset");
		this.gdeComputer.reset();
		return new ResponseEntity(HttpStatus.OK);
	}
	
	@Scheduled(fixedDelay = 2000)
	public void testAlive() {
		if (this.lastPing != null) {
			if (LocalDateTime.now().minusSeconds(5).isAfter(this.lastPing)) {
				log.info("Le navigateur a été fermé : on coupe !");
				System.exit(0);
			} else {
				log.debug("Le navigateur est toujours là, on continue");
			}
		}
	}

	@GetMapping("/get-result")
	public ResponseEntity<ComputingResult> getComputeResult() {
		return ResponseEntity.status(HttpStatus.OK).body(gdeComputer.getComputeContext().getComputingResult());
	}

	@GetMapping("/get-result-bytes")
	public ResponseEntity<byte[]> getComputeResultBytes() {
		HttpHeaders header = new HttpHeaders();
		header.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=gde.xlsx");
		byte[] bytes = gdeComputer.getComputeContext().getComputingResult().getXls();
		header.setContentLength(bytes.length);
		return ResponseEntity.status(HttpStatus.OK).headers(header).body(bytes);
	}

}
