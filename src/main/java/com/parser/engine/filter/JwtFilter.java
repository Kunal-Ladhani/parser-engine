package com.parser.engine.filter;

import com.parser.engine.config.JwtConfig;
import com.parser.engine.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtils jwtUtils;
	private final JwtConfig jwtConfig;
	private final UserDetailsService userDetailsService;

	@Autowired
	public JwtFilter(JwtUtils jwtUtils,
					 JwtConfig jwtConfig,
					 UserDetailsService userDetailsService) {
		this.jwtUtils = jwtUtils;
		this.jwtConfig = jwtConfig;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		final String authHeader = request.getHeader(jwtConfig.getHeaderString());

		// Auth header is null or incorrect then exit
		if (authHeader == null || !authHeader.startsWith(jwtConfig.getTokenPrefix())) {
			filterChain.doFilter(request, response);
			return;
		}

		final String jwt = authHeader.substring(jwtConfig.getTokenPrefix().length());

		// jwt is the token
		final String username = jwtUtils.extractUsername(jwt);
		if (username == null) {
			log.error("Username could not be extracted from token.");
			filterChain.doFilter(request, response);
			return;
		}

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null) {
			//Authenticate the user
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);

			if (jwtUtils.isTokenValid(jwt, userDetails)) {
				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				log.info("User {} authenticated successfully", username);
			} else {
				log.error("JWT token is not valid for user: {}", username);
			}
		}

		filterChain.doFilter(request, response);
	}

}
 