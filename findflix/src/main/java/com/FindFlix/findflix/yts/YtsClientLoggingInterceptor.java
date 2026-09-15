package com.FindFlix.findflix.yts;

import java.io.IOException;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class YtsClientLoggingInterceptor implements ClientHttpRequestInterceptor {

	private static final Logger log = LoggerFactory.getLogger(YtsClientLoggingInterceptor.class);

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		long startedAt = System.nanoTime();
		log.info("YTS call started method={} uri={}", request.getMethod(), request.getURI());
		try {
			ClientHttpResponse response = execution.execute(request, body);
			log.info("YTS call completed method={} uri={} status={} durationMs={}",
					request.getMethod(),
					request.getURI(),
					response.getStatusCode().value(),
					Duration.ofNanos(System.nanoTime() - startedAt).toMillis());
			return response;
		}
		catch (IOException ex) {
			log.error("YTS call failed method={} uri={} durationMs={} reason={}",
					request.getMethod(),
					request.getURI(),
					Duration.ofNanos(System.nanoTime() - startedAt).toMillis(),
					ex.getMessage());
			throw ex;
		}
	}
}
