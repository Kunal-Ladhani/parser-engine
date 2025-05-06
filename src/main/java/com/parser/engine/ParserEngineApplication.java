package com.parser.engine;

import com.parser.engine.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;

@Slf4j
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@SpringBootApplication
public class ParserEngineApplication {

//	public static void main(String[] args) {
//		SpringApplication.run(ParserEngineApplication.class, args);
//	}

	public static void main(String[] args) throws UnknownHostException {
		var app = new SpringApplication(ParserEngineApplication.class);
		app.setDefaultProperties(Collections.singletonMap("spring.profiles.default", Constants.SpringProfile.DEV));
		Environment env = app.run(args).getEnvironment();
		log.info("Access URLs:\n----------------------------------------------------------\n\t" +
						"Local: \t\thttp://127.0.0.1:{}\n\t" +
						"External: \thttp://{}:{}\n----------------------------------------------------------",
				env.getProperty("server.port"),
				InetAddress.getLocalHost().getHostAddress(),
				env.getProperty("server.port"));
	}
}
