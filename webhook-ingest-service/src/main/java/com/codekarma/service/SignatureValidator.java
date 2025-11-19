package com.codekarma.service;

import java.util.Locale;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


import org.springframework.stereotype.Component;


@Component
public class SignatureValidator {


    private static final String HMAC_SHA256 = "HmacSHA256";


    public boolean isValid(String secret, byte[] payloadBody, String signatureHeader) {
        if (secret == null || secret.isBlank()) {
// No secret configured — don't validate
            return true;
        }
        if (signatureHeader == null || !signatureHeader.startsWith("sha256=")) {
            return false;
        }


        try {
            String expected = "sha256=" + toHex(hmacSha256(secret.getBytes(), payloadBody));
// constant time compare
            return constantTimeEquals(expected.getBytes(), signatureHeader.getBytes());
        } catch (Exception e) {
            return false;
        }
    }


    private byte[] hmacSha256(byte[] key, byte[] data) throws Exception {
        Mac mac = Mac.getInstance(HMAC_SHA256);
        mac.init(new SecretKeySpec(key, HMAC_SHA256));
        return mac.doFinal(data);
    }


    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format(Locale.ROOT, "%02x", b));
        }
        return sb.toString();
    }


    private boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a.length != b.length) return false;
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
    }
}