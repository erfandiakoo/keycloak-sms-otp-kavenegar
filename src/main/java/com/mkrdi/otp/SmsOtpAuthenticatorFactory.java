package com.mkrdi.otp;

import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class SmsOtpAuthenticatorFactory implements AuthenticatorFactory {

    private static final String PROVIDER_ID = "sms-otp-authenticator";

    @Override
    public Authenticator create(KeycloakSession session) {
        return new SmsOtpAuthenticator();
    }

    @Override
    public void init(org.keycloak.Config.Scope config) {}

    @Override
    public void postInit(KeycloakSessionFactory factory) {}

    @Override
    public void close() {}

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getHelpText() {
        return "Authenticator using OTP via Kavenegar SMS";
    }
}