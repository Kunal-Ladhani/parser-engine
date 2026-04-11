package com.parser.engine.dto.response;

import lombok.*;

@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {

	private String token;

	private String tokenType;

	private String username;

	private String email;

	private String role;

}