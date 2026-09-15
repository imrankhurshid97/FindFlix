package com.FindFlix.findflix.yts;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.time.Duration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(YtsProxyProperties.class)
public class YtsClientConfig {

	private static final Logger log = LoggerFactory.getLogger(YtsClientConfig.class);

	@Bean
	RestClient ytsRestClient(
			RestClient.Builder builder,
			@Value("${yts.api.base-url}") String baseUrl,
			@Value("${spring.http.clients.connect-timeout}") Duration connectTimeout,
			@Value("${spring.http.clients.read-timeout}") Duration readTimeout,
			YtsProxyProperties proxyProperties) {
		RestClient.Builder clientBuilder = builder.clone()
				.baseUrl(baseUrl)
				.requestInterceptor(new YtsClientLoggingInterceptor())
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.defaultHeader(HttpHeaders.USER_AGENT, "FindFlix/0.0.1");

		if (proxyProperties.enabled()) {
			clientBuilder.requestFactory(ClientHttpRequestFactoryBuilder.jdk()
					.withProxySelector(proxySelector(proxyProperties))
					.build(HttpClientSettings.defaults()
							.withConnectTimeout(connectTimeout)
							.withReadTimeout(readTimeout)));
			log.info("YTS RestClient using {} proxy {}:{}",
					proxyProperties.type(), proxyProperties.host(), proxyProperties.port());
		}
		else {
			log.info("YTS RestClient using direct connection (no local proxy)");
		}

		return clientBuilder.build();
	}

	private static ProxySelector proxySelector(YtsProxyProperties proxyProperties) {
		InetSocketAddress address = new InetSocketAddress(proxyProperties.host(), proxyProperties.port());
		Proxy.Type proxyType = proxyProperties.type() == YtsProxyProperties.Type.SOCKS
				? Proxy.Type.SOCKS
				: Proxy.Type.HTTP;
		Proxy proxy = new Proxy(proxyType, address);
		return new ProxySelector() {
			@Override
			public List<Proxy> select(URI uri) {
				return List.of(proxy);
			}

			@Override
			public void connectFailed(URI uri, SocketAddress sa, java.io.IOException ioe) {
				log.warn("Local proxy connection failed uri={} address={} reason={}",
						uri, sa, ioe.getMessage());
			}
		};
	}
}
