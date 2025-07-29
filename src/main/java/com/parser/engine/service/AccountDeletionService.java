package com.parser.engine.service;

import com.parser.engine.entity.User;

public interface AccountDeletionService {

    /**
     * Delete all refresh tokens for a user
     */
    int deleteUserRefreshTokens(User user);

    /**
     * Delete all files uploaded by a user
     */
    int deleteUserFiles(User user);

    /**
     * Anonymize files by removing user references
     */
    int anonymizeUserFiles(User user);

    /**
     * Delete all properties created by a user
     */
    int deleteUserProperties(User user);

    /**
     * Anonymize properties by removing user references
     */
    int anonymizeUserProperties(User user);
}
