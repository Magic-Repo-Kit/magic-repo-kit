package com.magicrepokit.chat;

import cn.hutool.core.lang.UUID;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class CodeChallenge {
    public static void main(String[] args) {
        String codeVerifier = generateCodeVerifier();
        String codeChallenge = generateCodeChallenge(codeVerifier);

        System.out.println("code_verifier: " + codeVerifier);
        System.out.println("code_challenge: " + codeChallenge);
        for (int i = 3; i > 0; i--) {
            System.out.println("UUID: " + UUID.fastUUID().toString());
        }

    }
    private static String generateCodeVerifier() {
        byte[] token = new byte[32];
        new java.security.SecureRandom().nextBytes(token);
        String codeVerifier = Base64.getUrlEncoder().withoutPadding().encodeToString(token);
        return codeVerifier;
    }

    private static String generateCodeChallenge(String codeVerifier) {
        byte[] codeVerifierBytes = codeVerifier.getBytes();
        byte[] codeChallengeBytes;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(codeVerifierBytes);
            byte[] digest = messageDigest.digest();
            codeChallengeBytes = Base64.getUrlEncoder().withoutPadding().encode(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        String codeChallenge = new String(codeChallengeBytes);
        return codeChallenge;
    }
}
