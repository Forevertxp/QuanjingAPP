package com.quanjing.weitu.app.ui.common;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

import com.quanjing.weitu.app.ui.circle.NewCircleFragment;

/**
 * Created by Administrator on 2015/4/22.
 *
 */
public class PreImeEditText extends EditText {

    private Context context;

    public PreImeEditText(Context context) {
        super(context);
        this.context = context;
    }

    public PreImeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public PreImeEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            //when the softinput display
            //处理事件
            Intent intent = new Intent("com.quanjing.hideKeyboard");
            context.sendBroadcast(intent);

        }
        return super.dispatchKeyEventPreIme(event);
    }
}
