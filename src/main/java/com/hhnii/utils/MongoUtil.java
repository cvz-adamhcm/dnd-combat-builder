package com.hhnii.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhnii.model.ChallengeRating;
import com.hhnii.model.Environment;
import com.hhnii.model.Type;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;

public class MongoUtil {

    public static void main(String[] args) {
        initCRs();
        initEnvironments();
        initTypes();
    }

    private static void initCRs() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = new FileInputStream("src/main/resources/cr.json");
            String result = new String(is.readAllBytes());

            ChallengeRating[] crs = mapper.readValue(result, ChallengeRating[].class);

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
                    .build();
            MongoClient mongoClient = MongoClients.create(settings);

            if (mongoClient.getDatabase("dnd").getCollection("cr").countDocuments() == 0) {
                Arrays.stream(crs).iterator().forEachRemaining(cr -> {
                    mongoClient.getDatabase("dnd").getCollection("cr")
                            .insertOne(new Document()
                                    .append("challenge", cr.challenge)
                                    .append("xp", cr.xp));
                    System.out.println("Data correctly inserted, cr: " + cr.getChallenge());
                });
            }
            mongoClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initEnvironments() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = new FileInputStream("src/main/resources/environment.json");
            String result = new String(is.readAllBytes());

            Environment[] envs = mapper.readValue(result, Environment[].class);

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
                    .build();
            MongoClient mongoClient = MongoClients.create(settings);

            if (mongoClient.getDatabase("dnd").getCollection("environment").countDocuments() == 0) {
                Arrays.stream(envs).iterator().forEachRemaining(environment -> {
                    mongoClient.getDatabase("dnd").getCollection("environment")
                            .insertOne(new Document()
                                    .append("name", environment.getName()));
                    System.out.println("Data correctly inserted, environment: " + environment.getName());
                });
            }
            mongoClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initTypes() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = new FileInputStream("src/main/resources/type.json");
            String result = new String(is.readAllBytes());

            Type[] types = mapper.readValue(result, Type[].class);

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
                    .build();
            MongoClient mongoClient = MongoClients.create(settings);

            if (mongoClient.getDatabase("dnd").getCollection("type").countDocuments() == 0) {
                Arrays.stream(types).iterator().forEachRemaining(type -> {
                    mongoClient.getDatabase("dnd").getCollection("type")
                            .insertOne(new Document()
                                    .append("name", type.getName()));
                    System.out.println("Data correctly inserted, type: " + type.getName());
                });
            }
            mongoClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
