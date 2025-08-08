package com.parser.engine.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.ModelMapper;
import org.modelmapper.jackson.JacksonModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.ZoneId;
import java.util.TimeZone;

@Configuration
public class BeanConfig {

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder()
				.modulesToInstall(new JavaTimeModule(), new Jdk8Module())
				.serializationInclusion(JsonInclude.Include.NON_NULL)
				.failOnUnknownProperties(false)
				.defaultViewInclusion(false)
				.timeZone(TimeZone.getTimeZone(ZoneId.of("Asia/Kolkata")))
				.simpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
				.build();

		// Configure date/time serialization
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
		objectMapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
		objectMapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);

		// register a module to clean up non-breaking spaces in all string fields
		SimpleModule module = new SimpleModule();
		module.addSerializer(String.class, new NonBreakingSpaceSanitizer());
		module.addDeserializer(String.class, new NonBreakingSpaceSanitizerDeserializer());
		objectMapper.registerModule(module);

		return objectMapper;
	}

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.registerModule(new JacksonModule(objectMapper()));
		return modelMapper;
	}

}
