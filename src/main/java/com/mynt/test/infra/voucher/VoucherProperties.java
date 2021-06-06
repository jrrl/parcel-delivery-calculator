package com.mynt.test.infra.voucher;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Configuration;

//@Configuration
@ConfigurationProperties(prefix = "voucher")
@ConstructorBinding
public class VoucherProperties {
	private final String host;
	private final String protocol;
	private final String basePath;
	private final String getVoucherPath;
	private final int port;
	private final String apikey;

	public VoucherProperties(String host, String protocol, String basePath, String getVoucherPath, int port, String apikey) {
		this.host = host;
		this.protocol = protocol;
		this.basePath = basePath;
		this.getVoucherPath = getVoucherPath;
		this.port = port;
		this.apikey = apikey;
	}

	public String getHost() {
		return host;
	}

	public String getProtocol() {
		return protocol;
	}

	public String getBasePath() {
		return basePath;
	}

	public String getGetVoucherPath() {
		return getVoucherPath;
	}

	public int getPort() {
		return port;
	}

	public String getApikey() {
		return apikey;
	}
}
