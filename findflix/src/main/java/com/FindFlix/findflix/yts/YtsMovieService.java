package com.FindFlix.findflix.yts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

import tools.jackson.databind.JsonNode;

@Service
public class YtsMovieService {

	private static final Logger log = LoggerFactory.getLogger(YtsMovieService.class);

	private final RestClient ytsRestClient;

	public YtsMovieService(@Qualifier("ytsRestClient") RestClient ytsRestClient) {
		this.ytsRestClient = ytsRestClient;
	}

	public JsonNode listMovies(YtsMovieRequestParams params) {
		return get("list_movies.json", uriBuilder -> {
			if (params == null) {
				return uriBuilder;
			}
			queryParam(uriBuilder, "limit", params.limit());
			queryParam(uriBuilder, "page", params.page());
			queryParam(uriBuilder, "quality", params.quality());
			queryParam(uriBuilder, "minimum_rating", params.minimumRating());
			queryParam(uriBuilder, "query_term", params.queryTerm());
			queryParam(uriBuilder, "genre", params.genre());
			queryParam(uriBuilder, "sort_by", params.sortBy());
			queryParam(uriBuilder, "order_by", params.orderBy());
			queryParam(uriBuilder, "with_rt_ratings", params.withRtRatings());
			return uriBuilder;
		});
	}

	public JsonNode getMovieDetails(long movieId, Boolean withImages, Boolean withCast) {
		return get("movie_details.json", uriBuilder -> {
			uriBuilder.queryParam("movie_id", movieId);
			queryParam(uriBuilder, "with_images", withImages);
			queryParam(uriBuilder, "with_cast", withCast);
			return uriBuilder;
		});
	}

	public JsonNode getMovieSuggestions(long movieId) {
		return getByMovieId("movie_suggestions.json", movieId);
	}

	public JsonNode getMovieParentalGuides(long movieId) {
		return getByMovieId("movie_parental_guides.json", movieId);
	}

	public JsonNode getMovieComments(long movieId) {
		return getByMovieId("movie_comments.json", movieId);
	}

	private JsonNode getByMovieId(String path, long movieId) {
		return get(path, uriBuilder -> uriBuilder.queryParam("movie_id", movieId));
	}

	private JsonNode get(String path, java.util.function.UnaryOperator<UriBuilder> queryCustomizer) {
		log.info("Fetching YTS resource path={}", path);
		JsonNode body = ytsRestClient.get()
				.uri(uriBuilder -> queryCustomizer.apply(uriBuilder.path(path)).build())
				.retrieve()
				.body(JsonNode.class);
		JsonNode status = body == null ? null : body.get("status");
		log.info("Fetched YTS resource path={} ytsStatus={}",
				path,
				status == null || status.isNull() ? "unknown" : status.asString());
		return body;
	}

	private static void queryParam(UriBuilder uriBuilder, String name, Object value) {
		if (value != null && !(value instanceof String string && string.isBlank())) {
			uriBuilder.queryParam(name, value);
		}
	}
}
