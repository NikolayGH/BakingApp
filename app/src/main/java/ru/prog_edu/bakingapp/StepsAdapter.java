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
import ru.prog_edu.bakingapp.model.recipes.Step;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepsViewHolder> {
    private final List<Step> stepsList;

    public StepsAdapter(List<Step> stepsList, OnSelectedStepListener mClickStepHandler) {
        this.mClickStepHandler = mClickStepHandler;
        this.stepsList = stepsList;
    }

    public interface OnSelectedStepListener{
        void onStepItemClick(int selectedStep);
    }

    @NonNull
    @Override
    public StepsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context;
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.step_item, parent, false);
        return new StepsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StepsViewHolder holder, int position) {
        String stepShortDescription = stepsList.get(position).getShortDescription();
        int stepSerialNumber = (stepsList.get(position).getId());
        holder.textViewDescription.setText(stepShortDescription);
        holder.textViewStepSerialNumber.setText(String.valueOf(stepSerialNumber));
    }

    @Override
    public int getItemCount() {
        return stepsList.size();
    }



    private final OnSelectedStepListener mClickStepHandler;

    public class StepsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_step_short_description) TextView textViewDescription;
        @BindView(R.id.tv_step_serial_number) TextView textViewStepSerialNumber;

        StepsViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View view) {
            int selectedPosition = getAdapterPosition();
            mClickStepHandler.onStepItemClick(selectedPosition);
        }
    }
}
