package com.zyyoona7.stitcher.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.exifinterface.media.ExifInterface;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.Set;

public class StitcherUtils {

    private static Set<SoftReference<Bitmap>> sReusableBitmaps;
    private StitcherUtils() {

    }

    public static int roundFloatToInt(float ratio) {
        int result = Math.round(ratio);
        return result == 0 ? 1 : result;
    }

    @NonNull
    public static BitmapFactory.Options decodeBitmapBounds(String filePath) {
        //获取图片信息，修复图片有的手机照片旋转问题
        int rotateDegree = getBitmapDegree(filePath);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        if (rotateDegree > 0) {
            changeSizeByDegree(options, rotateDegree);
        }
        return options;
    }

    private static void changeSizeByDegree(@NonNull BitmapFactory.Options options,
                                           int rotateDegree) {
        if (rotateDegree <= 0) {
            return;
        }
        if (rotateDegree == 90 || rotateDegree == 270) {
            //宽高互换
            int tempW = options.outHeight;
            int tempH = options.outWidth;
            options.outWidth = tempW;
            options.outHeight = tempH;
        }
//        if (rotateDegree==180) {
//            //忽略
//        }
    }


    @Nullable
    public static Bitmap decodeBitmap(String filePath, BitmapFactory.Options options) {
        options.inMutable = true;
        Bitmap inBitmap = ReusableCache.getBitmap(options);
        if (inBitmap != null) {
            options.inBitmap = inBitmap;
        }
        //获取图片信息，修复图片有的手机照片旋转问题
        int rotateDegree = getBitmapDegree(filePath);
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        if (rotateDegree > 0) {
            return rotateBitmapByDegree(bitmap, rotateDegree);
        }
        return bitmap;
    }

    /**
     * 获取图片的旋转角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    private static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    degree = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片按照指定的角度进行旋转
     *
     * @param bitmap 需要旋转的图片
     * @param degree 指定的旋转角度
     * @return 旋转后的图片
     */
    @Nullable
    private static Bitmap rotateBitmapByDegree(Bitmap bitmap, int degree) {
        if (isEmptyBitmap(bitmap)) {
            return null;
        }
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (!bitmap.isRecycled() && newBitmap != bitmap) {
            bitmap.recycle();
        }
        return newBitmap;
    }

    public static boolean isEmptyBitmap(final Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static boolean canUseForInBitmap(Bitmap candidate, BitmapFactory.Options targetOptions) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // From Android 4.4 (KitKat) onward we can re-use if the byte size of
            // the new bitmap is smaller than the reusable bitmap candidate
            // allocation byte count.
            int width = targetOptions.outWidth / targetOptions.inSampleSize;
            int height = targetOptions.outHeight / targetOptions.inSampleSize;
            int byteCount = width * height * getBytesPerPixel(candidate.getConfig());

            try {
                return byteCount <= candidate.getAllocationByteCount();
            } catch (NullPointerException e) {
                return byteCount <= candidate.getHeight() * candidate.getRowBytes();
            }
        }
        // On earlier versions, the dimensions must match exactly and the inSampleSize must be 1
        return candidate.getWidth() == targetOptions.outWidth
                && candidate.getHeight() == targetOptions.outHeight
                && targetOptions.inSampleSize == 1;
    }

    private static int getBytesPerPixel(Bitmap.Config config) {
        // A bitmap by decoding a gif has null "config" in certain environments.
        if (config == null) {
            config = Bitmap.Config.ARGB_8888;
        }

        int bytesPerPixel;
        switch (config) {
            case ALPHA_8:
                bytesPerPixel = 1;
                break;
            case RGB_565:
            case ARGB_4444:
                bytesPerPixel = 2;
                break;
            case ARGB_8888:
            default:
                bytesPerPixel = 4;
                break;
        }
        return bytesPerPixel;
    }

    protected Bitmap getBitmapFromReusableSet(BitmapFactory.Options options) {
        //BEGIN_INCLUDE(get_bitmap_from_reusable_set)
        Bitmap bitmap = null;

        if (sReusableBitmaps != null && !sReusableBitmaps.isEmpty()) {
            synchronized (this) {
                final Iterator<SoftReference<Bitmap>> iterator = sReusableBitmaps.iterator();
                Bitmap item;

                while (iterator.hasNext()) {
                    item = iterator.next().get();

                    if (null != item && item.isMutable()) {
                        // Check to see it the item can be used for inBitmap
                        if (canUseForInBitmap(item, options)) {
                            bitmap = item;
                            // Remove from reusable set so it can't be used again
                            iterator.remove();
                            break;
                        }
                    } else {
                        if (item != null) {
                            item.recycle();
                        }
                        // Remove from the set if the reference has been cleared.
                        iterator.remove();
                    }
                }
            }
        }

        return bitmap;
        //END_INCLUDE(get_bitmap_from_reusable_set)
    }
}
