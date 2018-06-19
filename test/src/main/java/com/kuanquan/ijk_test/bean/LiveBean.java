package com.kuanquan.ijk_test.bean;

public class LiveBean {

    /**
     * nickname : 直播
     * liveStream : http://pull.kktv8.com/livekktv/109204379.flv
     */

    private String nickname;
    private long livestarttime;
    private String liveStream;
    private String portrait;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public long getLivestarttime() {
        return livestarttime;
    }

    public void setLivestarttime(long livestarttime) {
        this.livestarttime = livestarttime;
    }

    public String getLiveStream() {
        return liveStream;
    }

    public void setLiveStream(String liveStream) {
        this.liveStream = liveStream;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }
}
