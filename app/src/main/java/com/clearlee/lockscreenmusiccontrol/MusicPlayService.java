package com.clearlee.lockscreenmusiccontrol;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;

import com.clearlee.lockscreenmusiccontrol.constant.MusicConstants;
import com.clearlee.lockscreenmusiccontrol.mediasession.MediaSessionManager;
import com.clearlee.lockscreenmusiccontrol.util.LogTool;
import com.clearlee.lockscreenmusiccontrol.util.MusicUtil;

public class MusicPlayService extends Service {

    public static MediaPlayer mMediaPlyer;
    public static MusicPlayService musicPlayService;

    public int curPlayState = PLAT_STATE_NORAML; //播放状态
    public static final int PLAT_STATE_NORAML = 0;
    public static final int PLAY_STATE_PLAYING = 1;
    public static final int PLAY_STATE_PAUSED = 2;

    private boolean resetMusic;

    private Handler refreshTimeHandler = new Handler();

    private MediaSessionManager mediaSessionManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        musicPlayService = this;
        mediaSessionManager = new MediaSessionManager(this);
        initMediaPlayer();
    }


    private void initMediaPlayer() {
        LogTool.s("initMediaPlayer");
        resetMusic = false;
        curPlayState = PLAT_STATE_NORAML;
        mMediaPlyer = new MediaPlayer();
        mMediaPlyer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlyer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                LogTool.s("onCompletion");
                handleNextPlay();
            }
        });
        mMediaPlyer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                realStartPlay();
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void startPlay() {
        LogTool.s("startPlay");
        try {
            if (mMediaPlyer != null) {
                mMediaPlyer.setDataSource(MusicUtil.getInstance().getCurrPlayMusicInfo().getSource());
                mMediaPlyer.prepareAsync();
            }
        } catch (Exception e) {
            LogTool.ex(e);
        }
    }

    private void handleIntent(Intent intent) {

        if (intent == null || intent.getAction() == null) {
            return;
        }

        switch (intent.getAction()) {
            case MusicConstants.MUSIC_ACTICON_START_PLAY:
                handleStartPlay();
                break;
            case MusicConstants.MUSIC_ACTICON_PAUSE_PLAY:
                handlePausePlay();
                break;
            case MusicConstants.MUSIC_ACTICON_CONTINUE_PLAY:
                handleStartPlay();
                break;
            case MusicConstants.MUSIC_ACTICON_RESET_START_PLAY:
                handleResetAndStartPlay();
                break;
            case MusicConstants.MUSIC_ACTICON_PLAY_PRE:
                handlePrePlay();
                break;
            case MusicConstants.MUSIC_ACTICON_PLAY_NEXT:
                handleNextPlay();
                break;
        }
    }

    public void handleNextPlay() {
        LogTool.s("handleNextPlay");
        try {
            if (App.getApp().activity != null) {
                App.getApp().activity.updateNextMusic();
            }
            resetMusic = true;
            curPlayState = PLAT_STATE_NORAML;
            handleStartPlay();
        } catch (Exception e) {
            LogTool.ex(e);
        }
    }

    public void handlePrePlay() {
        LogTool.s("handlePrePlay");
        try {
            if (App.getApp().activity != null) {
                App.getApp().activity.updatePreMusic();
            }
            resetMusic = true;
            curPlayState = PLAT_STATE_NORAML;
            handleStartPlay();
        } catch (Exception e) {
            LogTool.ex(e);
        }
    }

    public void handleResetAndStartPlay() {
        LogTool.s("handleResetAndStartPlay");
        try {
            resetMusic = true;
            curPlayState = PLAT_STATE_NORAML;
            handleStartPlay();
        } catch (Exception e) {
            LogTool.ex(e);
        }
    }

    private void resetStartPlay() {
        LogTool.s("resetStartPlay");
        try {
            if (mMediaPlyer != null) {
                mMediaPlyer.reset();
                initMediaPlayer();
                startPlay();
            }
        } catch (Exception e) {
            LogTool.ex(e);
        }
    }


    private void realStartPlay() {
        LogTool.s("realStartPlay");
        try {
            mMediaPlyer.start();
            mMediaPlyer.setVolume(1, 1);
            curPlayState = PLAY_STATE_PLAYING;
            refreshTimeTask();
            mediaSessionManager.updatePlaybackState(curPlayState);
            mediaSessionManager.updateLocMsg();
        } catch (Exception e) {
            LogTool.ex(e);
        }
    }


    public void handleStartPlay() {
        LogTool.s("handleStartPlay");
        try {
            App.getApp().activity.updateImage();
            switch (curPlayState) {
                case PLAT_STATE_NORAML: {
                    if (resetMusic) {
                        resetStartPlay();
                    } else {
                        startPlay();
                    }
                    resetMusic = false;
                }
                break;
                case PLAY_STATE_PAUSED:
                    continuePlay();
                    break;
            }
        } catch (Exception e) {
            LogTool.ex(e);
        }
    }


    public void handlePausePlay() {
        LogTool.s("handlePausePlay");
        try {
            if (curPlayState == PLAY_STATE_PLAYING && mMediaPlyer != null) {
                App.getApp().activity.updateImage();
                mMediaPlyer.pause();
                curPlayState = PLAY_STATE_PAUSED;
                mediaSessionManager.updatePlaybackState(curPlayState);
            }
        } catch (Exception e) {
            LogTool.ex(e);
        }
    }

    private void continuePlay() {
        LogTool.s("continuePlay");
        try {
            if (curPlayState == PLAY_STATE_PAUSED && mMediaPlyer != null) {
                realStartPlay();
            }
        } catch (Exception e) {
            LogTool.ex(e);
        }
    }

    private void refreshTimeTask() {
        LogTool.s("refreshTimeTask");
        try {
            refreshTimeHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (curPlayState == PLAY_STATE_PLAYING) {
                        int position = mMediaPlyer.getCurrentPosition();
                        if (App.getApp().activity != null) {
                            App.getApp().activity.refreshTime(position);
                        }
                        refreshTimeHandler.postDelayed(this, 1000);
                    }
                }
            }, 1000);
        } catch (Exception e) {
            LogTool.ex(e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlyer != null) {
            mMediaPlyer.release();
            mMediaPlyer = null;
        }
        refreshTimeHandler = null;
        mediaSessionManager.release();
    }
}
