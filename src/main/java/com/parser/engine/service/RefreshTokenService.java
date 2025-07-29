package com.parser.engine.service;

import com.parser.engine.entity.RefreshToken;
import com.parser.engine.entity.User;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user);

    RefreshToken verifyExpiration(RefreshToken token);

    RefreshToken findByToken(String token);

    void revokeToken(RefreshToken token);

    int revokeAllUserTokens(User user);

    void cleanupExpiredTokens();
}
