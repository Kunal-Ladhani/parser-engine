package com.parser.engine.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Objects;

public class NonBreakingSpaceSanitizer extends JsonSerializer<String> {

	@Override
	public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
		if (Objects.nonNull(value)) {
			jsonGenerator.writeString(value.replace("\u00A0", ""));
		} else {
			jsonGenerator.writeNull();
		}
	}

}
