package com.example.demogateway;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.MultipartHttpMessageReader;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.MultiValueMap;

import static junit.framework.TestCase.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = DEFINED_PORT)
@Import(TestController.class)
public class DemoGatewayApplicationTests {

	@LocalServerPort
	protected int port = 0;

	@Test
	public void contextLoads() {

		String baseUri = "http://localhost:" + port;

		WebTestClient testClient = WebTestClient.bindToServer()
				.baseUrl(baseUri)
				.build();

		testClient.post()
				.uri("/gw/test/post")
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.syncBody(generateBody())
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody(String.class)
				.consumeWith(result -> {
					assertTrue(result.getResponseBody().contains("data"));
					assertTrue(result.getResponseBody().contains("base64"));
				});

	}

	private MultiValueMap<String, HttpEntity<?>> generateBody() {
		ClassPathResource file = new ClassPathResource("/test.txt", MultipartHttpMessageReader.class);
		MultipartBodyBuilder builder = new MultipartBodyBuilder();
		builder.part("file", file);
		return builder.build();
	}


}
