package com.cxd.android.liveplayer.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.Nullable;

import com.cxd.android.liveplayer.toolkit.cache.MemoryCache;
import com.cxd.android.liveplayer.toolkit.cache.SizeOf;
import com.cxd.android.liveplayer.toolkit.fresco.ImageLoader;
import com.cxd.android.liveplayer.toolkit.rxjava.bus.thread.EventThread;
import com.cxd.android.liveplayer.toolkit.rxjava.executor.Action1;
import com.cxd.android.liveplayer.toolkit.rxjava.executor.RxExecutor;

import org.json.JSONArray;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;


public class Utils {
    private static final String TAG = "Utils";
    public static final String EXT_SDCARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String APP_DATA_PATH = EXT_SDCARD_PATH + "/kascend/chushou/";
    public static final String ONLINE_THUMB_ROOTPATH = APP_DATA_PATH + ".thumbcache/";
    public static final String THUMBNAIL_SURFIX = ".thp";

    public static Context mContext;

    private static final int DRAWABLE_CACHE_SIZE = 15;
    private static MemoryCache<Drawable> mDrawableCache;

    public static void initialize(Context context) {
        context = checkNotNull(context);
        mContext = context.getApplicationContext();
    }

    public static void release() {
//        RichTextHelper.clearDrawableCache();
        if (mDrawableCache != null) {
            mDrawableCache.clear();
            mDrawableCache = null;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Commons
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @param reference object
     * @param <T>       T
     * @return the none null object
     */
    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    /**
     * @param reference object
     * @param message   message of the null pointer exception
     * @param <T>       T
     * @return the none null object
     */
    public static <T> T checkNotNull(T reference, @Nullable Object message) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(message));
        }
        return reference;
    }

    /**
     * @param str string
     * @return whether the string is empty
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty() || str.equals("null");
    }

    /**
     * @param collection collection
     * @return whether the collection is empty
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * @param array json array
     * @return whether the json array is emtpy
     */
    public static boolean isEmpty(JSONArray array) {
        return array == null || array.length() == 0;
    }

    public static boolean isEmptyOrZero(String str) {
        return str == null || str.isEmpty() || str.equals("null") || str.equals("0");
    }

    ///////////////////////////////////////////////////////////////////////////
    // parse
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @param str string
     * @return convert the string to an integer, default 0
     */
    public static int parseInt(String str) {
        int res = 0;
        if (isEmpty(str))
            return res;
        try {
            res = Integer.parseInt(str);
        } catch (NumberFormatException ignored) {
            res = 0;
        }
        return res;
    }

    /**
     * @param str string
     * @return convert the string to long ,default 0
     */
    public static long parseLong(String str) {
        long res = 0;
        if (isEmpty(str))
            return res;
        try {
            res = Long.parseLong(str);
        } catch (NumberFormatException ignored) {
            res = 0;
        }
        return res;
    }

    public static double parseDouble(String str) {
        double result = 0.0;
        if (isEmpty(str)) {
            return result;
        }
        try {
            result = Double.parseDouble(str);
        } catch (NumberFormatException ignored) {
            result = 0.0;
        }
        return result;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Drawable Cache
    ///////////////////////////////////////////////////////////////////////////

    /**
     * clear drawable memory cache
     */
    public static void clearDrawableCache() {
        if (mDrawableCache != null) {
            mDrawableCache.clear();
            mDrawableCache = null;
        }
    }

    /**
     * @param key key
     * @return the related drawable,maybe null
     */
    @Nullable
    public static Drawable getIconMapDrawable(String key) {
        if (mDrawableCache != null) {
            return mDrawableCache.get(key);
        }
        return null;
    }

    /**
     * @param key key
     * @param obj bitmap or drawable
     * @return the related drawable ,maybe  null
     */
    @Nullable
    public static Drawable saveIconMapDrawable(String key, Object obj) {
        if (obj == null) {
            return null;
        }
        Drawable drawable = null;
        try {
            if (obj instanceof Drawable) {
                drawable = (Drawable) obj;
            } else if (obj instanceof Bitmap) {
                drawable = new BitmapDrawable(mContext.getResources(), (Bitmap) obj);
            }
            if (drawable != null) {
                if (mDrawableCache == null) {
                    mDrawableCache = new MemoryCache<>(DRAWABLE_CACHE_SIZE, new SizeOf<Drawable>() {
                        @Override
                        public int sizeOf(String key, Drawable value) {
                            return 1;
                        }
                    });
                }
                mDrawableCache.put(key, drawable);
            }
        } catch (Exception ignored) {
        }

        return drawable;
    }

    ///////////////////////////////////////////////////////////////////////////
    // local storage
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @param url icon图片url
     * @return 缓存在本地的图片地址
     */
    public static String makeLocalPath(String url) {
        if (isEmpty(url)) {
            return "";
        }

        return ONLINE_THUMB_ROOTPATH
                + url.hashCode()
                + THUMBNAIL_SURFIX;
    }

    @Nullable
    public static Drawable downloadImg(final String url) {
        if (isEmpty(url)) {
            return null;
        }

        final String imagepath = isImgExists(url);
        if (isEmpty(imagepath)) {
            if (!isEmpty(url)) {
                RxExecutor.just(EventThread.IO, url, new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Bitmap bitmap = ImageLoader.loadImageSync(s, 0, 0);
                        if (bitmap != null) {
                            saveIconMapDrawable(s, bitmap);
                            ImageUtils.saveBitmaptoPNGFile(bitmap, new File(makeLocalPath(url)));
                        }
                    }
                });
            }
            return null;
        } else {
            Drawable drawable = getIconMapDrawable(url);
            if (null != drawable) {
                return drawable;
            }
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(imagepath);
                if (bitmap != null) {
                    drawable = saveIconMapDrawable(url, bitmap);
                }
            } catch (Exception ignored) {
            }
            return drawable;
        }
    }

    public static void checkDownloadImg(String url) {
        if (Utils.isEmpty(url))
            return;

        final String imagepath = isImgExists(url);

        Bitmap bitmap;
        if (Utils.isEmpty(imagepath)) {
            //本地没有储存
            bitmap = ImageLoader.loadImageSync(url, 0, 0);
            if (bitmap != null) {
                Utils.saveIconMapDrawable(url, bitmap);
                ImageUtils.saveBitmaptoPNGFile(bitmap, new File(makeLocalPath(url)));
            }
        } else {
            File f = new File(imagepath);
            if (f.exists() && f.isFile()) {
                bitmap = BitmapFactory.decodeFile(imagepath);
                if (bitmap != null) {
                    Utils.saveIconMapDrawable(url, bitmap);
                }
            }
        }
    }

    /**
     * @param url icon图片url地址
     * @return 若存在返回path, 若不存在返回""
     */
    private static String isImgExists(String url) {
        if (isEmpty(url)) {
            return "";
        }
        String path = makeLocalPath(url);
        File file = new File(path);
        if (file.isFile() && file.exists()) {
            return path;
        } else {
            KasLog.d(TAG, "image( " + url + " )" + ": not exists");
            return "";
        }
    }

    private static final Field TEXT_LINE_CACHED;

    static {
        Field textLineCached = null;
        try {
            textLineCached = Class.forName("android.text.TextLine").getDeclaredField("sCached");
            textLineCached.setAccessible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        TEXT_LINE_CACHED = textLineCached;
    }

    public static void clearTextLineCache() {
        // If the field was not found for whatever reason just return.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) return;

        if (TEXT_LINE_CACHED == null) return;

        Object cached = null;
        try {
            // Get reference to the TextLine sCached array.
            cached = TEXT_LINE_CACHED.get(null);
        } catch (Exception ex) {
            //
        }
        if (cached != null) {
            // Clear the array.
            for (int i = 0, size = Array.getLength(cached); i < size; i++) {
                Array.set(cached, i, null);
            }
        }
    }
}