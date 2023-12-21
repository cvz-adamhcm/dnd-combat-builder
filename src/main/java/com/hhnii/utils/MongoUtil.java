package com.hhnii.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhnii.model.ChallengeRating;
import com.hhnii.model.Environment;
import com.hhnii.model.Monster;
import com.hhnii.model.Type;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        initCRs();
        initEnvironments();
        initTypes();
        initMonsters();
    }

    private static void initCRs() {
        try {
            InputStream is = new FileInputStream("src/main/resources/cr.json");
            String result = new String(is.readAllBytes());

            ChallengeRating[] crs = mapper.readValue(result, ChallengeRating[].class);

            CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                    fromProviders(PojoCodecProvider.builder().automatic(true).build()));

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
                    .codecRegistry(pojoCodecRegistry)
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
            InputStream is = new FileInputStream("src/main/resources/environment.json");
            String result = new String(is.readAllBytes());

            Environment[] envs = mapper.readValue(result, Environment[].class);

            CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                    fromProviders(PojoCodecProvider.builder().automatic(true).build()));

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
                    .codecRegistry(pojoCodecRegistry)
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
            InputStream is = new FileInputStream("src/main/resources/type.json");
            String result = new String(is.readAllBytes());

            Type[] types = mapper.readValue(result, Type[].class);

            CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                    fromProviders(PojoCodecProvider.builder().automatic(true).build()));

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
                    .codecRegistry(pojoCodecRegistry)
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

    private static void initMonsters() {
        try {
            InputStream is = new FileInputStream("src/main/resources/monster.json");
            String result = new String(is.readAllBytes());

            Monster[] monsters = mapper.readValue(result, Monster[].class);

            CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                    fromProviders(PojoCodecProvider.builder().automatic(true).build()));

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
                    .codecRegistry(pojoCodecRegistry)
                    .build();
            MongoClient mongoClient = MongoClients.create(settings);

            Arrays.stream(monsters).iterator().forEachRemaining(monster -> {
                Bson filter = Filters.eq("name", monster.getName());
                Bson update = Updates.combine(
                        Updates.set("cr", monster.getCr()),
                        Updates.set("type", monster.getType()),
                        Updates.set("environment", monster.getEnvironment()));

                UpdateOptions options = new UpdateOptions().upsert(true);

                mongoClient.getDatabase("dnd").getCollection("monster")
                        .updateOne(filter, update, options);
                System.out.println("Data correctly upserted, type: " + monster.getName());
            });

            mongoClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
