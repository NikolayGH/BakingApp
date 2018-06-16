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
import ru.prog_edu.bakingapp.model.recipes.Ingredient;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientsHolder> {
    private final List<Ingredient> ingredientList;

    public IngredientsAdapter(List<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;
    }

    @NonNull
    @Override
    public IngredientsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context;
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.ingredient_item, parent, false);

        return new IngredientsHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsHolder holder, int position) {
        holder.quantityIngredientTextView.setText(String.valueOf(ingredientList.get(position).getQuantity()));
        holder.measureIngredientTextView.setText(ingredientList.get(position).getMeasure());
        holder.nameIngredientTextView.setText(ingredientList.get(position).getIngredient());
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }


    public class IngredientsHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_quantity)TextView quantityIngredientTextView;
        @BindView(R.id.tv_measure)TextView measureIngredientTextView;
        @BindView(R.id.tv_ingredient_name)TextView nameIngredientTextView;

        IngredientsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
