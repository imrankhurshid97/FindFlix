package com.FindFlix.findflix.yts;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestLifecycleFilter extends OncePerRequestFilter {

	private static final Logger log = LoggerFactory.getLogger(RequestLifecycleFilter.class);
	private static final String REQUEST_ID = "requestId";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String requestId = UUID.randomUUID().toString().substring(0, 8);
		MDC.put(REQUEST_ID, requestId);
		long startedAt = System.nanoTime();
		String uri = requestUri(request);
		log.info("Request started method={} uri={}", request.getMethod(), uri);
		try {
			filterChain.doFilter(request, response);
		}
		finally {
			log.info("Request completed method={} uri={} status={} durationMs={}",
					request.getMethod(),
					uri,
					response.getStatus(),
					Duration.ofNanos(System.nanoTime() - startedAt).toMillis());
			MDC.remove(REQUEST_ID);
		}
	}

	private static String requestUri(HttpServletRequest request) {
		String query = request.getQueryString();
		return query == null || query.isBlank() ? request.getRequestURI() : request.getRequestURI() + "?" + query;
	}
}
