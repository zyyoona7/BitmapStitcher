package com.zyyoona7.stitcher;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.zyyoona7.stitcher.engine.SizeEngine;
import com.zyyoona7.stitcher.engine.StitcherEngine;
import com.zyyoona7.stitcher.util.StitcherUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 图片拼接、裁剪、保存外部调用类
 */
public class BitmapStitcher {
    //缩放策略
    //如果 宽/高 小于最大 宽/高 尺寸，则缩放至最大 宽/高 尺寸
    public static final int SCALE_LARGER = SizeEngine.SCALE_LARGER;
    //如果 宽/高 大于最小 宽/高 尺寸，则缩放至最小 宽/高 尺寸
    public static final int SCALE_SMALLER = SizeEngine.SCALE_SMALLER;

    /*
       ---------- stitch bitmap area ----------
     */

    /**
     * 垂直方向排列，多张图片拼接
     *
     * @param pathList  图片地址列表
     * @param destWidth 目标宽度
     * @return Bitmap if null 出错
     */
    @Nullable
    public static Bitmap stitchVertical(List<String> pathList, int destWidth) {
        return stitchVertical(pathList, destWidth, 0);
    }

    /**
     * 垂直方向排列，多张图片拼接
     *
     * @param pathList        图片地址列表
     * @param destWidth       目标宽度
     * @param verticalSpacing 垂直间距
     * @return Bitmap if null 出错
     */
    @Nullable
    public static Bitmap stitchVertical(List<String> pathList, int destWidth,
                                        int verticalSpacing) {
        return stitchVertical(pathList, destWidth, verticalSpacing, Color.TRANSPARENT);
    }

    /**
     * 垂直方向排列，多张图片拼接
     *
     * @param pathList        图片地址列表
     * @param destWidth       目标宽度
     * @param verticalSpacing 垂直间距
     * @param fillColor       间距或透明部分的填充颜色
     * @return Bitmap if null 出错
     */
    @Nullable
    public static Bitmap stitchVertical(List<String> pathList, int destWidth,
                                        int verticalSpacing, @ColorInt int fillColor) {
        return StitcherEngine.stitchVertical(pathList, destWidth, verticalSpacing, fillColor);
    }

    /**
     * 垂直方向排列 单张图片 多次拼接
     *
     * @param filePath    图片地址
     * @param stitchCount 拼接次数
     * @param destWidth   目标宽度
     * @return Bitmap if null 出错
     */
    @Nullable
    public static Bitmap stitchVertical(String filePath, int stitchCount, int destWidth) {
        return stitchVertical(filePath, stitchCount, destWidth, 0);
    }

    /**
     * 垂直方向排列 单张图片 多次拼接
     *
     * @param filePath        图片地址
     * @param stitchCount     拼接次数
     * @param destWidth       目标宽度
     * @param verticalSpacing 垂直间距
     * @return Bitmap if null 出错
     */
    @Nullable
    public static Bitmap stitchVertical(String filePath, int stitchCount, int destWidth,
                                        int verticalSpacing) {
        return stitchVertical(filePath, stitchCount, destWidth,
                verticalSpacing, Color.TRANSPARENT);
    }

    /**
     * 垂直方向排列 单张图片 多次拼接
     *
     * @param filePath        图片地址
     * @param stitchCount     拼接次数
     * @param destWidth       目标宽度
     * @param verticalSpacing 垂直间距
     * @param fillColor       间距或透明部分的填充颜色
     * @return Bitmap if null 出错
     */
    @Nullable
    public static Bitmap stitchVertical(String filePath, int stitchCount, int destWidth,
                                        int verticalSpacing, @ColorInt int fillColor) {
        return StitcherEngine.stitchVertical(filePath, stitchCount, destWidth,
                verticalSpacing, fillColor);
    }

