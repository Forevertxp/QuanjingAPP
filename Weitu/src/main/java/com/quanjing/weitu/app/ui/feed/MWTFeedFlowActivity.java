package com.quanjing.weitu.app.ui.feed;

import android.os.Bundle;
import com.quanjing.weitu.R;
import com.quanjing.weitu.app.model.MWTFeed;
import com.quanjing.weitu.app.model.MWTFeedManager;
import com.quanjing.weitu.app.ui.common.MWTBaseSearchActivity;

public class MWTFeedFlowActivity extends MWTBaseSearchActivity
{
    public final static String ARG_FEED_ID = "ARG_FEED_ID";

    private MWTFeedFlowFragment _feedFlowFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_flow);

        if (savedInstanceState == null)
        {
            if (_feedFlowFragment == null)
            {
                String feedID = getIntent().getStringExtra(ARG_FEED_ID);

                if (feedID != null)
                {
                    MWTFeed feed = MWTFeedManager.getInstance().getFeedWithID(feedID);
                    setTitle(feed.getNameZH());
                }

                _feedFlowFragment = MWTFeedFlowFragment.newInstance(feedID);
                getFragmentManager().beginTransaction()
                                    .add(R.id.container, _feedFlowFragment)
                                    .commit();
            }
        }
    }
}
