package com.clearlee.lockscreenmusiccontrol.util;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaControllerCompat;

import com.clearlee.lockscreenmusiccontrol.App;
import com.clearlee.lockscreenmusiccontrol.bean.LocalMusicInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Clearlee on 2017/12/26 0026.
 */

public class MusicUtil {

    private List<LocalMusicInfo> localMusicList = new ArrayList<>();
    private MediaControllerCompat mMediaController;
    private Uri contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//Uri，指向external的database
    private LocalMusicInfo curPlayMusicInfo;

    //projection：选择的列; where：过滤条件; sortOrder：排序。
    private String[] projection = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE
    };

    private static MusicUtil musicUtil;

    public static MusicUtil getInstance() {
        if (musicUtil == null) {
            synchronized (MusicUtil.class) {
                if (musicUtil == null) {
                    musicUtil = new MusicUtil();
                }
            }
        }
        return musicUtil;
    }

    public List<LocalMusicInfo> getPlayMusicList() {
        return localMusicList;
    }

    public HashSet<LocalMusicInfo> getLocalMusicData() {
        HashSet<LocalMusicInfo> result = new HashSet<>();
        try {
            Cursor cursor = App.getApp().getContentResolver().query(contentUri, projection, null, null, MediaStore.Audio.Media.DATA);
            if (cursor == null) {
                LogTool.s("Music Loader cursor == null.");
            } else if (!cursor.moveToFirst()) {
                LogTool.s("Music Loader cursor.moveToFirst() returns false.");
            } else {
                int displayNameCol = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
                int titleCol = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int albumCol = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                int idCol = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int durationCol = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                int sizeCol = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE);
                int artistCol = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int urlCol = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);

                do {
                    String displayName = cursor.getString(displayNameCol);

                    String album = cursor.getString(albumCol);

                    String title = cursor.getString(titleCol);
                    long id = cursor.getLong(idCol);
                    int duration = cursor.getInt(durationCol);
                    long size = cursor.getLong(sizeCol);
                    String artist = cursor.getString(artistCol);
                    String url = cursor.getString(urlCol);

                    if (url.replace(title, "").toLowerCase().contains("record"))
                        continue;//如果这个音频文件的目录包含这个内容就被指认为录音文件过滤
                    if (displayName.contains("录音") || title.contains("录音"))
                        continue;//如果歌名含有录音两个字就当录音文件过滤
                    if (displayName.contains("record") || title.contains("record"))
                        continue;//如果歌名含有record就当录音文件过滤
                    if (duration < 70 * 1000) continue;//过滤时长小于70秒的

                    if (displayName.endsWith(".mp3") || displayName.endsWith(".wav") || displayName.endsWith(".m4a")) {
                        LocalMusicInfo musicInfo = new LocalMusicInfo();
                        musicInfo.setId(id + "");
                        musicInfo.setName(title);
                        musicInfo.setDuration(duration);
                        musicInfo.setSize(size);
                        musicInfo.setAuthor(artist);
                        musicInfo.setAlbum(album);
                        musicInfo.setSource(url);

                        if (!result.contains(musicInfo)) {
                            result.add(musicInfo);
                        }

                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            LogTool.ex(e);
        }
        return result;
    }

    public void setController(MediaControllerCompat controller) {
        this.mMediaController = controller;
    }

    public MediaControllerCompat getMediaContorller() {
        return mMediaController;
    }


    public void setCurrPlayMusicInfo(LocalMusicInfo musicInfo) {
        this.curPlayMusicInfo = musicInfo;
    }

    public LocalMusicInfo getCurrPlayMusicInfo() {
        return curPlayMusicInfo;
    }


    public LocalMusicInfo getPreMusicInfo() {
        int curIndex = getPlayMusicList().indexOf(getCurrPlayMusicInfo());
        int index = curIndex - 1;
        if (index < 0) {
            index = getPlayMusicList().size() - 1;
        }
        return getPlayMusicList().get(index);
    }

    public LocalMusicInfo getNextMusicInfo() {
        int curIndex = getPlayMusicList().indexOf(getCurrPlayMusicInfo());
        int index = curIndex + 1;
        if (index > getPlayMusicList().size() - 1) {
            index = 0;
        }
        return getPlayMusicList().get(index);
    }

}
