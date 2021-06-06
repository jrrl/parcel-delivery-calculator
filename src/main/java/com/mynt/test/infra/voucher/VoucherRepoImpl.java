package com.mynt.test.infra.voucher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mynt.test.core.DependencyErrorException;
import com.mynt.test.core.voucher.Voucher;
import com.mynt.test.core.voucher.VoucherRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;

@Component
public class VoucherRepoImpl implements VoucherRepo {
	private static final Logger logger = LoggerFactory.getLogger(VoucherRepoImpl.class);
	private final VoucherProperties properties;
	private final String baseUrl;
	private final ObjectMapper objectMapper;
	private final RestTemplate restTemplate;

	public VoucherRepoImpl(ObjectMapper objectMapper, VoucherProperties properties) {
		this.objectMapper = objectMapper;
		this.properties = properties;
		this.baseUrl = properties.getProtocol() +
			"://" +
			properties.getHost() +
			":" +
			properties.getPort() +
			properties.getBasePath();
		restTemplate = new RestTemplateBuilder()
			.errorHandler(new VoucherErrorHandler())
			.build();
	}

	@Override
	public Voucher getVoucherDetails(String voucherCode) {
		ResponseEntity<String> response = restTemplate.getForEntity(
			baseUrl + properties.getGetVoucherPath() + "?key={apikey}",
			String.class,
			voucherCode, properties.getApikey());
		logger.info("Response: {} {}", response.getStatusCode(), response.getBody());

		try {
			if (response.getStatusCode().is2xxSuccessful()) {
				return objectMapper.readValue(response.getBody(), Voucher.class);
			}
			VoucherErrorResponse errorResponse = objectMapper.readValue(response.getBody(), VoucherErrorResponse.class);
			return new Voucher(null, BigDecimal.ZERO, errorResponse.getError());
		}
		catch (JsonProcessingException e) {
			throw new DependencyErrorException("Invalid response");
		}
	}

	private static class VoucherErrorHandler implements ResponseErrorHandler {

		@Override
		public boolean hasError(ClientHttpResponse response) throws IOException {
			return response.getStatusCode().is5xxServerError() ||
				response.getStatusCode() == HttpStatus.FORBIDDEN ||
				response.getStatusCode() == HttpStatus.UNAUTHORIZED;
		}

		@Override
		public void handleError(ClientHttpResponse response) throws IOException {
			throw new DependencyErrorException("Error connecting to voucher service");
		}
	}
}
