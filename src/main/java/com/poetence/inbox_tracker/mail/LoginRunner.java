package com.poetence.inbox_tracker.mail;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class LoginRunner implements ApplicationRunner {

    private final GraphTokenProvider tokens;

    public LoginRunner(GraphTokenProvider tokens) {
        this.tokens = tokens;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (args.containsOption("login")) {
            tokens.loginWithDeviceCode();
            String token = tokens.getAccessToken();
            System.out.println("Silent token fetch works (token length " + token.length() + ").");
        }
    }
}