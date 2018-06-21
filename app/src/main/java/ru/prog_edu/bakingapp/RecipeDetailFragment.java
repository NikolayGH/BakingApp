package ru.prog_edu.bakingapp;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.prog_edu.bakingapp.model.recipes.Ingredient;
import ru.prog_edu.bakingapp.model.recipes.Recipe;
import ru.prog_edu.bakingapp.model.recipes.Step;

public class RecipeDetailFragment extends Fragment implements StepsAdapter.OnSelectedStepListener {

    private static final String BUNDLE_RECIPE_CONTENT = "bundle_recipe_content";
    private static final String COUNTER_INGREDIENT_POSITION = "counterIngredientPosition";
    private static final String COUNTER_STEP_POSITION = "counterStepPosition";

    @BindView(R.id.ingredients_recycler)RecyclerView ingredientsRecyclerView;
    @BindView(R.id.steps_recycler)RecyclerView stepsRecyclerView;
    private Unbinder unbinder;

    private List<Step> stepsList = null;
    private List<Ingredient> ingredientsList = null;

    private OnStepClickListener stepCallback;

    private int currentIngredientVisiblePosition;
    private int currentStepVisiblePosition;
    private RecyclerView.LayoutManager ingredientsLayoutManager;
    private RecyclerView.LayoutManager stepsLayoutManager;
    private int temporaryVariableIngredientPosition;
    private int temporaryVariableStepPosition;

    public interface OnStepClickListener{
        void onStepSelected(int stepPosition);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            stepCallback = (OnStepClickListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + "must implement onRecipeClickListener");
        }
    }

    public static RecipeDetailFragment newInstance(final Recipe recipe){
        final RecipeDetailFragment fragment = new RecipeDetailFragment();
        final Bundle arguments = new Bundle();
        arguments.putParcelable(BUNDLE_RECIPE_CONTENT, recipe);
        fragment.setArguments(arguments);
        return fragment;
    }

    public RecipeDetailFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (savedInstanceState == null) {
            currentIngredientVisiblePosition = 0;
            currentStepVisiblePosition = 0;
        } else {
            currentIngredientVisiblePosition = savedInstanceState.getInt(COUNTER_INGREDIENT_POSITION, 0);
            currentStepVisiblePosition = savedInstanceState.getInt(COUNTER_STEP_POSITION, 0);
        }

        Recipe recipe;
        if (getArguments() != null && getArguments().containsKey(BUNDLE_RECIPE_CONTENT)) {
            recipe = getArguments().getParcelable(BUNDLE_RECIPE_CONTENT);
        } else {
            throw new IllegalArgumentException("Must be created through newInstance(...)");
        }

        if (recipe != null) {
            stepsList = recipe.getSteps();
        }
        if (recipe != null) {
            ingredientsList = recipe.getIngredients();
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail_recipe, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        ingredientsLayoutManager = new LinearLayoutManager(getContext());
        ingredientsLayoutManager.scrollToPosition(currentIngredientVisiblePosition);
        ingredientsRecyclerView.setLayoutManager(ingredientsLayoutManager);
        ingredientsRecyclerView.getLayoutManager().scrollToPosition(currentIngredientVisiblePosition);

        stepsLayoutManager = new LinearLayoutManager(getContext());

        stepsRecyclerView.setLayoutManager(stepsLayoutManager);
        stepsRecyclerView.getLayoutManager().scrollToPosition(currentStepVisiblePosition);

        RecyclerView.ItemDecoration itemDecoration =
                new ru.prog_edu.bakingapp.DividerItemDecoration(5);

        ingredientsRecyclerView.addItemDecoration(itemDecoration);
        stepsRecyclerView.addItemDecoration(itemDecoration);

        StepsAdapter stepsAdapter = new StepsAdapter(stepsList, this);
        stepsRecyclerView.setAdapter(stepsAdapter);
        stepsAdapter.notifyDataSetChanged();

        IngredientsAdapter ingredientsAdapter = new IngredientsAdapter(ingredientsList);
        ingredientsRecyclerView.setAdapter(ingredientsAdapter);
        ingredientsAdapter.notifyDataSetChanged();
        return rootView;
    }

    @Override
    public void onStepItemClick(int selectedStep) {
        stepCallback.onStepSelected(selectedStep);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putInt("counter", mCounter);

        currentIngredientVisiblePosition = temporaryVariableIngredientPosition;
        outState.putInt(COUNTER_INGREDIENT_POSITION, currentIngredientVisiblePosition);

        currentStepVisiblePosition = temporaryVariableStepPosition;
        outState.putInt(COUNTER_STEP_POSITION, currentStepVisiblePosition);
    }

    @Override
    public void onPause() {
        super.onPause();

        temporaryVariableIngredientPosition = ((LinearLayoutManager)ingredientsRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();


        temporaryVariableStepPosition = ((LinearLayoutManager)stepsRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();


    }
}