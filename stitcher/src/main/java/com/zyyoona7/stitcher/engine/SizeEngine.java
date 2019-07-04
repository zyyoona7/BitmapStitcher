package com.zyyoona7.stitcher.engine;

import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.zyyoona7.stitcher.size.StitchSize;
import com.zyyoona7.stitcher.util.StitcherUtils;

import java.util.List;

public class SizeEngine {

    //缩放策略
    //如果 宽/高 小于最大 宽/高 尺寸，则缩放至最大 宽/高 尺寸
    public static final int SCALE_LARGER = 0;
    //如果 宽/高 大于最小 宽/高 尺寸，则缩放至最小 宽/高 尺寸
    public static final int SCALE_SMALLER = -1;

    private SizeEngine() {
    }

    /**
     * 计算垂直方向排列多张图片拼接的尺寸
     *
     * @param pathList        path list
     * @param destWidth       目标宽度 0 根据 SCALE_LARGER 策略缩放
     *                        -1 根据 SCALE_SMALLER 策略缩放 >0 则保持比例缩放到指定宽度度
     * @param verticalSpacing 垂直间距
     * @return 测量后的总宽高
     */
    @NonNull
    public static StitchSize calculateVerticalSize(List<String> pathList, int destWidth, int verticalSpacing) {

        if (pathList == null || pathList.size() == 0) {
            return new StitchSize(0, 0);
        }

        //图片列表中宽度最大值和宽度最小值
        int maxWidth = -1;
        int minWidth = -1;

        if (destWidth <= SCALE_LARGER) {
            //找出 最大宽度和最小宽度
            try {
                for (String filePath : pathList) {
                    BitmapFactory.Options options = StitcherUtils.decodeBitmapBounds(filePath);
                    if (maxWidth == -1) {
                        maxWidth = options.outWidth;
                    } else if (options.outWidth > maxWidth) {
                        maxWidth = options.outWidth;
                    }

                    if (minWidth == -1) {
                        minWidth = options.outWidth;
                    } else if (options.outWidth < minWidth) {
                        minWidth = options.outWidth;
                    }
                }
            } catch (Exception e) {
                return new StitchSize(0, 0);
            }
        }

        int totalHeight = 0;

        try {
            for (String filePath : pathList) {
                BitmapFactory.Options options = StitcherUtils.decodeBitmapBounds(filePath);
                int width = options.outWidth;
                int height = options.outHeight;
                float ratio = height * 1f / width;

                if (destWidth > 0) {
                    width = destWidth;
                    height = StitcherUtils.roundFloatToInt(width * ratio);
                } else if (destWidth == SCALE_LARGER) {
                    //缩放到最大宽度 比例
                    if (width < maxWidth) {
                        width = maxWidth;
                        height = StitcherUtils.roundFloatToInt(width * ratio);
                    }
                } else if (width > minWidth) {
                    //缩放到最小宽度 比例
                    width = minWidth;
                    height = StitcherUtils.roundFloatToInt(width * ratio);
                }

                totalHeight += height;
            }
        } catch (Exception e) {
            return new StitchSize(0, 0);
        }

        //加上间距
        if (verticalSpacing > 0) {
            totalHeight += verticalSpacing * (pathList.size() - 1);
        }

        int totalWidth = destWidth;
        if (destWidth == SCALE_LARGER) {
            totalWidth = maxWidth;
        } else if (destWidth < 0) {
            totalWidth = minWidth;
        }
        return new StitchSize(totalWidth, totalHeight);
    }

    /**
     * 计算垂直方向排列单张图片多次拼接尺寸
     *
     * @param filePath        文件路径
     * @param stitchCount     拼接次数
     * @param destWidth       目标宽度 >0 保持比例缩放到指定宽度 其他情况保存图片尺寸
     * @param verticalSpacing 间距尺寸
     * @return 测量后的总宽高
     */
    public static StitchSize calculateVerticalSize(String filePath, int stitchCount,
                                                   int destWidth, int verticalSpacing) {
        if (stitchCount <= 0) {
            return new StitchSize(0, 0);
        }
        try {
            BitmapFactory.Options options = StitcherUtils.decodeBitmapBounds(filePath);
            int width = options.outWidth;
            int height = options.outHeight;
            float ratio = height * 1f / width;

            if (destWidth > 0) {
                width = destWidth;
                height = StitcherUtils.roundFloatToInt(width * ratio);
            }

            int totalHeight = height * stitchCount + verticalSpacing * (stitchCount - 1);
            return new StitchSize(destWidth > 0 ? destWidth : width, totalHeight);
        } catch (Exception e) {
            return new StitchSize(0, 0);
        }
    }

