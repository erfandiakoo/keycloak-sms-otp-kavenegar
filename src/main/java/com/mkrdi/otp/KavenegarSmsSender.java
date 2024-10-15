package com.mkrdi.otp;

import com.kavenegar.sdk.KavenegarApi;
import com.kavenegar.sdk.excepctions.ApiException;
import com.kavenegar.sdk.excepctions.HttpException;
import com.kavenegar.sdk.models.SendResult;

import java.util.Random;

public class KavenegarSmsSender {

    private static final String KAVENEGAR_API_KEY = "your_kavenegar_api_key"; // Replace with your API key

    public static void sendOtp(String phoneNumber) {
        KavenegarApi api = new KavenegarApi(KAVENEGAR_API_KEY);

        // Generate a 6-digit OTP
        String otp = generateOtp();

        try {
            SendResult result = api.send(phoneNumber, "Your OTP code is: " + otp);
            System.out.println("OTP sent to " + phoneNumber + ": " + result.getMessageid());
        } catch (ApiException e) {
            System.err.println("API Exception: " + e.getMessage());
        } catch (HttpException e) {
            System.err.println("HTTP Exception: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    private static String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generate a 6-digit OTP
        return String.valueOf(otp);
    }
}