    /**
     * 水平方向排列 多张图片拼接
     *
     * @param pathList   图片地址列表
     * @param destHeight 目标宽度
     * @return Bitmap if null 出错
     */
    @Nullable
    public static Bitmap stitchHorizontal(List<String> pathList, int destHeight) {
        return stitchHorizontal(pathList, destHeight, 0);
    }

    /**
     * 水平方向排列 多张图片拼接
     *
     * @param pathList          图片地址列表
     * @param destHeight        目标宽度
     * @param horizontalSpacing 水平间距
     * @return Bitmap if null 出错
     */
    @Nullable
    public static Bitmap stitchHorizontal(List<String> pathList, int destHeight, int horizontalSpacing) {
        return stitchHorizontal(pathList, destHeight, horizontalSpacing, Color.TRANSPARENT);
    }

    /**
     * 水平方向排列 多张图片拼接
     *
     * @param pathList          图片地址列表
     * @param destHeight        目标宽度
     * @param horizontalSpacing 水平间距
     * @param fillColor         间距或透明部分的填充颜色
     * @return Bitmap if null 出错
     */
    @Nullable
    public static Bitmap stitchHorizontal(List<String> pathList, int destHeight,
                                          int horizontalSpacing, @ColorInt int fillColor) {
        return StitcherEngine.stitchHorizontal(pathList, destHeight, horizontalSpacing, fillColor);
    }

    /**
     * 水平方向排列 单张图片多次拼接
     *
     * @param filePath    图片地址
     * @param stitchCount 拼接次数
     * @param destHeight  目标高度
     * @return Bitmap if null 出错
     */
    @Nullable
    public static Bitmap stitchHorizontal(String filePath, int stitchCount, int destHeight) {
        return stitchHorizontal(filePath, stitchCount, destHeight, 0);
    }

    /**
     * 水平方向排列 单张图片多次拼接
     *
     * @param filePath          图片地址
     * @param stitchCount       拼接次数
     * @param destHeight        目标高度
     * @param horizontalSpacing 水平间距
     * @return Bitmap if null 出错
     */
    @Nullable
    public static Bitmap stitchHorizontal(String filePath, int stitchCount,
                                          int destHeight, int horizontalSpacing) {
        return stitchHorizontal(filePath, stitchCount, destHeight, horizontalSpacing,
                Color.TRANSPARENT);
    }

    /**
     * 水平方向排列 单张图片多次拼接
     *
     * @param filePath          图片地址
     * @param stitchCount       拼接次数
     * @param destHeight        目标高度
     * @param horizontalSpacing 水平间距
     * @param fillColor         间距或透明部分的填充颜色
     * @return Bitmap if null 出错
     */
    @Nullable
    public static Bitmap stitchHorizontal(String filePath, int stitchCount, int destHeight,
                                          int horizontalSpacing, @ColorInt int fillColor) {
        return StitcherEngine.stitchHorizontal(filePath, stitchCount, destHeight,
                horizontalSpacing, fillColor);
    }

    /*
       ---------- stitch bitmap area ----------
     */

    /*
       ---------- save bitmap area ----------
     */

    /**
     * 保存到本地
     *
     * @param bitmap   bitmap
     * @param filePath 输出文件路径
     * @param format   转换类型
     * @param quality  压缩质量
     */
    @WorkerThread
    public static void save(Bitmap bitmap, String filePath,
                            Bitmap.CompressFormat format, int quality) {
        save(bitmap, new File(filePath), format, quality);
    }

