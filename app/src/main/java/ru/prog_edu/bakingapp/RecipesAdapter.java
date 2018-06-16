package ru.prog_edu.bakingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.prog_edu.bakingapp.model.recipes.Recipe;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder>{

    private final List<Recipe> recipeList;

    public RecipesAdapter(List<Recipe> recipeList, OnSelectedItemListener mClickHandler) {
        this.mClickHandler = mClickHandler;
        this.recipeList=recipeList;
    }

    public interface OnSelectedItemListener{
        void onListItemClick(int selectedRecipe);
    }

    private final OnSelectedItemListener mClickHandler;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context;
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.recipe_item, parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String recipeName = recipeList.get(position).getName();
        holder.textViewName.setText(recipeName);
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.tv_recipe_name)TextView textViewName;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View view) {
            int selectedPosition = getAdapterPosition();
            mClickHandler.onListItemClick(selectedPosition);
        }
    }
}
