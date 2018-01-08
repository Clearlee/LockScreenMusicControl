package com.clearlee.lockscreenmusiccontrol.bean;

/**
 * 本地音乐信息
 */
public class LocalMusicInfo {

    private boolean isSelectedInShouye = false;//首页列表里是否已经选中

    String id = "";
    private String name = ""; //音乐名称
    private String author = ""; //音乐作者
    private String album = ""; //音乐专辑
    private long duration = 0; //音乐时长
    private long size = 0; //音乐大小
    private String source = ""; //音乐地址

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public boolean isSelectedInShouye() {
        return isSelectedInShouye;
    }

    public void setSelectedInShouye(boolean selectedInShouye) {
        isSelectedInShouye = selectedInShouye;
    }

}
