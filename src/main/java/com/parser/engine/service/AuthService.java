package com.parser.engine.service;

import com.parser.engine.dto.request.DeleteAccountRequestDto;
import com.parser.engine.dto.request.LoginRequestDto;
import com.parser.engine.dto.request.SignupRequestDto;
import com.parser.engine.dto.response.AccountDeletionResponseDto;
import com.parser.engine.dto.response.LogoutResponseDto;
import com.parser.engine.dto.response.TokenResponseDto;

public interface AuthService {

    TokenResponseDto signup(SignupRequestDto signupRequest);

    TokenResponseDto login(LoginRequestDto loginRequest);

    TokenResponseDto refreshToken(String refreshToken);

    LogoutResponseDto logout();

    AccountDeletionResponseDto deleteAccount(DeleteAccountRequestDto deleteRequest);

}
