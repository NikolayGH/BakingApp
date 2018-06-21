package ru.prog_edu.bakingapp.utilities;

import android.content.Context;
import android.net.Uri;
import android.view.SurfaceView;

import com.google.android.exoplayer2.DefaultLoadControl;
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

public class ExoPlayerVideoHandler {
    private static ExoPlayerVideoHandler instance;

    public static ExoPlayerVideoHandler getInstance(){
        if(instance == null){
            instance = new ExoPlayerVideoHandler();
        }
        return instance;
    }
    private SimpleExoPlayer player;
    private Uri playerUri;
    private boolean isPlayerPlaying;
    private long currentPosition;

    private ExoPlayerVideoHandler(){}

    public void prepareExoPlayerForUri(Context context, Uri uri,
                                       SimpleExoPlayerView exoPlayerView){

        if (uri != null && !uri.equals(playerUri)) {
            currentPosition = 0;
        }

        if(context != null && uri != null && exoPlayerView != null){
            if(!uri.equals(playerUri) || player == null){
                if (player != null) {
                    player.release();
                    player = null;
                }

                TrackSelector trackSelector = new DefaultTrackSelector();
                LoadControl loadControl = new DefaultLoadControl();
                player =  ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl);

                playerUri = uri;
                isPlayerPlaying = true;

                String userAgent = Util.getUserAgent(context, "BakingApp");
                MediaSource source = new ExtractorMediaSource(
                        uri,
                        new DefaultDataSourceFactory(context, userAgent),
                        new DefaultExtractorsFactory(),
                        null,
                        null
                );

                player.prepare(source);
            }
            player.clearVideoSurface();
            player.setVideoSurfaceView(
                    (SurfaceView)exoPlayerView.getVideoSurfaceView());
            player.seekTo(currentPosition);
            exoPlayerView.setPlayer(player);
        }
    }

    public void releaseVideoPlayer(){
        if(player != null)
        {
            currentPosition = player.getCurrentPosition();
            player.release();
        }
        player = null;
    }

    public void goToBackground(){
        if(player != null){
            currentPosition = player.getCurrentPosition();
            isPlayerPlaying = player.getPlayWhenReady();
            player.setPlayWhenReady(false);
        }
    }

    public void goToForeground(){
        if(player != null){
            player.setPlayWhenReady(isPlayerPlaying);
        }
    }
}
