package sii.ms_corrector.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/healthcheck")
public class Healthcheck {
	@GetMapping
	public ResponseEntity<String> ping() {
		return ResponseEntity.ok("Service online\n");
	}
}
