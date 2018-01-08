package com.clearlee.lockscreenmusiccontrol.mediasession;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.clearlee.lockscreenmusiccontrol.MusicPlayService;
import com.clearlee.lockscreenmusiccontrol.util.LogTool;
import com.clearlee.lockscreenmusiccontrol.util.MusicUtil;

/**
 * Created by Clearlee on 2018/1/4 0004.
 */

public class MediaSessionManager {

    private static final String MY_MEDIA_ROOT_ID = "MediaSessionManager";

    private MusicPlayService musicPlayService;
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder stateBuilder;

    public MediaSessionManager(MusicPlayService service) {
        this.musicPlayService = service;
        initSession();
    }

    public void initSession() {
        try {
            mMediaSession = new MediaSessionCompat(musicPlayService, MY_MEDIA_ROOT_ID);
            mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
            stateBuilder = new PlaybackStateCompat.Builder()
                    .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE
                            | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
            mMediaSession.setPlaybackState(stateBuilder.build());
            mMediaSession.setCallback(sessionCb);
            mMediaSession.setActive(true);
        } catch (Exception e) {
            LogTool.ex(e);
        }
    }

    public void updatePlaybackState(int currentState) {
        int state = (currentState == MusicPlayService.PLAY_STATE_PAUSED) ? PlaybackStateCompat.STATE_PAUSED : PlaybackStateCompat.STATE_PLAYING;
        stateBuilder.setState(state, musicPlayService.mMediaPlyer.getCurrentPosition(), 1.0f);
        mMediaSession.setPlaybackState(stateBuilder.build());
    }

    public void updateLocMsg() {
        try {
            //同步歌曲信息
            MediaMetadataCompat.Builder md = new MediaMetadataCompat.Builder();
            md.putString(MediaMetadataCompat.METADATA_KEY_TITLE, MusicUtil.getInstance().getCurrPlayMusicInfo().getName());
            md.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, MusicUtil.getInstance().getCurrPlayMusicInfo().getAuthor());
            md.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, MusicUtil.getInstance().getCurrPlayMusicInfo().getAlbum());
            md.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, MusicUtil.getInstance().getCurrPlayMusicInfo().getDuration());
            mMediaSession.setMetadata(md.build());
        } catch (Exception e) {
            LogTool.ex(e);
        }

    }

    private MediaSessionCompat.Callback sessionCb = new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
            super.onPlay();
            musicPlayService.handleStartPlay();
        }

        @Override
        public void onPause() {
            super.onPause();
            musicPlayService.handlePausePlay();
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            musicPlayService.handleNextPlay();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            musicPlayService.handlePrePlay();
        }

    };

    public void release() {
        mMediaSession.setCallback(null);
        mMediaSession.setActive(false);
        mMediaSession.release();
    }

}
