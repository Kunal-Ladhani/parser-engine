package com.parser.engine.service;

import com.parser.engine.dto.request.*;
import com.parser.engine.dto.response.*;

public interface AuthService {

	TokenResponseDto signup(SignupRequestDto signupRequest);

	TokenResponseDto login(LoginRequestDto loginRequest);

	TokenResponseDto refreshToken(String refreshToken, String authorizationHeader);

	LogoutResponseDto logout();

	AccountDeletionResponseDto deleteAccount(DeleteAccountRequestDto deleteRequest);

	UpdateProfileResponseDto updateProfile(UpdateProfileRequestDto updateRequest);

	ChangePasswordResponseDto changePassword(ChangePasswordRequestDto changePasswordRequest);

}
