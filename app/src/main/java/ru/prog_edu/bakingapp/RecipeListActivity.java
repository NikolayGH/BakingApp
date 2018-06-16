package ru.prog_edu.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.prog_edu.bakingapp.model.recipes.Recipe;
import ru.prog_edu.bakingapp.model.recipes.RecipesInterface;
import ru.prog_edu.bakingapp.utilities.NetworkState;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class RecipeListActivity extends AppCompatActivity implements RecipeCardsFragment.OnRecipeClickListener {

    private List<Recipe> recipeList;
    private boolean mTwoPane;
    private final Set<String> ingredientsList = new HashSet<>();
    private static final String PREF_KEY_INGREDIENTS = "ingredientData";

    private static final Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl("https://d17h27t6h515a5.cloudfront.net/")
            .addConverterFactory(GsonConverterFactory.create());
    private static final Retrofit retrofit = builder.build();
    private CountingIdlingResource countingIdlingResource;

    private void setIdlingResource(CountingIdlingResource countingIdlingResource) {
        this.countingIdlingResource = countingIdlingResource;
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        return countingIdlingResource;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        countingIdlingResource = new CountingIdlingResource("Data loading");
        setIdlingResource(countingIdlingResource);

        NetworkState networkState = new NetworkState(this);
        boolean isOnline = networkState.isOnline();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        mTwoPane = findViewById(R.id.control_content_fragment) != null;
        countingIdlingResource.increment();
        if(isOnline){
            getAllRecipes();
        }else{
            Toast.makeText(this, "No Internet connection", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRecipeSelected(int position) {

        for (int i = 0; i < recipeList.get(position).getIngredients().size(); i++) {
            ingredientsList.add(recipeList.get(position).getIngredients().get(i).getIngredient());
        }
        SharedPreferences sharedPref = this.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);
        sharedPref.edit().putStringSet(PREF_KEY_INGREDIENTS, ingredientsList).apply();

        Recipe selectedRecipe;
        selectedRecipe = recipeList.get(position);

        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(Recipe.class.getCanonicalName(), selectedRecipe);
        startActivity(intent);
    }

    private void getAllRecipes(){
        final RecipesInterface recipesInterface = retrofit.create(RecipesInterface.class);
        Call<List<Recipe>> recipeCall = recipesInterface.getRecipes();
        recipeCall.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
                if(response.isSuccessful()){
                    recipeList = response.body();
                    if (recipeList != null) {
                        Log.i("numberOfRecipes", String.valueOf(recipeList.size()));
                    }
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    RecipeCardsFragment fragment;

                    new RecipeCardsFragment();
                    fragment = RecipeCardsFragment.newInstance(recipeList, mTwoPane);
                    transaction.add(R.id.first_content_fragment, fragment);
                    transaction.commit();
                    countingIdlingResource.decrement();
                }else{
                    try {
                        if (response.errorBody() != null) {
                            Log.e("ERROR BODY", response.errorBody().string());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
                Log.e("Failure: ", t.getMessage());
            }
        });
    }
}
