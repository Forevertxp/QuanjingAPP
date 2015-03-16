package com.quanjing.weitu.app.model;

import com.quanjing.weitu.app.common.MWTCallback;
import com.quanjing.weitu.app.protocol.MWTError;
import com.quanjing.weitu.app.protocol.MWTFeedData;
import com.quanjing.weitu.app.protocol.MWTFeedItemData;
import com.quanjing.weitu.app.protocol.service.MWTFeedRefreshResult;
import com.quanjing.weitu.app.protocol.service.MWTSquareService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MWTSquareFeed {
    private final static int kWTFeedRefreshItemNum = 50;
    private ArrayList<MWTFeedItem> _items = new ArrayList<>();
    private HashMap<String, MWTFeedItem> _itemsByID = new HashMap<>();
    private long _maxItemTimestamp;
    private long _minItemTimestamp;
    private MWTSquareService _feedService;

    private static int TIMES = 1;

    public MWTSquareFeed() {
    }

    public ArrayList<MWTFeedItem> getItems() {
        return _items;
    }

    public MWTFeedItem getItem(int index) {
        if (_items != null) {
            return _items.get(index);
        } else {
            return null;
        }
    }

    public int getItemNum() {
        if (_items != null) {
            return _items.size();
        } else {
            return 0;
        }
    }

    public MWTAsset getAsset(int index) {
        MWTFeedItem item = getItem(index);
        if (item != null) {
            return item.getAsset();
        } else {
            return null;
        }
    }

    private MWTSquareService getSquareService() {
        if (_feedService == null) {
            _feedService = MWTSquareFeedManager.getInstance().getService();
        }

        return _feedService;
    }

    public void refresh(final MWTCallback callback) {
        TIMES = 1;
        getSquareService().fetchItems(kWTFeedRefreshItemNum, 0, Long.MAX_VALUE, new Callback<MWTFeedRefreshResult>() {
            @Override
            public void success(MWTFeedRefreshResult result, Response response) {
                if (result == null) {
                    if (callback != null) {
                        callback.failure(new MWTError(-1, "服务器返回数据出错"));
                    }
                    return;
                }

                if (result.error != null) {
                    if (callback != null) {
                        callback.failure(result.error);
                    }
                    return;
                }

                MWTUserManager.getInstance().registerUserDatas(result.relatedUsers);
                mergeWithData(result.feedFragment, true);
                if (callback != null) {
                    callback.success();
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                if (callback != null) {
                    callback.failure(new MWTError(retrofitError));
                }
            }
        });

    }

    public void loadMore(final MWTCallback callback) {
        TIMES++;
        getSquareService().fetchItems(kWTFeedRefreshItemNum * TIMES, 0, Long.MAX_VALUE, new Callback<MWTFeedRefreshResult>() {
                    @Override
                    public void success(MWTFeedRefreshResult result, Response response) {
                        if (result == null) {
                            if (callback != null) {
                                callback.failure(new MWTError(-1, "服务器返回数据出错"));
                            }
                            return;
                        }

                        if (result.error != null) {
                            if (callback != null) {
                                callback.failure(result.error);
                            }
                            return;
                        }

                        MWTUserManager.getInstance().registerUserDatas(result.relatedUsers);
                        mergeWithData(result.feedFragment, true);
                        if (callback != null) {
                            callback.success();
                        }
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        if (callback != null) {
                            callback.failure(new MWTError(retrofitError));
                        }
                    }
                }

        );
    }

    private void mergeWithData(MWTFeedData feedData, boolean dropOldItems) {
        if (feedData == null) {
            return;
        }

        if (dropOldItems) {
            _items.clear();
            _itemsByID.clear();
            ;

            _maxItemTimestamp = 0;
            _minItemTimestamp = Long.MAX_VALUE;

            if (feedData.items != null) {
                for (MWTFeedItemData itemData : feedData.items) {
                    MWTFeedItem item = new MWTFeedItem();
                    _itemsByID.put(itemData.itemID, item);
                    _items.add(item);

                    item.mergeWithData(itemData);

                    _maxItemTimestamp = Math.max(_maxItemTimestamp, item.getTimestamp());
                    _minItemTimestamp = Math.min(_minItemTimestamp, item.getTimestamp());
                }
            }
        } else {
            for (MWTFeedItemData itemData : feedData.items) {
                MWTFeedItem item = _itemsByID.get(itemData.itemID);
                if (item == null) {
                    item = new MWTFeedItem();
                    _itemsByID.put(itemData.itemID, item);
                    _items.add(item);
                }

                item.mergeWithData(itemData);
                _maxItemTimestamp = Math.max(_maxItemTimestamp, item.getTimestamp());
                _minItemTimestamp = Math.min(_minItemTimestamp, item.getTimestamp());
            }
        }

        Collections.sort(_items);
    }
}
