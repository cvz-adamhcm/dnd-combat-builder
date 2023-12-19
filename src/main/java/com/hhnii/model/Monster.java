package com.hhnii.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class Monster {
    private String name;
    private ChallengeRating cr;
    private Type type;
    private List<Environment> environment;
}
