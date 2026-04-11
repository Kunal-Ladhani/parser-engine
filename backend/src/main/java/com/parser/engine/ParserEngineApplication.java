package com.parser.engine;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import com.parser.engine.common.Constants;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class ParserEngineApplication {

	public static void main(String[] args) throws UnknownHostException {
		var app = new SpringApplication(ParserEngineApplication.class);
		app.setDefaultProperties(Collections.singletonMap("spring.profiles.default", Constants.SpringProfile.DEV));
		Environment env = app.run(args).getEnvironment();
		log.info("""
						Access URLs:
						----------------------------------------------------------
						\t\
						Local: \t\thttp://127.0.0.1:{}
						\t\
						External: \thttp://{}:{}
						----------------------------------------------------------""",
				env.getProperty("server.port"),
				InetAddress.getLocalHost().getHostAddress(),
				env.getProperty("server.port"));
	}

	@PostConstruct
	public void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
	}
}