    /**
     * 保存到本地
     *
     * @param bitmap     bitmap
     * @param outputFile 输出文件
     * @param format     转换类型
     * @param quality    压缩质量
     */
    @WorkerThread
    public static void save(Bitmap bitmap, File outputFile,
                            Bitmap.CompressFormat format, int quality) {
        if (StitcherUtils.isEmptyBitmap(bitmap)) {
            return;
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(outputFile);
            bitmap.compress(format, quality, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
       ---------- save bitmap area ----------
     */

    /*
       ---------- clip bitmap area ----------
     */

    /**
     * 从中间位置裁剪图片到指定宽度
     *
     * @param src   源 bitmap
     * @param width 裁剪宽度
     * @return bitmap if null 出错
     */
    @Nullable
    public static Bitmap clipXFromCenter(Bitmap src, int width) {
        if (StitcherUtils.isEmptyBitmap(src)) {
            return null;
        }
        return clipFromCenter(src, width, src.getHeight());
    }

    /**
     * 从中间位置裁剪图片到指定高度
     *
     * @param src    源 bitmap
     * @param height 裁剪高度
     * @return bitmap if null 出错
     */
    @Nullable
    public static Bitmap clipYFromCenter(Bitmap src, int height) {
        if (StitcherUtils.isEmptyBitmap(src)) {
            return null;
        }
        return clipFromCenter(src, src.getWidth(), height);
    }

    /**
     * 从中间位置裁剪图片
     *
     * @param src    源 bitmap
     * @param width  clip width
     * @param height clip height
     * @return bitmap if null 出错
     */
    @Nullable
    public static Bitmap clipFromCenter(Bitmap src, int width, int height) {
        if (StitcherUtils.isEmptyBitmap(src)) {
            return null;
        }
        int x = (src.getWidth() - width) / 2;
        int y = (src.getHeight() - height) / 2;
        return clip(src, x, y, width, height);
    }

    /**
     * 裁剪 bitmap
     *
     * @param src    源 bitmap
     * @param x      x
     * @param y      y
     * @param width  clip width
     * @param height clip height
     * @return bitmap if null 出错
     */
    @Nullable
    public static Bitmap clip(Bitmap src, int x, int y, int width, int height) {
        if (StitcherUtils.isEmptyBitmap(src)) {
            return null;
        }
        if (x < 0 || x > src.getWidth()) {
            x = 0;
        }
        if (y < 0 || y > src.getHeight()) {
            y = 0;
        }
        //超出范围不裁剪，否则 createBitmap() 会抛出异常
        if (width > src.getWidth() || height > src.getHeight()
                || x + width > src.getWidth() || y + height > src.getHeight()) {
            return src;
        }
        Bitmap clippedBitmap = Bitmap.createBitmap(src, x, y, width, height);
        if (!src.isRecycled() && clippedBitmap != src) {
            src.recycle();
        }
        return clippedBitmap;
    }

    /**
     * 裁剪 Bitmap 为正方形
     *
     * @param src 源 bitmap
     * @return bitmap if null 出错
     */
    @Nullable
    public static Bitmap clipToSquare(Bitmap src) {
        if (StitcherUtils.isEmptyBitmap(src)) {
            return null;
        }
        int clipSize = Math.min(src.getWidth(), src.getHeight());
        int x = (src.getWidth() - clipSize) / 2;
        int y = (src.getHeight() - clipSize) / 2;
        return clip(src, x, y, clipSize, clipSize);
    }

    /**
     * 裁剪 Bitmap 为圆形
     * modified from https://gist.github.com/jewelzqiu/c0633c9f3089677ecf85
     *
     * @param src 源bitmap
     * @return bitmap if null 出错
     */
    @Nullable
    public static Bitmap clipToCircle(Bitmap src) {
        if (StitcherUtils.isEmptyBitmap(src)) {
            return null;
        }
        int minSize = Math.min(src.getWidth(), src.getHeight());
        Bitmap output = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        final Rect destRect = new Rect(0, 0, src.getWidth(), src.getHeight());

        canvas.drawARGB(0,0,0,0);
        try {
            int radius = minSize / 2;
            canvas.drawCircle(src.getWidth() / 2, src.getHeight() / 2, radius, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(src, null, destRect, paint);
        } catch (Exception e) {
            return null;
        } finally {
            if (!src.isRecycled() && src!=output) {
                src.recycle();
            }
        }
        //因为是 bitmap 大小为原始大小，所以将原始大小裁剪成正方形
        return clipToSquare(output);
    }

    /*
       ---------- clip bitmap area ----------
     */
}
