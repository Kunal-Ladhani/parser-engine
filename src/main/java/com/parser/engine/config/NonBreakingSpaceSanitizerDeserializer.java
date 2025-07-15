package com.parser.engine.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Objects;

public class NonBreakingSpaceSanitizerDeserializer extends JsonDeserializer<String> {

	@Override
	public String deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		String value = parser.getValueAsString();
		return Objects.nonNull(value) ? value.replace("\u00A0", "") : null;
	}

}
