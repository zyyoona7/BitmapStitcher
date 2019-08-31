package com.zyyoona7.stitcher.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 根据官方示例和 GlideBitmapPool 结合，实现简易版的 Bitmap 复用的缓存
 * 参考 https://developer.android.google.cn/topic/performance/graphics/manage-memory.html?hl=zh-cn#java
 * 参考 https://github.com/amitshekhariitbhu/GlideBitmapPool
 * 参考 https://github.com/googlesamples/android-DisplayingBitmaps
 */
public class ReusableCache {
    private static final int DEFAULT_MAX_SIZE = 15;

    private List<WeakReference<Bitmap>> mReusableBitmaps;
    private final Object mLock = new Object();

    private ReusableCache() {
        mReusableBitmaps =
                Collections.synchronizedList(new LinkedList<WeakReference<Bitmap>>());
    }

    private static class Holder {
        private static final ReusableCache INSTANCE = new ReusableCache();
    }

    private static ReusableCache getInstance() {
        return Holder.INSTANCE;
    }

    private void put(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()
                || mReusableBitmaps == null) {
            return;
        }
        if (!bitmap.isMutable()) {
            bitmap.recycle();
        }
        if (mReusableBitmaps.size() > DEFAULT_MAX_SIZE) {
            mReusableBitmaps.remove(mReusableBitmaps.size() - 1);
        }
        mReusableBitmaps.add(new WeakReference<>(bitmap));
    }

    @Nullable
    private Bitmap get(BitmapFactory.Options options) {
        //BEGIN_INCLUDE(get_bitmap_from_reusable_set)
        Bitmap bitmap = null;

        if (mReusableBitmaps != null && !mReusableBitmaps.isEmpty()) {
            synchronized (mLock) {
                final Iterator<WeakReference<Bitmap>> iterator = mReusableBitmaps.iterator();
                Bitmap item;

                while (iterator.hasNext()) {
                    item = iterator.next().get();

                    if (null != item && item.isMutable()) {
                        // Check to see it the item can be used for inBitmap
                        if (StitcherUtils.canUseForInBitmap(item, options)) {
                            bitmap = item;

                            // Remove from reusable set so it can't be used again
                            iterator.remove();
                            break;
                        }
                    } else {
                        // Remove from the set if the reference has been cleared.
                        iterator.remove();
                    }
                }
            }
        }
        Log.d("ReusableCache", "get: execute...bitmap=" + bitmap);
        return bitmap;
        //END_INCLUDE(get_bitmap_from_reusable_set)
    }

    private void clear() {
        if (mReusableBitmaps != null && !mReusableBitmaps.isEmpty()) {
            final Iterator<WeakReference<Bitmap>> iterator = mReusableBitmaps.iterator();
            Bitmap item;
            while (iterator.hasNext()) {
                item = iterator.next().get();

                if (null != item ) {
                    item.recycle();
                }
                iterator.remove();
            }
        }
    }

    public static void putBitmap(Bitmap bitmap) {
        getInstance().put(bitmap);
    }

    @Nullable
    public static Bitmap getBitmap(BitmapFactory.Options options) {
        return getInstance().get(options);
    }

    public static void clearBitmap(){
        getInstance().clear();
    }
}
