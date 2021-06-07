package com.mynt.test.infra.voucher;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;

public class WireMockConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {
	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		WireMockConfiguration mockConfiguration = new WireMockConfiguration().dynamicPort();
		WireMockServer mockServer = new WireMockServer(mockConfiguration);
		mockServer.start();

		applicationContext.getBeanFactory().registerSingleton("wireMock", mockServer);
		applicationContext.addApplicationListener(event -> {
			if (event instanceof ContextClosedEvent) {
				mockServer.stop();
			}
		});

		TestPropertyValues.of(
			"voucher.host=localhost",
			"voucher.port=" + mockServer.port(),
			"voucher.protocol=http",
			"voucher.basePath=/voucher",
			"voucher.getVoucherPath=/{voucherCode}",
			"voucher.apikey=apikey"
		).applyTo(applicationContext.getEnvironment());
	}
}
