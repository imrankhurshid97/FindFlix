package com.FindFlix.findflix.yts;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@RestControllerAdvice(assignableTypes = YtsMovieController.class)
public class YtsApiExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(YtsApiExceptionHandler.class);

	@ExceptionHandler(RestClientResponseException.class)
	public ResponseEntity<Map<String, Object>> handleYtsHttpError(RestClientResponseException ex) {
		log.warn("YTS HTTP error status={} message={}", ex.getStatusCode().value(), ex.getStatusText());
		return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
				"error", "YTS API request failed",
				"status", ex.getStatusCode().value(),
				"message", ex.getStatusText()));
	}

	@ExceptionHandler(RestClientException.class)
	public ResponseEntity<Map<String, Object>> handleYtsError(RestClientException ex) {
		log.error("YTS request failed reason={}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
				"error", "Unable to reach YTS API",
				"message", ex.getMessage() != null ? ex.getMessage() : "Unexpected error"));
	}
}
