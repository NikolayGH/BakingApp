package ru.prog_edu.bakingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.provider.MediaStore.Video.Thumbnails;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
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
        holder.imageStep.setVisibility(View.GONE);

        String stepImageUrl = stepsList.get(position).getThumbnailURL();
        if (!stepImageUrl.equals("")) {
            holder.imageStep.setVisibility(View.VISIBLE);
            if (stepImageUrl.contains(".mp4")) {
                Bitmap bmThumbnail = null;
                try {
                    bmThumbnail = retriveVideoFrameFromVideo(stepImageUrl);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                holder.imageStep.setImageBitmap(bmThumbnail);
            }else{
                Picasso.get()
                        .load(stepImageUrl)
                        .into(holder.imageStep);

            }
        }
        //Here it may add one more block of code to get and create thumbnail from "videoURL" and set it here if "thumbnailURL" is empty.
        //But it need to do on not main thread.
//                else{
//            String videoUrl = stepsList.get(position).getVideoURL();
//            holder.imageStep.setVisibility(View.VISIBLE);
//            Bitmap bmThumbnail = null;
//            try {
//                bmThumbnail = retriveVideoFrameFromVideo(videoUrl);
//            } catch (Throwable throwable) {
//                throwable.printStackTrace();
//            }
//            holder.imageStep.setImageBitmap(bmThumbnail);
//        }
    }

    @Override
    public int getItemCount() {
        return stepsList.size();
    }

    private final OnSelectedStepListener mClickStepHandler;

    public class StepsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.imageStep)ImageView imageStep;
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



    private static Bitmap retriveVideoFrameFromVideo(String videoPath)throws Throwable
    {
        Bitmap bitmap;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try
        {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)"+ e.getMessage());
        }
        finally
        {
            if (mediaMetadataRetriever != null)
            {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }
}
