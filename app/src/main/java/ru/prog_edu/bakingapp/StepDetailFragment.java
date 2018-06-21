package ru.prog_edu.bakingapp;

import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import java.util.Objects;

import ru.prog_edu.bakingapp.model.recipes.Recipe;
import ru.prog_edu.bakingapp.utilities.ExoPlayerVideoHandler;
import ru.prog_edu.bakingapp.utilities.NetworkState;

public class StepDetailFragment extends Fragment implements ExoPlayer.EventListener{

    private static final String BUNDLE_RECIPE_STEP = "bundle_recipe_step";
    private static final String BUNDLE_STEP = "bundle_step";
    private Recipe recipe = null;
    private int stepId;
    private SimpleExoPlayer simpleExoPlayer;
    private SimpleExoPlayerView simpleExoPlayerView;
    private TextView tvStepInstruction;
    private boolean playWhenReady=true;
    private long position=0;

    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;

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
        setRetainInstance(true);
        if (getArguments() != null && getArguments().containsKey(BUNDLE_RECIPE_STEP)){
            this.recipe = getArguments().getParcelable(BUNDLE_RECIPE_STEP);
            this.stepId = getArguments().getInt(BUNDLE_STEP);
        } else {
            throw new IllegalArgumentException("Must be created through newInstance(...)");
        }
        NetworkState networkState = new NetworkState(getActivity());
        boolean isOnline = networkState.isOnline();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //Here I did not use Butter Knife because there is selecting implementation of views
        //Here I use ExoPlayerVideoHandler from example: https://medium.com/tall-programmer/fullscreen-functionality-with-android-exoplayer-5fddad45509f
        // It was made to avoid black screen by rotation phone
        if(savedInstanceState!=null){
            position = savedInstanceState.getLong("position");
            playWhenReady = savedInstanceState.getBoolean("playWhenReady");
        }
        //проверить на устройстве
        if(getResources().getBoolean(R.bool.land_only)){
            Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail_step, container, false);
        simpleExoPlayerView = rootView.findViewById(R.id.playerView);

        simpleExoPlayerView.requestFocus();

        tvStepInstruction = rootView.findViewById(R.id.tv_recipe_step_instruction);
        tvStepInstruction.setText(recipe.getSteps().get(stepId).getDescription());

        ActionBar toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(videoIsFullscreen()) {
            if (toolbar != null) {
                toolbar.hide();
            }
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
                    if (simpleExoPlayer != null) {
                        simpleExoPlayer.stop();
                        simpleExoPlayer.release();
                        simpleExoPlayerView.setPlayer(null);
                    }
                    ExoPlayerVideoHandler.getInstance()
                            .prepareExoPlayerForUri(getContext(),
                                    Uri.parse(recipe.getSteps().get(stepId).getVideoURL()), simpleExoPlayerView);
                    ExoPlayerVideoHandler.getInstance().goToForeground();
                    //releasePlayer();
                    showStep(stepId, recipe);
                }
            }
        });
        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stepId>0){
                    stepId--;
                    if (simpleExoPlayer != null) {
                        simpleExoPlayer.stop();
                        simpleExoPlayer.release();
                        simpleExoPlayerView.setPlayer(null);
                    }
                    ExoPlayerVideoHandler.getInstance()
                            .prepareExoPlayerForUri(getContext(),
                                    Uri.parse(recipe.getSteps().get(stepId).getVideoURL()), simpleExoPlayerView);
                    ExoPlayerVideoHandler.getInstance().goToForeground();
                    showStep(stepId, recipe);
                }
            }
        });
        return rootView;
    }

    private boolean videoIsFullscreen() {
        return tvStepInstruction.getVisibility() != View.VISIBLE;
    }

    private void showStep(int changedStepID, Recipe recipe){
        ExoPlayerVideoHandler.getInstance().goToForeground();
        tvStepInstruction.setText(recipe.getSteps().get(changedStepID).getDescription());
    }


    @Override
    public void onPause(){
        super.onPause();
        ExoPlayerVideoHandler.getInstance().goToBackground();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(simpleExoPlayerView != null){
            ExoPlayerVideoHandler.getInstance()
                    .prepareExoPlayerForUri(getContext(),
                    Uri.parse(recipe.getSteps().get(stepId).getVideoURL()), simpleExoPlayerView);
            ExoPlayerVideoHandler.getInstance().goToForeground();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        ExoPlayerVideoHandler.getInstance().releaseVideoPlayer();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("playWhenReady", playWhenReady);
        outState.putLong("position", position);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, simpleExoPlayer.getCurrentPosition(), 1f);
        } else if (playbackState == ExoPlayer.STATE_READY) {
            stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, simpleExoPlayer.getCurrentPosition(), 1f);
        }
        mediaSession.setPlaybackState(stateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }
}
