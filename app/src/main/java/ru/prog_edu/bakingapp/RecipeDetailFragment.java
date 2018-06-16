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

    @BindView(R.id.ingredients_recycler)RecyclerView ingredientsRecyclerView;
    @BindView(R.id.steps_recycler)RecyclerView stepsRecyclerView;
    private Unbinder unbinder;

    private List<Step> stepsList = null;
    private List<Ingredient> ingredientsList = null;

    OnStepClickListener stepCallback;

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
        RecyclerView.LayoutManager ingredientsLayoutManager = new LinearLayoutManager(getContext());
        ingredientsRecyclerView.setLayoutManager(ingredientsLayoutManager);

        RecyclerView.LayoutManager stepsLayoutManager = new LinearLayoutManager(getContext());
        stepsRecyclerView.setLayoutManager(stepsLayoutManager);

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
}