package com.parser.engine.utils;

import com.parser.engine.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component()
public class SecurityUtils {

	private static Authentication getCurrentAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	// TODO: properly impl all
	public static User getLoggedInUser() {
		return null;
	}

	public static String getLoggedInUserEmail() {
		return null;
	}

	public static String getLoggedInUsername() {
		return null;
	}

	public static String getLoggedInUserFirstName() {
		return null;
	}

	public static String getLoggedInUserLastName() {
		return null;
	}

	public static boolean isAuthenticated() {
		return getCurrentAuthentication().isAuthenticated();
	}

}
