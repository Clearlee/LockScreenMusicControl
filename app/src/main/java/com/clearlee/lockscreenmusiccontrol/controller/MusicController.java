package com.clearlee.lockscreenmusiccontrol.controller;

import android.content.Intent;

import com.clearlee.lockscreenmusiccontrol.App;
import com.clearlee.lockscreenmusiccontrol.MusicPlayService;
import com.clearlee.lockscreenmusiccontrol.constant.MusicConstants;

/**
 * Created by Clearlee on 2017/12/27 0027.
 */

public class MusicController {

    public static void initMusicService() {
        sendCommandToService(null, null, null);
    }

    public static void startPlay(String path) {
        sendCommandToService(MusicConstants.MUSIC_ACTICON_START_PLAY, MusicConstants.PARAM_MUSIC_PATH, path);
    }

    public static void pausePlay() {
        sendCommandToService(MusicConstants.MUSIC_ACTICON_PAUSE_PLAY, null, null);
    }

    public static void continuePlay() {
        sendCommandToService(MusicConstants.MUSIC_ACTICON_CONTINUE_PLAY, null, null);
    }

    public static void resetStartPlay(String path) {
        sendCommandToService(MusicConstants.MUSIC_ACTICON_RESET_START_PLAY, MusicConstants.PARAM_MUSIC_PATH, path);
    }

    public static void playNext() {
        sendCommandToService(MusicConstants.MUSIC_ACTICON_PLAY_NEXT, null, null);
    }

    public static void playPre() {
        sendCommandToService(MusicConstants.MUSIC_ACTICON_PLAY_PRE, null, null);
    }

    //发送指令到音乐服务
    private static void sendCommandToService(String action, String param, String data) {
        Intent intent = new Intent();
        intent.setClass(App.getApp(), MusicPlayService.class);
        intent.setAction(action);
        if (param != null) {
            intent.putExtra(param, data);
        }
        App.getApp().startService(intent);
    }


}
