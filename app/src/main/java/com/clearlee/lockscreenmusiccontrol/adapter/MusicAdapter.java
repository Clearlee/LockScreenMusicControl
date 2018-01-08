package com.clearlee.lockscreenmusiccontrol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clearlee.lockscreenmusiccontrol.R;
import com.clearlee.lockscreenmusiccontrol.bean.LocalMusicInfo;
import com.clearlee.lockscreenmusiccontrol.util.Common;

import java.util.List;

/**
 * Created by Clearlee on 2017/12/26 0026.
 */

public class MusicAdapter extends BaseAdapter {

    private Context mContext;
    private List<LocalMusicInfo> mDatas;

    public MusicAdapter(Context context, List<LocalMusicInfo> data) {
        this.mContext = context;
        this.mDatas = data;
    }


    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LocalMusicInfo musicInfo = mDatas.get(position);

        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_music_list, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_name.setText(musicInfo.getName() + "");
        holder.tv_author.setText(musicInfo.getAuthor() + "");
        holder.tv_duration.setText(Common.getSecDuration2HMSFormatString((int) musicInfo.getDuration() / 1000));

        if (musicInfo.isSelectedInShouye()) {
            holder.musicItem.setBackgroundColor(mContext.getResources().getColor(R.color.grayf0));
        } else {
            holder.musicItem.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }

        return convertView;
    }

    public class ViewHolder {
        TextView tv_name;
        TextView tv_author;
        ImageView iv_resource;
        TextView tv_duration;
        RelativeLayout musicItem;

        ViewHolder(View convertView) {
            tv_duration = (TextView) convertView.findViewById(R.id.tv_duration);
            tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            tv_author = (TextView) convertView.findViewById(R.id.tv_author);
            iv_resource = (ImageView) convertView.findViewById(R.id.iv_resource);
            musicItem = (RelativeLayout) convertView.findViewById(R.id.music_item);
        }

    }
}
