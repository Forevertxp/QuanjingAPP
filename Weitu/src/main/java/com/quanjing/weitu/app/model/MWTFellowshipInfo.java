package com.quanjing.weitu.app.model;

import com.quanjing.weitu.app.protocol.MWTFellowshipInfoData;
import com.quanjing.weitu.app.protocol.MWTUserPrivateInfoData;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2014/12/30.
 */
public class MWTFellowshipInfo implements Serializable {

    private ArrayList<String> _followingUserIDs;
    private Integer _followingNum;
    private Integer _followerNum;

    public ArrayList<String> get_followingUserIDs() {
        return _followingUserIDs;
    }

    public Integer get_followingNum() {
        return _followingNum;
    }

    public Integer get_followerNum() {
        return _followerNum;
    }

    public void set_followingUserIDs(ArrayList<String> _followingUserIDs) {
        this._followingUserIDs = _followingUserIDs;
    }

    public void set_followingNum(Integer _followingNum) {
        this._followingNum = _followingNum;
    }

    public void set_followerNum(Integer _followerNum) {
        this._followerNum = _followerNum;
    }

    public void mergeWithData(MWTFellowshipInfoData fellowshipInfoData)
    {
        if (fellowshipInfoData.followingUserIDs != null)
        {
            _followingUserIDs = fellowshipInfoData.followingUserIDs;
        }

        if (fellowshipInfoData.followerNum != null)
        {
            _followerNum = fellowshipInfoData.followerNum;
        }

        if (fellowshipInfoData.followingNum != null)
        {
            _followingNum = fellowshipInfoData.followingNum;
        }
    }

}
