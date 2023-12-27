package com.hhnii.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhnii.model.ChallengeRating;
import com.hhnii.model.Environment;
import com.hhnii.model.Monster;
import com.hhnii.model.Type;
import com.mongodb.client.MongoClient;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Path("/api")
@Slf4j
public class MonsterResource {

    @Inject
    MongoClient mongoClient;

    private final ObjectMapper mapper = new ObjectMapper();

    @GET
    @Path("/monster")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllMonsters(@QueryParam("name") String name) {
        List<Monster> monsters = new ArrayList<>();

        Bson filters = name == null || name.isEmpty() ? Filters.empty() :
                Filters.and(Filters.regex("name", "(?i)" + name));

        mongoClient.getDatabase("dnd").getCollection("monster").find(filters)
                .projection(Projections.fields(Projections.excludeId()))
                .forEach(doc -> {
                    try {
                        monsters.add(mapper.readValue(doc.toJson(), Monster.class));
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage());
                    }
                });

        return Response.ok(new JsonObject().put("monsters", monsters)).build();
    }

    @GET
    @Path("/monster/cr")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCR() {
        List<ChallengeRating> ratings = new ArrayList<>();

        mongoClient.getDatabase("dnd").getCollection("cr").find()
                .projection(Projections.fields(Projections.excludeId()))
                .forEach(doc -> {
                    try {
                        ratings.add(mapper.readValue(doc.toJson(), ChallengeRating.class));
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage());
                    }
                });

        return Response.ok(new JsonObject().put("crs", ratings)).build();
    }

    @GET
    @Path("/monster/environment")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllEnvironments() {
        List<Environment> environments = new ArrayList<>();

        mongoClient.getDatabase("dnd").getCollection("environment").find()
                .projection(Projections.fields(Projections.excludeId()))
                .forEach(doc -> {
                    try {
                        environments.add(mapper.readValue(doc.toJson(), Environment.class));
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage());
                    }
                });

        return Response.ok(new JsonObject().put("environments", environments)).build();
    }

    @GET
    @Path("/monster/type")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTypes() {
        List<Type> types = new ArrayList<>();

        mongoClient.getDatabase("dnd").getCollection("type").find()
                .projection(Projections.fields(Projections.excludeId()))
                .forEach(doc -> {
                    try {
                        types.add(mapper.readValue(doc.toJson(), Type.class));
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage());
                    }
                });

        return Response.ok(new JsonObject().put("types", types)).build();
    }

    @GET
    @Path("/encounter")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEncounter(@QueryParam("threshold") int threshold,
                                 @QueryParam("environment") String environment) {
        List<Monster> monsters = new ArrayList<>(),
                encounter;

        Bson thresholdFilter = threshold > 0 ? Filters.lte("cr.xp", threshold) : Filters.empty();
        Bson environmentFilter = environment != null ? Filters.in("environment.name",
                environment) : Filters.empty();

        mongoClient.getDatabase("dnd").getCollection("monster")
                .find(Filters.and(thresholdFilter, environmentFilter))
                .projection(Projections.fields(Projections.excludeId()))
                .forEach(doc -> {
                    try {
                        monsters.add(mapper.readValue(doc.toJson(), Monster.class));
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage());
                    }
                });

        encounter = buildEncounter(monsters, new ArrayList<>(), threshold);

        return Response.ok(new JsonObject().put("encounter", encounter)).build();
    }

    private List<Monster> buildEncounter(List<Monster> monsters, List<Monster> encounter, int remaining) {
        List<Monster> filtered = monsters.stream().filter(monster -> monster.getCr().getXp() < remaining)
                .collect(Collectors.toList());

        if (filtered.size() > 0) {
            Monster monster = filtered.get(new Random().nextInt(filtered.size()));
            encounter.add(monster);
            return buildEncounter(monsters, encounter, remaining - monster.getCr().getXp());
        } else {
            return encounter;
        }
    }
}
