package sn.bmbacke.models;

import jakarta.persistence.*;
import lombok.Data;
import sn.bmbacke.models.enums.TokenTypeEnum;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
@Data
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type")
    private TokenTypeEnum tokenType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    private boolean valid = true;

    public PasswordResetToken() {}

    public PasswordResetToken(String token, TokenTypeEnum tokenType, User user, int expiryTimeInMinutes) {
        this.token = token;
        this.tokenType = tokenType;
        this.user = user;
        this.expiryDate = LocalDateTime.now().plusMinutes(expiryTimeInMinutes);
        this.valid = true;
    }
}