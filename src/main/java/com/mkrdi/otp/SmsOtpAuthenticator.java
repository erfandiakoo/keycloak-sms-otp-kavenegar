package com.mkrdi.otp;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.messages.Messages;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class SmsOtpAuthenticator implements Authenticator {
    
    private static final long OTP_EXPIRATION_TIME = TimeUnit.MINUTES.toMillis(5);
    private ConcurrentHashMap<String, OtpDetails> otpStorage = new ConcurrentHashMap<>();

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        UserModel user = context.getUser();
        String phoneNumber = user.getFirstAttribute("phone_number");

        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            String otp = generateOtp();
            otpStorage.put(phoneNumber, new OtpDetails(otp, System.currentTimeMillis()));

            // Send OTP via Kavenegar
            KavenegarSmsSender.sendOtp(phoneNumber, otp);

            // Challenge user to input the OTP received via SMS
            context.challenge(context.form().createForm("login-otp.ftl"));
        } else {
            context.failureChallenge(AuthenticationFlowContext.CODE, context.form()
                    .setError(Messages.INVALID_USER)
                    .createForm("login-otp-error.ftl"));
        }
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        String phoneNumber = context.getUser().getFirstAttribute("phone_number");
        String enteredOtp = context.getHttpRequest().getDecodedFormParameters().getFirst("otp");

        if (validateOtp(phoneNumber, enteredOtp)) {
            context.success();
        } else {
            context.failureChallenge(AuthenticationFlowContext.CODE, context.form()
                    .setError(Messages.INVALID_TOTP)
                    .createForm("login-otp.ftl"));
        }
    }

    private String generateOtp() {
        // Generate a 6-digit OTP (you can enhance this logic further)
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    private boolean validateOtp(String phoneNumber, String enteredOtp) {
        OtpDetails storedOtpDetails = otpStorage.get(phoneNumber);
        if (storedOtpDetails != null) {
            // Check for expiration
            if (System.currentTimeMillis() - storedOtpDetails.timestamp > OTP_EXPIRATION_TIME) {
                otpStorage.remove(phoneNumber); // Remove expired OTP
                return false;
            }
            return storedOtpDetails.otp.equals(enteredOtp);
        }
        return false;
    }

    @Override
    public void close() {}

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return user.getFirstAttribute("phone_number") != null;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {}

    // Helper class to store OTP and its timestamp
    private static class OtpDetails {
        String otp;
        long timestamp;

        OtpDetails(String otp, long timestamp) {
            this.otp = otp;
            this.timestamp = timestamp;
        }
    }
}