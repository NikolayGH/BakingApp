package ru.prog_edu.bakingapp.model.recipes;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RecipesInterface {
    @GET("topher/2017/May/59121517_baking/baking.json")
    Call<List<Recipe>> getRecipes();
}
