package com.parser.engine.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Parser Engine")
						.description("API documentation for server parsing and processing property listing files.")
						.version("1.0")
						.contact(new Contact()
								.name("Kunal Ladhani")
								.email("k.ladhani1@gmail.com")
								.url("https://kunal-ladhani.github.io")))
				.servers(List.of(new Server()
								.url("http://localhost:8080")
								.description("local"),
						new Server()
								.url("http://localhost:8081")
								.description("live"))
				)
				;
	}

}
