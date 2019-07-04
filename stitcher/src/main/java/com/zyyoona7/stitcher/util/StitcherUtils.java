package com.zyyoona7.stitcher.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.exifinterface.media.ExifInterface;

import java.io.IOException;

public class StitcherUtils {

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
}
