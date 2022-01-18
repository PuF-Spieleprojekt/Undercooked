package com.undercooked.game.entities;

import java.util.Set;

public class Recipe {
    //TODO: Create visual Repesentation
    private Set<Ingredient> ingredients;
    private String name;

    public Recipe(String name, Set<Ingredient> ingredients){
        this.name = name;
        this.ingredients = ingredients;
    }

    @Override
    public String toString() {
        return name + ": " + ingredients.toString();
    }
}
