package com.zyyoona7.stitcher.size;

/**
 * 存储尺寸信息
 */
public final class StitchSize {

    //最大宽高尺寸，防止 OOM
    private static final int MAX_SIZE=7000*7000;

    private final int mWidth;
    private final int mHeight;

    public StitchSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }


    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public boolean isEmpty() {
        return mWidth <= 0 || mHeight <= 0;
    }

    public boolean isOverMaxSize(){
        return mWidth*mHeight>MAX_SIZE;
    }
}