package com.techreturners.GraphAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Properties;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);

        System.out.println("Graph API");
        System.out.println();

        final Properties oAuthProperties = new Properties();
        try {
            oAuthProperties.load(App.class.getResourceAsStream("/graphapi/oAuth.properties"));
        } catch (IOException e) {
            System.out.println("Unable to read OAuth configuration. Make sure you have a properly formatted oAuth.properties file. See README for details.");
            return;
        }
        initializeGraph(oAuthProperties);
    }

    private static void initializeGraph(Properties properties) {
        try {
            Graph.initializeGraphForUserAuth(properties,
                    challenge -> System.out.println(challenge.getMessage()));
        } catch (Exception e) {
            System.out.println("Error initializing Graph for user auth");
            System.out.println(e.getMessage());
        }
    }
}
