package sn.bmbacke.repository;

import sn.bmbacke.models.PasswordResetToken;
import sn.bmbacke.models.enums.TokenTypeEnum;

import java.util.List;
import java.util.Optional;

public interface PasswordResetTokenRepository extends GenericRepository<PasswordResetToken, Long> {
    List<PasswordResetToken> findByTokenAndTokenTypeAndUserId(String token, TokenTypeEnum tokenType, Long userId);
    Optional<PasswordResetToken> findByToken(String token);
    List<PasswordResetToken> findByUserIdAndTokenType(Long userId, TokenTypeEnum tokenType);
}

