package com.hyeonuk.chatting.integ.service.encrypt;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class ShaPasswordEncoder implements PasswordEncoder{
    @Override
    public String encode(String password) {
        StringBuilder sb = new StringBuilder();
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update(password.getBytes());
            byte[] pw = md.digest();

            for(byte b : pw){
                sb.append(String.format("%02x",b));
            }

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }
}
