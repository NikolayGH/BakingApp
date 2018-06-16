package ru.prog_edu.bakingapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.prog_edu.bakingapp.model.recipes.Recipe;

public class RecipeCardsFragment extends Fragment implements RecipesAdapter.OnSelectedItemListener {

    private static final String BUNDLE_CONTENT = "bundle_content";
    private static final String BUNDLE_SCREEN_SIZE = "bundle_screen_size";

    @BindView(R.id.catalog_recycler) RecyclerView mRecyclerView;
    private Unbinder unbinder;
    private List<Recipe> recipeList = null;
    private OnRecipeClickListener mCallback;
    private boolean mTwoPane;

    public interface OnRecipeClickListener{
        void onRecipeSelected(int position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnRecipeClickListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + "must implement onRecipeClickListener");
        }
    }

    public static RecipeCardsFragment newInstance(final List<Recipe> recipeList, boolean twoPane) {
        final RecipeCardsFragment fragment = new RecipeCardsFragment();
        final Bundle arguments = new Bundle();
        arguments.putParcelableArrayList(BUNDLE_CONTENT, (ArrayList<? extends Parcelable>) recipeList);
        arguments.putBoolean(BUNDLE_SCREEN_SIZE, twoPane);
        fragment.setArguments(arguments);
        return fragment;
    }

    public RecipeCardsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(BUNDLE_CONTENT)) {
            this.recipeList = getArguments().getParcelableArrayList(BUNDLE_CONTENT);
            this.mTwoPane = getArguments().getBoolean(BUNDLE_SCREEN_SIZE);
        } else {
            throw new IllegalArgumentException("Must be created through newInstance(...)");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_card_recipes, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        int numberOfColumns;
        if(mTwoPane){
            numberOfColumns =2;
        }else{
            numberOfColumns =1;
        }

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));
        RecyclerView.ItemDecoration itemDecoration =
                new ru.prog_edu.bakingapp.DividerItemDecoration(30);
        mRecyclerView.addItemDecoration(itemDecoration);

        RecipesAdapter mRecipesAdapter = new RecipesAdapter(recipeList, this);
        mRecyclerView.setAdapter(mRecipesAdapter);

        mRecipesAdapter.notifyDataSetChanged();
        ButterKnife.bind(rootView);

        return rootView;
    }

    @Override
    public void onListItemClick(int selectedRecipe) {
        mCallback.onRecipeSelected(selectedRecipe);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
