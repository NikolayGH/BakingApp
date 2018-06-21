package ru.prog_edu.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import ru.prog_edu.bakingapp.model.recipes.Recipe;


public class RecipeDetailActivity extends AppCompatActivity implements RecipeDetailFragment.OnStepClickListener{

    private boolean mTwoPane;
    private Recipe selectedRecipe;
    private static final String SELECTED_RECIPE = "selectedRecipe";
    private String currentFragmentInSampleContent;

    private static final String RECIPE_DETAIL_FRAGMENT = "recipeDetailFragment";
    private static final String STEP_DETAIL_FRAGMENT = "stepDetailFragment";
    private static final String RECIPE_DETAIL_FRAGMENT_TAG = "recipeDetailFragmentTag";
    private static final String STEP_DETAIL_FRAGMENT_TAG = "stepDetailFragmentTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTwoPane = findViewById(R.id.second_content_fragment)!=null;
        selectedRecipe = getIntent().getParcelableExtra(Recipe.class.getCanonicalName());

        if(savedInstanceState==null){

            if(!mTwoPane){
                FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
                new RecipeDetailFragment();
                RecipeDetailFragment recipeDetailFragment = RecipeDetailFragment.newInstance(selectedRecipe);
                transaction.replace(R.id.sample_content_fragment, recipeDetailFragment, RECIPE_DETAIL_FRAGMENT_TAG);
                transaction.commit();
                currentFragmentInSampleContent = RECIPE_DETAIL_FRAGMENT;
            }else{
                FragmentTransaction transaction1 = this.getSupportFragmentManager().beginTransaction();
                new StepDetailFragment();
                StepDetailFragment stepDetailFragment = StepDetailFragment.newInstance(selectedRecipe, 0);
                transaction1.replace(R.id.sample_content_fragment, stepDetailFragment, STEP_DETAIL_FRAGMENT_TAG);
                //transaction1.addToBackStack(null);
                transaction1.commit();
                currentFragmentInSampleContent = STEP_DETAIL_FRAGMENT;

                FragmentTransaction transaction2 = this.getSupportFragmentManager().beginTransaction();
                new RecipeDetailFragment();
                RecipeDetailFragment recipeDetailFragment = RecipeDetailFragment.newInstance(selectedRecipe);
                transaction2.replace(R.id.second_content_fragment, recipeDetailFragment, RECIPE_DETAIL_FRAGMENT_TAG);
                transaction2.commit();
            }

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }else{

            selectedRecipe = savedInstanceState.getParcelable(SELECTED_RECIPE);
            currentFragmentInSampleContent = savedInstanceState.getString("currentFragmentInOnePane");

            if(!mTwoPane){
                StepDetailFragment stepDetailFragment = (StepDetailFragment) getSupportFragmentManager()
                        .findFragmentByTag(STEP_DETAIL_FRAGMENT_TAG);
            }else{

                StepDetailFragment stepDetailFragment = (StepDetailFragment) getSupportFragmentManager()
                            .findFragmentByTag(STEP_DETAIL_FRAGMENT_TAG);
                if(stepDetailFragment==null){
                    FragmentTransaction transaction1 = this.getSupportFragmentManager().beginTransaction();
                    new StepDetailFragment();
                    StepDetailFragment stepDetailFragment1 = StepDetailFragment.newInstance(selectedRecipe, 0);
                    transaction1.replace(R.id.sample_content_fragment, stepDetailFragment1, STEP_DETAIL_FRAGMENT_TAG);
                    transaction1.commit();
                    currentFragmentInSampleContent = STEP_DETAIL_FRAGMENT;
                }

                FragmentTransaction transaction2 = this.getSupportFragmentManager().beginTransaction();
                new RecipeDetailFragment();
                RecipeDetailFragment recipeDetailFragment = RecipeDetailFragment.newInstance(selectedRecipe);
                transaction2.replace(R.id.second_content_fragment, recipeDetailFragment, RECIPE_DETAIL_FRAGMENT_TAG);
                transaction2.commit();
            }
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
            transaction1.replace(R.id.sample_content_fragment, stepDetailFragment, STEP_DETAIL_FRAGMENT_TAG);
            transaction1.addToBackStack(null);
            transaction1.commit();

        }else {
            FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
            new StepDetailFragment();
            StepDetailFragment stepDetailFragment = StepDetailFragment.newInstance(selectedRecipe, stepPosition);
            transaction.replace(R.id.sample_content_fragment, stepDetailFragment, STEP_DETAIL_FRAGMENT_TAG);
            transaction.addToBackStack(null);
            transaction.commit();
            currentFragmentInSampleContent = STEP_DETAIL_FRAGMENT;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SELECTED_RECIPE, selectedRecipe);
        outState.putString("currentFragmentInOnePane", currentFragmentInSampleContent);
    }
}
