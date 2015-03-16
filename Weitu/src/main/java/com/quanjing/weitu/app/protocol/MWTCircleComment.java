package com.quanjing.weitu.app.protocol;

/**
 * Created by tianxiaopeng on 15-3-3.
 */
public class MWTCircleComment {
    private String activityId;
    private String userid;
    private String content;
    private String posttime;
    private String replyuserid;

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPosttime() {
        return posttime;
    }

    public void setPosttime(String posttime) {
        this.posttime = posttime;
    }

    public String getReplyuserid() {
        return replyuserid;
    }

    public void setReplyuserid(String replyuserid) {
        this.replyuserid = replyuserid;
    }
}
