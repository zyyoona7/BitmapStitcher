package com.zyyoona7.demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PathUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zyyoona7.stitcher.BitmapStitcher;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_MULTI = 1001;
    private static final int REQUEST_CODE_SINGLE = 1002;
    private static final int REQUEST_CODE_HOR_SINGLE = 1003;
    private static final int REQUEST_CODE_HOR_MULTI = 1004;

    private ImageView mPictureIv;
    private Group mLoadingGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button multiBtn = findViewById(R.id.btn_multi);
        Button horMultiBtn = findViewById(R.id.btn_hor_multi);
        Button singleBtn = findViewById(R.id.btn_single);
        Button horSingleBtn = findViewById(R.id.btn_hor_single);
        mPictureIv = findViewById(R.id.iv_picture);
        mLoadingGroup = findViewById(R.id.group_loading);

        multiBtn.setOnClickListener(v -> {
            if (AndPermission.hasPermissions(MainActivity.this, Permission.WRITE_EXTERNAL_STORAGE)) {
                choosePicture(9, REQUEST_CODE_MULTI);

            } else {
                AndPermission.with(MainActivity.this)
                        .runtime()
                        .permission(Permission.WRITE_EXTERNAL_STORAGE)
                        .onGranted(data -> choosePicture(9, REQUEST_CODE_MULTI))
                        .start();
            }
        });

        horMultiBtn.setOnClickListener(v -> {
            if (AndPermission.hasPermissions(MainActivity.this, Permission.WRITE_EXTERNAL_STORAGE)) {
                choosePicture(9, REQUEST_CODE_HOR_MULTI);

            } else {
                AndPermission.with(MainActivity.this)
                        .runtime()
                        .permission(Permission.WRITE_EXTERNAL_STORAGE)
                        .onGranted(data -> choosePicture(9, REQUEST_CODE_HOR_MULTI))
                        .start();
            }
        });

        singleBtn.setOnClickListener(v -> {
            if (AndPermission.hasPermissions(MainActivity.this, Permission.WRITE_EXTERNAL_STORAGE)) {
                choosePicture(1, REQUEST_CODE_SINGLE);
            } else {
                AndPermission.with(MainActivity.this)
                        .runtime()
                        .permission(Permission.WRITE_EXTERNAL_STORAGE)
                        .onGranted(data -> choosePicture(1, REQUEST_CODE_SINGLE))
                        .start();
            }
        });

        horSingleBtn.setOnClickListener(v -> {
            if (AndPermission.hasPermissions(MainActivity.this, Permission.WRITE_EXTERNAL_STORAGE)) {
                choosePicture(1, REQUEST_CODE_HOR_SINGLE);
            } else {
                AndPermission.with(MainActivity.this)
                        .runtime()
                        .permission(Permission.WRITE_EXTERNAL_STORAGE)
                        .onGranted(data -> choosePicture(1, REQUEST_CODE_HOR_SINGLE))
                        .start();
            }
        });

    }

    private void choosePicture(int maxSelectable, int requestCode) {
        Matisse.from(this)
                .choose(MimeType.ofImage())
                .showSingleMediaType(true)
                .imageEngine(new Glide4Engine())
                .maxSelectable(maxSelectable)
                .forResult(requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_CODE_SINGLE) {
                //垂直单张图
                List<String> pathList = Matisse.obtainPathResult(data);
                if (pathList != null && pathList.size() > 0) {
                    String filePath = pathList.get(0);
                    stitchVerticalSingle(filePath, false);
                }
            } else if (requestCode == REQUEST_CODE_HOR_SINGLE) {
                //水平单张图
                List<String> pathList = Matisse.obtainPathResult(data);
                if (pathList != null && pathList.size() > 0) {
                    String filePath = pathList.get(0);
                    stitchHorizontalSingle(filePath);
                }
            } else if (requestCode == REQUEST_CODE_MULTI) {
                //垂直多图
                List<String> pathList = Matisse.obtainPathResult(data);
                if (pathList.size() > 0) {
                    stitchVerticalMulti(pathList, false);
                }
            }else if (requestCode==REQUEST_CODE_HOR_MULTI){
                //水平多图
                List<String> pathList = Matisse.obtainPathResult(data);
                if (pathList.size() > 0) {
                    stitchHorizontalMulti(pathList);
                }
            }
        }
    }

    private void stitchVerticalMulti(List<String> pathList, boolean isClip) {
        mLoadingGroup.setVisibility(View.VISIBLE);
        String outputPath = PathUtils.getExternalDownloadsPath() + "/" + "bitmap_stitcher_ver_multi.png";
        Glide.with(MainActivity.this)
                .clear(mPictureIv);

        AppExecutors.runOnDiskIOPostUI(() -> {
            LogUtils.d("stitchVerticalMulti start...");
            Bitmap bitmap = BitmapStitcher.stitchVertical(pathList, BitmapStitcher.SCALE_SMALLER,
                    ConvertUtils.dp2px(5), Color.RED);

            Bitmap clipBitmap = isClip ? BitmapStitcher.clipYFromCenter(bitmap, 1920) : bitmap;

            FileUtils.delete(outputPath);
            LogUtils.d("stitchVerticalMulti bitmap success");
            BitmapStitcher.save(clipBitmap, outputPath, Bitmap.CompressFormat.JPEG, 50);
            LogUtils.d("stitchVerticalMulti success...");
        }, () -> {
            mLoadingGroup.setVisibility(View.GONE);
            Glide.with(MainActivity.this)
                    .asDrawable()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .load(new File(outputPath))
                    .into(mPictureIv);
        });
    }

    private void stitchHorizontalMulti(List<String> pathList) {
        mLoadingGroup.setVisibility(View.VISIBLE);
        String outputPath = PathUtils.getExternalDownloadsPath() + "/" + "bitmap_stitcher_hor_multi.png";
        Glide.with(MainActivity.this)
                .clear(mPictureIv);

        AppExecutors.runOnDiskIOPostUI(() -> {
            LogUtils.d("stitchHorizontalMulti start...");
            Bitmap bitmap = BitmapStitcher.stitchHorizontal(pathList, 1920,
                    ConvertUtils.dp2px(20), Color.WHITE);
            FileUtils.delete(outputPath);
            LogUtils.d("stitchHorizontalMulti bitmap success");
            BitmapStitcher.save(bitmap, outputPath, Bitmap.CompressFormat.PNG, 100);
            LogUtils.d("stitchHorizontalMulti success...");
        }, () -> {
            mLoadingGroup.setVisibility(View.GONE);
            Glide.with(MainActivity.this)
                    .asDrawable()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .load(new File(outputPath))
                    .into(mPictureIv);
        });
    }

    private void stitchVerticalSingle(String filePath, boolean isClip) {
        mLoadingGroup.setVisibility(View.VISIBLE);
        String outputPath = PathUtils.getExternalDownloadsPath() + "/" + "bitmap_stitcher_ver_sigle.png";
        Glide.with(MainActivity.this)
                .clear(mPictureIv);
        AppExecutors.runOnDiskIOPostUI(() -> {
            LogUtils.d("stitchVerticalSingle start...");
            Bitmap bitmap = BitmapStitcher.stitchVertical(filePath, 3, 720,
                    ConvertUtils.dp2px(10), Color.CYAN);

            Bitmap clipBitmap = isClip ? BitmapStitcher.clipToCircle(bitmap) : bitmap;

            FileUtils.delete(outputPath);
            LogUtils.d("stitchVerticalSingle bitmap success");
            BitmapStitcher.save(clipBitmap, outputPath, Bitmap.CompressFormat.PNG, 100);
            LogUtils.d("stitchVerticalSingle success...");
        }, () -> {
            mLoadingGroup.setVisibility(View.GONE);
            Glide.with(MainActivity.this)
                    .asDrawable()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .load(new File(outputPath))
                    .into(mPictureIv);
        });
    }

    private void stitchHorizontalSingle(String filePath) {
        mLoadingGroup.setVisibility(View.VISIBLE);
        String outputPath = PathUtils.getExternalDownloadsPath() + "/" + "bitmap_stitcher_hor_sigle.png";
        Glide.with(MainActivity.this)
                .clear(mPictureIv);
        AppExecutors.runOnDiskIOPostUI(() -> {
            LogUtils.d("stitchHorizontalSingle start...");
            Bitmap bitmap = BitmapStitcher.stitchHorizontal(filePath, 3, 1920,
                    ConvertUtils.dp2px(15), Color.BLACK);
            FileUtils.delete(outputPath);
            LogUtils.d("stitchHorizontalSingle bitmap success");
            BitmapStitcher.save(bitmap, outputPath, Bitmap.CompressFormat.PNG, 100);
            LogUtils.d("stitchHorizontalSingle success...");
        }, () -> {
            mLoadingGroup.setVisibility(View.GONE);
            Glide.with(MainActivity.this)
                    .asDrawable()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .load(new File(outputPath))
                    .into(mPictureIv);
        });
    }

}
