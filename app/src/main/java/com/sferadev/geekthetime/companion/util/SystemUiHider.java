package com.sferadev.geekthetime.companion.util;

import android.app.Activity;
import android.view.View;

public abstract class SystemUiHider {

    public static final int FLAG_LAYOUT_IN_SCREEN_OLDER_DEVICES = 0x1;
    public static final int FLAG_FULLSCREEN = 0x2;
    public static final int FLAG_HIDE_NAVIGATION = FLAG_FULLSCREEN | 0x4;
    private static final OnVisibilityChangeListener sDummyListener = new OnVisibilityChangeListener() {
        @Override
        public void onVisibilityChange(boolean visible) {
        }
    };
    protected OnVisibilityChangeListener mOnVisibilityChangeListener = sDummyListener;
    protected final Activity mActivity;
    protected final int mFlags;

    protected SystemUiHider(Activity activity, View anchorView, int flags) {
        mActivity = activity;
        mFlags = flags;
    }

    public static SystemUiHider getInstance(Activity activity, View anchorView, int flags) {
        return new SystemUiHiderBase(activity, anchorView, flags);
    }

    public abstract void setup();

    public abstract boolean isVisible();

    public abstract void hide();

    public abstract void show();

    public void toggle() {
        if (isVisible()) {
            hide();
        } else {
            show();
        }
    }

    public void setOnVisibilityChangeListener(OnVisibilityChangeListener listener) {
        if (listener == null) {
            listener = sDummyListener;
        }

        mOnVisibilityChangeListener = listener;
    }

    public interface OnVisibilityChangeListener {
        public void onVisibilityChange(boolean visible);
    }
}
