package com.FindFlix.findflix.yts;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class YtsMovieServiceTest {

	private MockRestServiceServer server;
	private YtsMovieService ytsMovieService;

	@BeforeEach
	void setUp() {
		RestClient.Builder builder = RestClient.builder().baseUrl("https://yts.mx/api/v2/");
		this.server = MockRestServiceServer.bindTo(builder).build();
		this.ytsMovieService = new YtsMovieService(builder.build());
	}

	@Test
	void listMoviesUsesPublicYtsQueryParams() {
		server.expect(requestTo("https://yts.mx/api/v2/list_movies.json?limit=20&page=1&sort_by=rating&order_by=desc"))
				.andRespond(withSuccess("{\"status\":\"ok\"}", MediaType.APPLICATION_JSON));

		ytsMovieService.listMovies(new YtsMovieRequestParams(20, 1, null, null, null, null, "rating", "desc", null));

		server.verify();
	}

	@Test
	void movieDetailsRequestsImagesAndCast() {
		server.expect(requestTo("https://yts.mx/api/v2/movie_details.json?movie_id=10&with_images=true&with_cast=true"))
				.andRespond(withSuccess("{\"status\":\"ok\"}", MediaType.APPLICATION_JSON));

		ytsMovieService.getMovieDetails(10, true, true);

		server.verify();
	}

	@Test
	void movieSuggestionsUsesMovieId() {
		server.expect(requestTo("https://yts.mx/api/v2/movie_suggestions.json?movie_id=10"))
				.andRespond(withSuccess("{\"status\":\"ok\"}", MediaType.APPLICATION_JSON));

		ytsMovieService.getMovieSuggestions(10);

		server.verify();
	}
}