    /**
     * 计算水平方向排列多张图片拼接的尺寸
     *
     * @param pathList          path list
     * @param destHeight        目标高度 0 根据 SCALE_LARGER 策略缩放
     *                          -1 根据 SCALE_SMALLER 策略缩放 >0 则保持比例缩放到指定高度
     * @param horizontalSpacing 水平间距
     * @return 测量后的总宽高
     */
    @NonNull
    public static StitchSize calculateHorizontalSize(List<String> pathList, int destHeight, int horizontalSpacing) {

        if (pathList == null || pathList.size() == 0) {
            return new StitchSize(0, 0);
        }

        //图片列表中高度度最大值和宽度最小值
        int maxHeight = -1;
        int minHeight = -1;

        if (destHeight <= SCALE_LARGER) {
            //找出 最大高度和最小高度
            try {
                for (String filePath : pathList) {
                    BitmapFactory.Options options = StitcherUtils.decodeBitmapBounds(filePath);
                    if (maxHeight == -1) {
                        maxHeight = options.outHeight;
                    } else if (options.outHeight > maxHeight) {
                        maxHeight = options.outHeight;
                    }

                    if (minHeight == -1) {
                        minHeight = options.outHeight;
                    } else if (options.outHeight < minHeight) {
                        minHeight = options.outHeight;
                    }
                }
            } catch (Exception e) {
                return new StitchSize(0, 0);
            }
        }

        int totalWidth = 0;

        try {
            for (String filePath : pathList) {
                BitmapFactory.Options options = StitcherUtils.decodeBitmapBounds(filePath);
                int width = options.outWidth;
                int height = options.outHeight;
                float ratio = width * 1f / height;

                if (destHeight > 0) {
                    height = destHeight;
                    width = StitcherUtils.roundFloatToInt(height * ratio);
                } else if (destHeight == SCALE_LARGER) {
                    //缩放到最大宽度 比例
                    if (height < maxHeight) {
                        height = maxHeight;
                        width = StitcherUtils.roundFloatToInt(height * ratio);
                    }
                } else if (height > minHeight) {
                    //缩放到最小宽度 比例
                    height = minHeight;
                    width = StitcherUtils.roundFloatToInt(height * ratio);
                }

                totalWidth += width;
            }
        } catch (Exception e) {
            return new StitchSize(0, 0);
        }

        //加上间距
        if (horizontalSpacing > 0) {
            totalWidth += horizontalSpacing * (pathList.size() - 1);
        }

        int totalHeight = destHeight;
        if (destHeight == SCALE_LARGER) {
            totalHeight = maxHeight;
        } else if (destHeight < 0) {
            totalHeight = minHeight;
        }
        return new StitchSize(totalWidth, totalHeight);
    }

    /**
     * 计算水平方向排列单张图片多次拼接的尺寸
     *
     * @param filePath          文件路径
     * @param stitchCount       拼接次数
     * @param destHeight        目标高度 >0 保持比例缩放到指定高度 其他保持原比例
     * @param horizontalSpacing 水平间距
     * @return 测量后的总宽高
     */
    public static StitchSize calculateHorizontalSize(String filePath, int stitchCount,
                                                     int destHeight, int horizontalSpacing) {
        if (stitchCount <= 0) {
            return new StitchSize(0, 0);
        }

        try {
            BitmapFactory.Options options = StitcherUtils.decodeBitmapBounds(filePath);
            int width = options.outWidth;
            int height = options.outHeight;
            float ratio = width * 1f / height;

            if (destHeight > 0) {
                height = destHeight;
                width = StitcherUtils.roundFloatToInt(height * ratio);
            }
            int totalWidth = width * stitchCount + horizontalSpacing * (stitchCount - 1);
            return new StitchSize(totalWidth, destHeight > 0 ? destHeight : height);
        } catch (Exception e) {
            return new StitchSize(0, 0);
        }
    }
}
