package com.vidaltpa.vidaltpa.SendRequest;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements CommandLineRunner {

    private final APILogic apilogic;

    public AppRunner(APILogic apilogic) {
        this.apilogic = apilogic;
    }

    @Override
    public void run(String... args) {
        apilogic.runTask();
    }
}
