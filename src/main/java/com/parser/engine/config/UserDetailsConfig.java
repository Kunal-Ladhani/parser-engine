package com.parser.engine.config;

import com.parser.engine.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class UserDetailsConfig {

	private final UserRepository userRepository;

	@Autowired
	public UserDetailsConfig(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return usernameOrEmail -> {
			log.info("Loading user by username or email: {}", usernameOrEmail);
			return userRepository.findByEmail(usernameOrEmail)
					.or(() -> userRepository.findByUsername(usernameOrEmail))
					.map(user -> {
						log.info("User found: {} with role: {}", user.getEmail(), user.getRole());
						return new User(
								user.getEmail(), // Using email as username for UserDetails
								user.getPassword(),
								List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
						);
					})
					.orElseThrow(() -> {
						log.error("User not found: {}", usernameOrEmail);
						return new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail);
					});
		};
	}

}
