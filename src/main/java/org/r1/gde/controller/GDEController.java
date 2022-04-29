package org.r1.gde.controller;

import lombok.extern.slf4j.Slf4j;
import org.r1.gde.model.DonneesMeteo;
import org.r1.gde.service.ComputingResult;
import org.r1.gde.service.GDEComputer;
import org.r1.gde.service.GDEService;
import org.r1.gde.service.MeteoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

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
	public ResponseEntity<BVDecanteurResponse> uploadBVDecanteurFile(@RequestParam("bvdec") MultipartFile file) {
		BVDecanteurResponse bvResponse = this.gdeService.giveBVDecanteurFile(file);

		return ResponseEntity.status(HttpStatus.OK).body(bvResponse);
	}

	@PostMapping("/apply-meteo")
	public ResponseEntity<MeteoResponse> applyMeteo(@RequestBody DonneesMeteo dataMeteo) {
		MeteoResponse meteoResponse = this.gdeService.applyMeteo(dataMeteo);

		return ResponseEntity.status(HttpStatus.OK).body(meteoResponse);
	}

	@PostMapping("/upload-decanteurs-file")
	public ResponseEntity<DecanteurResponse> uploadDecanteurFile(@RequestParam("dec") MultipartFile file) {
		DecanteurResponse decResponse = this.gdeService.giveDecanteurFile(file);

		return ResponseEntity.status(HttpStatus.OK).body(decResponse);
	}

	@PostMapping("/upload-bv-exutoires-file")
	public ResponseEntity<BVExutoireResponse> uploadBVExutoireFile(@RequestParam("bvexu") MultipartFile file) {
		log.info("upload-bv-exutoires-file");
		BVExutoireResponse bvExuResponse = this.gdeService.giveBVExutoireFile(file);

		return ResponseEntity.status(HttpStatus.OK).body(bvExuResponse);
	}

	@PostMapping("/upload-exutoires-file")
	public ResponseEntity<ExutoireResponse> uploadExutoireFile(@RequestParam("exu") MultipartFile file) {
		log.info("upload-exutoires-file");
		ExutoireResponse exuResponse = this.gdeService.giveExutoireFile(file);

		return ResponseEntity.status(HttpStatus.OK).body(exuResponse);
	}
//	@PostMapping("/upload-bv-file-by-path")
//	public BVDecanteurResponse uploadFileByPath(@RequestParam("bv") String bvFilePath) {
//
//		BVDecanteurResponse result = this.gdeService.giveBVFilePath(bvFilePath);
//
//		return result;
//	}

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

	@GetMapping("/get-meteo")
	public ResponseEntity<DonneesMeteo> getMeteo() {
		return ResponseEntity.status(HttpStatus.OK).body(gdeService.getDonneesMeteo());
	}

	@GetMapping("/get-result-bytes-xls")
	public ResponseEntity<byte[]> getComputeResultBytes() {
		HttpHeaders header = new HttpHeaders();
		header.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=gde.xlsx");
		try {
			byte[] bytes = gdeComputer.getComputeContext().getBytesResult().getBytesXls();
			header.setContentLength(bytes.length);
			return ResponseEntity.status(HttpStatus.OK).headers(header).body(bytes);
		} catch (Exception e) {
			return ResponseEntity.noContent().build();
		}
	}

	@GetMapping("/get-result-bytes-perf-dbf")
	public ResponseEntity<byte[]> getComputeResultBytesPerfDBF() {
		HttpHeaders header = new HttpHeaders();
		header.setContentType(new MediaType("application", "vnd.dbf"));
		header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bv-decanteurs-perf.dbf");
		try {
			byte[] bytes = gdeComputer.getComputeContext().getBytesResult().getBytesPerfDbf();
			header.setContentLength(bytes.length);
			return ResponseEntity.status(HttpStatus.OK).headers(header).body(bytes);
		} catch (Exception e) {
			return ResponseEntity.noContent().build();
		}
	}

	@GetMapping("/get-result-bytes-debit-dbf")
	public ResponseEntity<byte[]> getComputeResultBytesDebitDBF() {
		HttpHeaders header = new HttpHeaders();
		header.setContentType(new MediaType("application", "vnd.dbf"));
		header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=exutoires-classe.dbf");
		try {
			byte[] bytes = gdeComputer.getComputeContext().getBytesResult().getBytesDebitDbf();
			header.setContentLength(bytes.length);
			return ResponseEntity.status(HttpStatus.OK).headers(header).body(bytes);
		} catch (Exception e) {
			return ResponseEntity.noContent().build();
		}
	}
}
