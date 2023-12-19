package com.hhnii.resource;

import com.hhnii.model.ChallengeRating;
import com.hhnii.model.Environment;
import com.hhnii.model.Monster;
import com.hhnii.model.Type;
import com.mongodb.client.MongoClient;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

@Path("/api")
public class MonsterResource {

    @Inject
    MongoClient mongoClient;

    @GET
    @Path("/monster")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllMonsters() {
        Monster monster = new Monster("asdasd", new ChallengeRating("1/2", 500),
                new Type("Aberration"), new ArrayList<>());

        return Response.ok(monster, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/monster/cr")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCR() {
        List<ChallengeRating> ratings = new ArrayList<>();

        mongoClient.getDatabase("dnd").getCollection("cr").find()
                .forEach(doc -> ratings.add(
                        new ChallengeRating(doc.getString("challenge"),
                                doc.getInteger("xp"))));

        return Response.ok(new JsonObject().put("crs", ratings)).build();
    }

    @GET
    @Path("/monster/environment")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllEnvironments() {
        List<Environment> environments = new ArrayList<>();

        mongoClient.getDatabase("dnd").getCollection("environment").find()
                .forEach(doc -> environments.add(
                        new Environment(doc.getString("name"))));

        return Response.ok(new JsonObject().put("environments", environments)).build();
    }

}
