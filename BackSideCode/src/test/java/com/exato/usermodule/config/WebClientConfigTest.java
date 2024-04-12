package com.exato.usermodule.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest
@Import(WebClientConfig.class)
class WebClientConfigTest {

	 @Autowired
	    private WebClient.Builder webClientBuilder;

	    @Test
	    void testWebClientBuilder() {
	        // Assert
	        assertNotNull(webClientBuilder);
	        WebClient webClient = webClientBuilder.build();
	        assertNotNull(webClient);
	    }

}
