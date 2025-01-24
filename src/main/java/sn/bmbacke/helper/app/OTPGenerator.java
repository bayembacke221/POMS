package sn.bmbacke.helper.app;


import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class OTPGenerator {
    private static final String CHARACTERS = "0123456789";
    private static final int OTP_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateOTP() {
        return generateOTP(OTP_LENGTH);
    }

    public static String generateOTP(int length) {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return otp.toString();
    }
}
