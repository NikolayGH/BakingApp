package ru.prog_edu.bakingapp;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import java.util.Objects;
import ru.prog_edu.bakingapp.model.recipes.Recipe;
import ru.prog_edu.bakingapp.utilities.NetworkState;

public class StepDetailFragment extends Fragment {

    private static final String BUNDLE_RECIPE_STEP = "bundle_recipe_step";
    private static final String BUNDLE_STEP = "bundle_step";
    private Recipe recipe = null;
    private int stepId;
    private SimpleExoPlayer simpleExoPlayer;
    private SimpleExoPlayerView simpleExoPlayerView;
    private TextView tvStepInstruction;
    private boolean isOnline;
    private boolean onlyExoPlayerScreen;

    public static StepDetailFragment newInstance(final Recipe recipe, int step){
        final StepDetailFragment fragment = new StepDetailFragment();
        final Bundle arguments = new Bundle();
        arguments.putParcelable(BUNDLE_RECIPE_STEP, recipe);
        arguments.putInt(BUNDLE_STEP,  step);
        fragment.setArguments(arguments);
        return fragment;
    }

    public StepDetailFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(BUNDLE_RECIPE_STEP)){
            this.recipe = getArguments().getParcelable(BUNDLE_RECIPE_STEP);
            this.stepId = getArguments().getInt(BUNDLE_STEP);
        } else {
            throw new IllegalArgumentException("Must be created through newInstance(...)");
        }
        NetworkState networkState = new NetworkState(getActivity());
        isOnline = networkState.isOnline();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //Here I did not use Butter Knife because there is selecting implementation of views
        View rootView = inflater.inflate(R.layout.fragment_detail_step, container, false);
        simpleExoPlayerView = rootView.findViewById(R.id.playerView);

        if (rootView.findViewById(R.id.tv_recipe_step_instruction) != null){
            onlyExoPlayerScreen=false;
            tvStepInstruction = rootView.findViewById(R.id.tv_recipe_step_instruction);
            tvStepInstruction.setText(recipe.getSteps().get(stepId).getDescription());
        }else{
            onlyExoPlayerScreen=true;
        }

        Button buttonNext = rootView.findViewById(R.id.next_step_button);
        Button buttonPrevious = rootView.findViewById(R.id.previous_step_button);

        simpleExoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource
                (getResources(), R.drawable.no_video_content));

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stepId<recipe.getSteps().size()-1) {
                    stepId++;
                    ExoPlayer player = simpleExoPlayerView.getPlayer();
                    if (player != null) {
                        player.stop();
                        player.release();
                        simpleExoPlayerView.setPlayer(null);
                    }

                    releasePlayer();
                    showStep(stepId, recipe);
                }
            }
        });
        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stepId>0){
                    stepId--;
                    ExoPlayer player = simpleExoPlayerView.getPlayer();
                    if (player != null) {
                        player.stop();
                        player.release();
                        simpleExoPlayerView.setPlayer(null);
                    }
                    releasePlayer();
                    showStep(stepId, recipe);
                }
            }
        });
        initializePlayer(Uri.parse(recipe.getSteps().get(stepId).getVideoURL()));
        return rootView;
    }

    private void initializePlayer(Uri mediaUri) {
        if(isOnline) {
            if (simpleExoPlayer == null) {
                TrackSelector trackSelector = new DefaultTrackSelector();
                LoadControl loadControl = new DefaultLoadControl();
                simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
                simpleExoPlayerView.setPlayer(simpleExoPlayer);
                String userAgent = Util.getUserAgent(getActivity(), "Baking App");
                MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                        Objects.requireNonNull(getActivity()), userAgent), new DefaultExtractorsFactory(), null, null);
                simpleExoPlayer.prepare(mediaSource);
                simpleExoPlayer.setPlayWhenReady(true);
            }
        }else{
            Toast.makeText(getActivity(), "No Internet connection", Toast.LENGTH_LONG).show();
        }
    }

    private void releasePlayer() {
        simpleExoPlayer.stop();
        simpleExoPlayer.release();
        simpleExoPlayer = null;
    }

    private void showStep(int changedStepID, Recipe recipe){
        initializePlayer(Uri.parse(recipe.getSteps().get(changedStepID).getVideoURL()));
        if(!onlyExoPlayerScreen){
            tvStepInstruction.setText(recipe.getSteps().get(changedStepID).getDescription());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ExoPlayer player = simpleExoPlayerView.getPlayer();
        if (player != null) {
            player.stop();
            player.release();
            simpleExoPlayerView.setPlayer(null);
        }
    }
}
