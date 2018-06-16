package ru.prog_edu.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.Toast;
import ru.prog_edu.bakingapp.model.recipes.Recipe;

/**
 * An activity representing a single Recipe detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RecipeListActivity}.
 */
public class RecipeDetailActivity extends AppCompatActivity implements RecipeDetailFragment.OnStepClickListener{

    private boolean mTwoPane;
    private Recipe selectedRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTwoPane = findViewById(R.id.second_content_fragment) != null;

        selectedRecipe = getIntent().getParcelableExtra(Recipe.class.getCanonicalName());

        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        new RecipeDetailFragment();
        RecipeDetailFragment recipeDetailFragment = RecipeDetailFragment.newInstance(selectedRecipe);
        transaction.replace(R.id.sample_content_fragment, recipeDetailFragment);
        //transaction.addToBackStack(null);
        transaction.commit();

        if(mTwoPane){
            FragmentTransaction transaction1 = this.getSupportFragmentManager().beginTransaction();
            new StepDetailFragment();
            StepDetailFragment stepDetailFragment = StepDetailFragment.newInstance(selectedRecipe, 0);
            transaction1.replace(R.id.second_content_fragment, stepDetailFragment);
            //transaction1.addToBackStack(null);
            transaction1.commit();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, RecipeListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStepSelected(int stepPosition) {
        if(mTwoPane){
            FragmentTransaction transaction1 = this.getSupportFragmentManager().beginTransaction();
            new StepDetailFragment();
            StepDetailFragment stepDetailFragment = StepDetailFragment.newInstance(selectedRecipe, stepPosition);
            transaction1.replace(R.id.second_content_fragment, stepDetailFragment);
            transaction1.addToBackStack(null);
            transaction1.commit();
        }else {
            FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
            new StepDetailFragment();
            StepDetailFragment stepDetailFragment = StepDetailFragment.newInstance(selectedRecipe, stepPosition);
            transaction.replace(R.id.sample_content_fragment, stepDetailFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
