package com.farmer.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {

    // Storage: Email -> OTP
    // In production, use Redis. For now, HashMap is fine.
    private final Map<String, String> otpStorage = new HashMap<>();

    // Config: Expiry time (e.g., 5 minutes) could be added here.

    public String generateOtp(String email) {
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        otpStorage.put(email, otp);
        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        if (email == null || otp == null)
            return false;

        String storedOtp = otpStorage.get(email);
        if (storedOtp != null && storedOtp.equals(otp)) {
            otpStorage.remove(email); // OTP acts as one-time
            return true;
        }
        return false;
    }
}
