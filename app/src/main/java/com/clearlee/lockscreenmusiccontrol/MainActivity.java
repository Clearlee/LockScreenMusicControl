package com.clearlee.lockscreenmusiccontrol;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.clearlee.lockscreenmusiccontrol.adapter.MusicAdapter;
import com.clearlee.lockscreenmusiccontrol.bean.LocalMusicInfo;
import com.clearlee.lockscreenmusiccontrol.controller.MusicController;
import com.clearlee.lockscreenmusiccontrol.util.Common;
import com.clearlee.lockscreenmusiccontrol.util.LogTool;
import com.clearlee.lockscreenmusiccontrol.util.MusicUtil;

import java.util.List;

import static com.clearlee.lockscreenmusiccontrol.MusicPlayService.PLAT_STATE_NORAML;
import static com.clearlee.lockscreenmusiccontrol.MusicPlayService.PLAY_STATE_PAUSED;
import static com.clearlee.lockscreenmusiccontrol.MusicPlayService.PLAY_STATE_PLAYING;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivMusicPlay, ivPre, ivNext;
    private SeekBar sb_music;
    private TextView currTime, totalTime, emptyView;
    private ListView mListView;
    private Handler scanMusicHandler = new Handler();
    private TextView musicName, musicAuthor;
    private MusicAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        App.getApp().activity = this;
        init();
    }

    private void init() {
        initView();
        initData();
        initService();
    }

    private void initService() {
        MusicController.initMusicService();
    }

    private void initData() {
        scanMusicHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (App.scanMusicFinish) {
                    List<LocalMusicInfo> data = MusicUtil.getInstance().getPlayMusicList();
                    if (data != null && data.size() > 0) {
                        initListViewData(data);
                    } else {
                        mListView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                } else {
                    scanMusicHandler.postDelayed(this, 500);
                }
            }
        }, 100);
    }

    private void initListViewData(final List<LocalMusicInfo> data) {

        if (data != null && data.size() > 0) {
            data.get(0).setSelectedInShouye(true);
            updateMusicInfo(data.get(0));
        }

        mAdapter = new MusicAdapter(this, data);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocalMusicInfo musicInfo = data.get(position);

                updateCurMusic(musicInfo);

                if (MusicPlayService.musicPlayService.curPlayState == PLAT_STATE_NORAML) {
                    startPlay(musicInfo);
                } else {
                    resetAndStartPlay(musicInfo);
                }
            }
        });
    }

    private void notifyListViewDataChange(LocalMusicInfo musicInfo) {
        changeListBackground(musicInfo);
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    private void changeListBackground(LocalMusicInfo musicInfo) {
        List<LocalMusicInfo> data = MusicUtil.getInstance().getPlayMusicList();
        for (int i = 0; i < data.size(); i++) {
            if (musicInfo.getName().equals(data.get(i).getName())) {
                data.get(i).setSelectedInShouye(true);
            } else {
                data.get(i).setSelectedInShouye(false);
            }
        }
    }

    private void updateMusicInfo(LocalMusicInfo musicInfo) {
        LogTool.s("updateMusicInfo");
        musicName.setText(musicInfo.getName());
        musicAuthor.setText(musicInfo.getAuthor());
        sb_music.setProgress(0);
    }

    private void initView() {
        ivMusicPlay = (ImageView) findViewById(R.id.iv_music_play);
        ivPre = (ImageView) findViewById(R.id.iv_pre);
        ivNext = (ImageView) findViewById(R.id.iv_next);
        sb_music = (SeekBar) findViewById(R.id.sb_music);
        currTime = (TextView) findViewById(R.id.tv_curr_play_time);
        totalTime = (TextView) findViewById(R.id.tv_total_play_time);
        musicName = (TextView) findViewById(R.id.tv_name);
        musicAuthor = (TextView) findViewById(R.id.tv_author);
        emptyView = (TextView) findViewById(R.id.emptyView);
        mListView = (ListView) findViewById(R.id.musiclist);
        ivMusicPlay.setOnClickListener(this);
        ivPre.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        sb_music.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int needPlayPosition = (int) (progress / 100.0 * MusicPlayService.musicPlayService.mMediaPlyer.getDuration());
                MusicPlayService.musicPlayService.mMediaPlyer.seekTo(needPlayPosition);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void refreshTime(int position) {
        int progress = (int) (((position / (float) MusicUtil.getInstance().getCurrPlayMusicInfo().getDuration())) * 100);
        LogTool.s("refreshTime progress = " + progress);
        sb_music.setProgress(progress);
        currTime.setText(Common.getSecDuration2HMSFormatString(position / 1000));
        totalTime.setText(Common.getSecDuration2HMSFormatString((int) MusicUtil.getInstance().getCurrPlayMusicInfo().getDuration() / 1000));
    }

    public void updateImage() {
        switch (MusicPlayService.musicPlayService.curPlayState) {
            case PLAY_STATE_PLAYING:
                ivMusicPlay.setImageResource(R.mipmap.music_button_play);
                break;
            case PLAY_STATE_PAUSED:
                ivMusicPlay.setImageResource(R.mipmap.music_button_pause);
                break;
            case PLAT_STATE_NORAML:
                ivMusicPlay.setImageResource(R.mipmap.music_button_pause);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_music_play:
                handleMusicControl();
                break;
            case R.id.iv_pre:
                handlePreControl();
                break;
            case R.id.iv_next:
                handleNextControl();
                break;
        }
    }

    private void handleNextControl() {
        MusicController.playNext();
    }

    public void updateNextMusic() {
        LocalMusicInfo musicInfo = MusicUtil.getInstance().getNextMusicInfo();
        updateCurMusic(musicInfo);
    }

    private void handlePreControl() {
        MusicController.playPre();
    }

    public void updatePreMusic() {
        LocalMusicInfo musicInfo = MusicUtil.getInstance().getPreMusicInfo();
        updateCurMusic(musicInfo);
    }

    private void updateCurMusic(LocalMusicInfo musicInfo) {
        updateMusicInfo(musicInfo);
        notifyListViewDataChange(musicInfo);
        MusicUtil.getInstance().setCurrPlayMusicInfo(musicInfo);
    }

    private void handleMusicControl() {
        switch (MusicPlayService.musicPlayService.curPlayState) {
            case PLAY_STATE_PLAYING:
                MusicController.pausePlay();
                break;
            case PLAY_STATE_PAUSED:
                MusicController.continuePlay();
                break;
            case PLAT_STATE_NORAML:
                if (MusicUtil.getInstance().getPlayMusicList().size() > 0) {
                    updateCurMusic(MusicUtil.getInstance().getPlayMusicList().get(0));
                    startPlay(MusicUtil.getInstance().getPlayMusicList().get(0));
                } else {
                    Toast.makeText(this, "暂无可播放歌曲", Toast.LENGTH_SHORT);
                }
                break;
        }
    }

    private void resetAndStartPlay(LocalMusicInfo musicInfo) {
        MusicController.resetStartPlay(musicInfo.getSource());
    }

    private void startPlay(LocalMusicInfo musicInfo) {
        MusicController.startPlay(musicInfo.getSource());
    }
}
