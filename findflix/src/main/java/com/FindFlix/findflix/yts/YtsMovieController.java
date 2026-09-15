package com.FindFlix.findflix.yts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tools.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/movies")
public class YtsMovieController {

	private static final Logger log = LoggerFactory.getLogger(YtsMovieController.class);

	private final YtsMovieService ytsMovieService;

	public YtsMovieController(YtsMovieService ytsMovieService) {
		this.ytsMovieService = ytsMovieService;
	}

	@GetMapping("")
	public JsonNode listMovies(YtsMovieRequestParams params) {
		log.info("Handling list movies limit={} page={} quality={} minimumRating={} queryTerm={} genre={} sortBy={} orderBy={} withRtRatings={}",
				params.limit(),
				params.page(),
				params.quality(),
				params.minimumRating(),
				params.queryTerm(),
				params.genre(),
				params.sortBy(),
				params.orderBy(),
				params.withRtRatings());
		JsonNode response = ytsMovieService.listMovies(params);
		log.info("Handled list movies ytsStatus={}", ytsStatus(response));
		return response;
	}

	@GetMapping("/{movieId}")
	public JsonNode getMovieDetails(
			@PathVariable long movieId,
			@RequestParam(name = "with_images", defaultValue = "true") boolean withImages,
			@RequestParam(name = "with_cast", defaultValue = "true") boolean withCast) {
		log.info("Handling movie details movieId={} withImages={} withCast={}", movieId, withImages, withCast);
		JsonNode response = ytsMovieService.getMovieDetails(movieId, withImages, withCast);
		log.info("Handled movie details movieId={} ytsStatus={}", movieId, ytsStatus(response));
		return response;
	}

	@GetMapping("/{movieId}/suggestions")
	public JsonNode getMovieSuggestions(@PathVariable long movieId) {
		log.info("Handling movie suggestions movieId={}", movieId);
		JsonNode response = ytsMovieService.getMovieSuggestions(movieId);
		log.info("Handled movie suggestions movieId={} ytsStatus={}", movieId, ytsStatus(response));
		return response;
	}

	@GetMapping("/{movieId}/parental-guides")
	public JsonNode getMovieParentalGuides(@PathVariable long movieId) {
		log.info("Handling parental guides movieId={}", movieId);
		JsonNode response = ytsMovieService.getMovieParentalGuides(movieId);
		log.info("Handled parental guides movieId={} ytsStatus={}", movieId, ytsStatus(response));
		return response;
	}

	@GetMapping("/{movieId}/comments")
	public JsonNode getMovieComments(@PathVariable long movieId) {
		log.info("Handling movie comments movieId={}", movieId);
		JsonNode response = ytsMovieService.getMovieComments(movieId);
		log.info("Handled movie comments movieId={} ytsStatus={}", movieId, ytsStatus(response));
		return response;
	}

	private static String ytsStatus(JsonNode response) {
		JsonNode status = response == null ? null : response.get("status");
		return status == null || status.isNull() ? "unknown" : status.asString();
	}
}
