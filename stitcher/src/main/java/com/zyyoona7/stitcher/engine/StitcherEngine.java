package com.zyyoona7.stitcher.engine;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.zyyoona7.stitcher.size.StitchSize;
import com.zyyoona7.stitcher.util.StitcherUtils;

import java.util.List;

/**
 * 图片拼接的引擎
 */
public class StitcherEngine {

    /**
     * 垂直方向排列多张图片拼接
     *
     * @param pathList        图片地址列表
     * @param destWidth       目标宽度
     * @param verticalSpacing 垂直间距
     * @param fillColor       间距或透明部分的填充颜色
     * @return Bitmap if null 拼接出错
     */
    @Nullable
    public static Bitmap stitchVertical(List<String> pathList, int destWidth,
                                        int verticalSpacing, @ColorInt int fillColor) {
        //测量尺寸
        StitchSize size = SizeEngine.calculateVerticalSize(pathList, destWidth, verticalSpacing);

        if (size.isEmpty()) {
            return null;
        }

        Bitmap destBitmap = Bitmap.createBitmap(size.getWidth(), size.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = createCanvas(destBitmap);
        Paint paint = createPaint();
        if (fillColor != Color.TRANSPARENT) {
            canvas.drawColor(fillColor);
        }
        int currentY = 0;

        try {
            for (String filePath : pathList) {
                BitmapFactory.Options options = StitcherUtils.decodeBitmapBounds(filePath);
                int width = options.outWidth;
                int height = options.outHeight;
                float ratio = height * 1f / width;

                if (width != size.getWidth()) {
                    width = size.getWidth();
                    height = StitcherUtils.roundFloatToInt(width * ratio);
                }

                int bottom = currentY + height;
                Rect rect = new Rect(0, currentY, width, bottom);

                options.inJustDecodeBounds = false;
                options.inSampleSize = (rect.right - rect.left) / options.outWidth;

                Bitmap bitmap = StitcherUtils.decodeBitmap(filePath, options);
                if (bitmap == null) {
                    continue;
                }
                try {
                    bitmap.setDensity(Bitmap.DENSITY_NONE);
                    canvas.drawBitmap(bitmap, null, rect, paint);
                } finally {
                    if (!bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                }

                currentY = rect.bottom + verticalSpacing;
            }
        } catch (Exception e) {
            return null;
        }
        return destBitmap;
    }

    /**
     * 垂直方向排列单张图片多次拼接
     *
     * @param filePath        文件路径
     * @param stitchCount     拼接次数
     * @param destWidth       目标宽度
     * @param verticalSpacing 垂直间距
     * @param fillColor       间距或透明部分的填充颜色
     * @return Bitmap if null 拼接出错
     */
    @Nullable
    public static Bitmap stitchVertical(String filePath, int stitchCount, int destWidth,
                                        int verticalSpacing, @ColorInt int fillColor) {
        //测量尺寸
        StitchSize size = SizeEngine.calculateVerticalSize(filePath, stitchCount,
                destWidth, verticalSpacing);

        if (size.isEmpty()) {
            return null;
        }

        Bitmap destBitmap = Bitmap.createBitmap(size.getWidth(), size.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = createCanvas(destBitmap);
        Paint paint = createPaint();
        if (fillColor != Color.TRANSPARENT) {
            canvas.drawColor(fillColor);
        }
        int currentY = 0;

        try {
            BitmapFactory.Options options = StitcherUtils.decodeBitmapBounds(filePath);
            int width = options.outWidth;
            int height = options.outHeight;
            float ratio = height * 1f / width;

            if (width != size.getWidth()) {
                width = size.getWidth();
                height = StitcherUtils.roundFloatToInt(width * ratio);
            }

            options.inJustDecodeBounds = false;
            options.inSampleSize = width / options.outWidth;

            Bitmap bitmap = StitcherUtils.decodeBitmap(filePath, options);
            if (bitmap == null) {
                return null;
            }
            try {
                for (int i = 0; i < stitchCount; i++) {
                    int bottom = currentY + height;
                    Rect rect = new Rect(0, currentY, width, bottom);

                    bitmap.setDensity(Bitmap.DENSITY_NONE);
                    canvas.drawBitmap(bitmap, null, rect, paint);

                    currentY = rect.bottom + verticalSpacing;
                }
            } finally {
                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        } catch (Exception e) {
            return null;
        }
        return destBitmap;
    }

    /**
     * 水平方向排列多张图片拼接
     *
     * @param pathList          图片地址列表
     * @param destHeight        目标高度
     * @param horizontalSpacing 水平间距
     * @param fillColor         间距或透明部分的填充颜色
     * @return Bitmap if null 拼接出错
     */
    @Nullable
    public static Bitmap stitchHorizontal(List<String> pathList, int destHeight,
                                          int horizontalSpacing, @ColorInt int fillColor) {
        //测量尺寸
        StitchSize size = SizeEngine.calculateHorizontalSize(pathList, destHeight, horizontalSpacing);

        if (size.isEmpty()) {
            return null;
        }

        Bitmap destBitmap = Bitmap.createBitmap(size.getWidth(), size.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = createCanvas(destBitmap);
        Paint paint = createPaint();
        if (fillColor != Color.TRANSPARENT) {
            canvas.drawColor(fillColor);
        }
        int currentX = 0;

        try {
            for (String filePath : pathList) {
                BitmapFactory.Options options = StitcherUtils.decodeBitmapBounds(filePath);
                int width = options.outWidth;
                int height = options.outHeight;
                float ratio = width * 1f / height;

                if (height != size.getHeight()) {
                    height = size.getHeight();
                    width = StitcherUtils.roundFloatToInt(height * ratio);
                }

                int right = currentX + width;
                Rect rect = new Rect(currentX, 0, right, height);

                options.inJustDecodeBounds = false;
                options.inSampleSize = (rect.bottom - rect.top) / options.outHeight;

                Bitmap bitmap = StitcherUtils.decodeBitmap(filePath, options);
                if (bitmap == null) {
                    continue;
                }
                try {
                    bitmap.setDensity(Bitmap.DENSITY_NONE);
                    canvas.drawBitmap(bitmap, null, rect, paint);
                } finally {
                    if (!bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                }

                currentX = rect.right + horizontalSpacing;
            }
        } catch (Exception e) {
            return null;
        }
        return destBitmap;
    }

    /**
     * 水平方向排列单张图片多次拼接
     *
     * @param filePath          图片地址
     * @param stitchCount       拼接次数
     * @param destHeight        目标高度
     * @param horizontalSpacing 水平间距
     * @param fillColor         间距或透明部分的填充颜色
     * @return Bitmap if null 拼接出错
     */
    @Nullable
    public static Bitmap stitchHorizontal(String filePath, int stitchCount, int destHeight,
                                          int horizontalSpacing, @ColorInt int fillColor) {
        //测量尺寸
        StitchSize size = SizeEngine.calculateHorizontalSize(filePath, stitchCount,
                destHeight, horizontalSpacing);

        if (size.isEmpty()) {
            return null;
        }

        Bitmap destBitmap = Bitmap.createBitmap(size.getWidth(), size.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = createCanvas(destBitmap);
        Paint paint = createPaint();
        if (fillColor != Color.TRANSPARENT) {
            canvas.drawColor(fillColor);
        }
        int currentX = 0;

        try {

            BitmapFactory.Options options = StitcherUtils.decodeBitmapBounds(filePath);
            int width = options.outWidth;
            int height = options.outHeight;
            float ratio = width * 1f / height;

            if (height != size.getHeight()) {
                height = size.getHeight();
                width = StitcherUtils.roundFloatToInt(height * ratio);
            }

            options.inJustDecodeBounds = false;
            options.inSampleSize = height / options.outHeight;

            Bitmap bitmap = StitcherUtils.decodeBitmap(filePath, options);
            if (bitmap == null) {
                return null;
            }
            try {
                for (int i = 0; i < stitchCount; i++) {
                    int right = currentX + width;
                    Rect rect = new Rect(currentX, 0, right, height);

                    bitmap.setDensity(Bitmap.DENSITY_NONE);
                    canvas.drawBitmap(bitmap, null, rect, paint);

                    currentX = rect.right + horizontalSpacing;
                }
            } finally {
                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }

        } catch (Exception e) {
            return null;
        }

        return destBitmap;
    }

    private static Paint createPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        return paint;
    }

    private static Canvas createCanvas(Bitmap bitmap) {
        return new Canvas(bitmap);
    }
}
