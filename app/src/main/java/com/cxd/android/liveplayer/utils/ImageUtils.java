package com.cxd.android.liveplayer.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by Rancune@126.com on 2016/10/14.
 */

@SuppressWarnings({"unused", "WeakerAccess"})
public class ImageUtils {
    private static final String TAG = "ImageUtils";

    private ImageUtils() {
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty() || str.equals("null");
    }

    /**
     * @param color original color
     * @param alpha alpha
     * @return adjust alpha of the original color
     */
    public static int adjustAlpha(@ColorInt int color, @IntRange(from = 0, to = 255) int alpha) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return Color.argb(alpha, r, g, b);
    }

    /**
     * sdk中的BitmapFactory.Options 计算方法
     */
    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * @param path      image local path
     * @param maxWidth  max width
     * @param maxHeight max height
     * @return a bitmap decoded from the file, or null
     */
    @Nullable
    public static Bitmap decodeBitmapFromFile(String path, int maxWidth, int maxHeight) {
        if (isEmpty(path)) {
            return null;
        }
        File file = new File(path);
        if (!file.exists() || file.isDirectory()) {
            return null;
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            if (maxWidth > 0 && maxHeight > 0) {
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, options);
                int orgw = options.outWidth;
                int orgh = options.outHeight;
                int maxNumOfPixels;
                if (orgw * orgh > maxWidth * maxHeight) {
                    maxNumOfPixels = maxWidth * maxHeight;
                    options.inSampleSize = computeSampleSize(options, -1, maxNumOfPixels);
                }
            }
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(path);
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * @param bitmap the bitmap to saved
     * @param file   the file which the bitmap will be saved
     * @return whether success
     */
    public static boolean saveBitmaptoPNGFile(Bitmap bitmap, File file) {
        if (bitmap == null || file == null) {
            return false;
        }
        if (file.exists() && !file.delete()) {
            return false;
        }
        File parentFile = file.getParentFile();
        if (parentFile == null || (!parentFile.exists() && !parentFile.mkdirs())) {
            return false;
        }
        OutputStream out = null;
        boolean success = false;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 75, out);
            out.flush();
            success = true;
        } catch (Exception e) {
            success = false;
        } finally {
            IOUtils.closeQuietly(out);
        }
        return success;
    }

    @Nullable
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }
        Bitmap output = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }

}
