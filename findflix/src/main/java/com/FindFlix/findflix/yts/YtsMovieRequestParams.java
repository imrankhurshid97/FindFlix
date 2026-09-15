package com.FindFlix.findflix.yts;

import org.springframework.web.bind.annotation.BindParam;

public record YtsMovieRequestParams(
		Integer limit,
		Integer page,
		String quality,
		@BindParam("minimum_rating") Integer minimumRating,
		@BindParam("query_term") String queryTerm,
		String genre,
		@BindParam("sort_by") String sortBy,
		@BindParam("order_by") String orderBy,
		@BindParam("with_rt_ratings") Boolean withRtRatings
) {
}
