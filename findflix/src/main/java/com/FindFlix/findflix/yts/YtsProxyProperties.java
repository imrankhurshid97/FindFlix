package com.FindFlix.findflix.yts;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "yts.proxy")
public record YtsProxyProperties(
		@DefaultValue("false") boolean enabled,
		@DefaultValue("127.0.0.1") String host,
		@DefaultValue("7890") int port,
		@DefaultValue("HTTP") Type type
) {

	public enum Type {
		HTTP,
		SOCKS
	}
}
