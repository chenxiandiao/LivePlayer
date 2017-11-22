//package com.cxd.android.liveplayer.toolkit.richtext;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.graphics.Typeface;
//import android.graphics.drawable.Drawable;
//import android.support.annotation.ColorInt;
//import android.support.annotation.IntRange;
//import android.support.v4.content.ContextCompat;
//import android.text.Spanned;
//import android.text.TextUtils;
//import android.text.style.BackgroundColorSpan;
//import android.text.style.ForegroundColorSpan;
//import android.text.style.StyleSpan;
//import android.text.style.TypefaceSpan;
//import android.text.style.UnderlineSpan;
//import android.util.TypedValue;
//import android.view.View;
//
//import com.cxd.android.liveplayer.R;
//import com.cxd.android.liveplayer.toolkit.cache.WeakReferenceCache;
//import com.cxd.android.liveplayer.utils.KasLog;
//import com.cxd.android.liveplayer.utils.Utils;
//import com.facebook.drawee.backends.pipeline.Fresco;
//import com.facebook.drawee.generic.GenericDraweeHierarchy;
//import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
//import com.facebook.drawee.interfaces.DraweeController;
//import com.facebook.drawee.span.DraweeSpan;
//import com.facebook.drawee.span.SimpleDraweeSpanTextView;
//import com.facebook.drawee.view.DraweeHolder;
//import com.facebook.widget.text.span.BetterImageSpan;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
///**
// * Created by Rancune@126.com on 2017/1/18.
// */
//
//@SuppressWarnings("unused")
//public class RichTextHelper {
//    private static final String TAG = "RichTextHelper";
//    /**
//     * <![JSON[{"type": 1, "content": "带头大哥", "fontColor": "#666666", "fontSizeLevel": 2, "backgroundColor": "#333333", "style": 3}]]>
//     * 送给
//     * <![JSON[{"type": 1, "content": "毁灭", "fontColor": "#666666", "fontSizeLevel": 2, "backgroundColor": "#333333", "style": 3}]]>
//     * 1颗珍珠
//     * <![JSON[{"type": 2, "image": "http://chushou.tv/xx.jpg"}]]>
//     * <![JSON[{"type": 3, "url": "http://chushou.tv", "name": "触手官网"}]]>
//     */
//
//    private static final String START_STR = "<![JSON[";
//    private static final String END_STR = "]]>";
//
//    private static final String COMBO_ICON_KEY = "-1";
//    private static WeakReferenceCache<Drawable> mDrawableCache;
//
//    public static void clearDrawableCache() {
//        if (mDrawableCache != null) {
//            mDrawableCache.clear();
//            mDrawableCache = null;
//        }
//    }
//
//    public static ArrayList<RichText> parseRichText(String content) {
//        ArrayList<RichText> ret = new ArrayList<>();
//        if (Utils.isEmpty(content)) {
//            return ret;
//        }
//
//        // 为了与录制端保持一致，如果content不包含START_STR,END_STR，也将其解析成一个
//        // RichText，只不过该句只有一项，并且类型为RichText.TYPE_NORMAL
////        if (!content.contains(START_STR) || !content.contains(END_STR)) {
////            return ret;
////        }
//
//        String[] arr = content.split(END_STR);
//        for (String temp : arr) {
//            int index = temp.indexOf(START_STR);
//            if (index == -1) {
//                //没有找到START_STR,整串是常规文本
//                RichText ri = new RichText();
//                ri.mType = RichText.TYPE_NORMAL;
//                ri.mContent = temp;
//                ret.add(ri);
//            } else {
//                if (index > 0) {
//                    // 前面有一段是常规文本
//                    String normal = temp.substring(0, index);
//                    RichText ri = new RichText();
//                    ri.mType = RichText.TYPE_NORMAL;
//                    ri.mContent = normal;
//                    ret.add(ri);
//                }
//                String rich = temp.substring(index + START_STR.length());
//                RichText rt = ParseRichJson(rich);
//                if (null != rt) {
//                    ret.add(rt);
//                }
//            }
//        }
//
//        return ret;
//    }
//
//    public static boolean processRichText(Context context, Spanny sp,
//                                          ArrayList<RichText> richTexts, int textsize,
//                                          @ColorInt int defaultTextColor, View view) {
//        return processRichText(context, sp, richTexts, textsize, defaultTextColor, -1, view);
//    }
//
//    public static boolean processRichText(Context context, Spanny sp,
//                                          ArrayList<RichText> richTexts, int textsize,
//                                          @ColorInt int defaultTextColor,
//                                          @IntRange(from = -1, to = 255) int alpha, View view) {
//        if (context == null || sp == null || Utils.isEmpty(richTexts)) {
//            return false;
//        }
//        for (int i = 0; i < richTexts.size(); i++) {
//            RichText rt = richTexts.get(i);
//            if (rt.mType == RichText.TYPE_IMAGE) {
//
//                if(view != null){
//                    int tvHeight = (int) AppUtils.getRawSize(TypedValue.COMPLEX_UNIT_DIP, textsize + 3, context);
//                    int tmpLen = sp.length();
//                    if (view instanceof DraweeTextView) {
//
//                        sp.append("richtext");
//                        DraweeSpan span = new DraweeSpan.Builder(rt.mImage, true)
//                                .setLayout(20, tvHeight, false, view)
//                                .setShowAnimaImmediately(true)
//                                .build();
//                        sp.setSpan(span, tmpLen, sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    }else if(view instanceof SimpleDraweeSpanTextView){
//                        GenericDraweeHierarchy medalHierarchy = new GenericDraweeHierarchyBuilder(context.getResources())
//                                .build();
//                        DraweeHolder<GenericDraweeHierarchy> medalDraweeHolder = DraweeHolder.create(medalHierarchy, mContext);
//                        DraweeController controller = Fresco.newDraweeControllerBuilder()
//                                .setUri(rt.mImage).build();
//                        medalDraweeHolder.setController(controller);
//
//                        sp.append(" ");
//                        sp.setImageSpan(medalDraweeHolder, tmpLen, tmpLen, 20
//                                , tvHeight, true, BetterImageSpan.ALIGN_CENTER);
//                    }
//                }else {
//                    Drawable drawable = Utils.getIconMapDrawable(rt.mImage);
//                    if (null == drawable) {
//                        //如果不存在看看本地，并且下载该图片
//                        drawable = Utils.downloadImg(rt.mImage);
//                    }
//
//                    if (null != drawable) {
//                        //图片已经缓存了就显示，如果没缓存就不显示
//                        Drawable nd = drawable.getConstantState().newDrawable();
//                        int drawableWidth = nd.getIntrinsicWidth();
//                        int drawableHeight = nd.getIntrinsicHeight();
//
//                        int height = (int) AppUtils.getRawSize(TypedValue.COMPLEX_UNIT_DIP, textsize + 3, context);
//                        int width = drawableWidth * height / drawableHeight;
//
//                        nd.setBounds(0, 0, width, height);
//                        sp.append("", new VerticalImageSpan(nd));
//                    }
//                }
//
//            } else if (rt.mType == RichText.TYPE_RICHTEXT) {
//                if (!Utils.isEmpty(rt.mContent)) {
//
//                    ArrayList<Object> formats = new ArrayList<>();
//                    if (!Utils.isEmpty(rt.mBackgroundColor)) {
//                        formats.add(new BackgroundColorSpan(Color.parseColor(appendAlpha(rt.mBackgroundColor, alpha))));
//                    }
//                    if (!Utils.isEmpty(rt.mFontColor)) {
//                        formats.add(new ForegroundColorSpan(Color.parseColor(appendAlpha(rt.mFontColor, alpha))));
//                    }
//
//                    //暂时不支持组合
//                    if (rt.mFontStyle == RichText.FONTSTYLE_BOLD) {
//                        formats.add(new StyleSpan(Typeface.BOLD));
//                    } else if (rt.mFontStyle == RichText.FONTSTYLE_ITALIC) {
//                        formats.add(new TypefaceSpan("monospace"));
//                        formats.add(new StyleSpan(Typeface.ITALIC));
//                    } else if (rt.mFontStyle == RichText.FONTSTYLE_UNDERLINE) {
//                        formats.add(new UnderlineSpan());
//                    } else {
//                        formats.add(new StyleSpan(Typeface.NORMAL));
//                    }
//
//                    if (formats.size() > 0) {
//                        sp.append(rt.mContent, formats);
//                    } else {
//                        sp.append(rt.mContent);
//                    }
//                }
//            } else if (rt.mType == RichText.TYPE_COMBO) {
//                appendCombo(context, sp, rt.mCombo);
//            } else {
//                if (!Utils.isEmpty(rt.mContent)) {
//                    String color = appendAlpha("#" + Integer.toHexString(defaultTextColor), alpha);
//                    sp.append(rt.mContent, new ForegroundColorSpan(Color.parseColor(color)));
//                }
//            }
//        }
//
//        return true;
//    }
//
//    private static RichText ParseRichJson(String content) {
//        try {
//            JSONObject job = new JSONObject(content);
//            RichText rt = new RichText();
//            rt.mType = job.optInt("type");
//            if (rt.mType == RichText.TYPE_IMAGE || rt.mType == RichText.TYPE_RICHTEXT || rt.mType == RichText.TYPE_LINK) {
//                rt.mImage = job.optString("image");
//                rt.mUrl = job.optString("url");
//                rt.mUrlDesc = job.optString("name");
//                rt.mContent = job.optString("content");
//                rt.mFontColor = job.optString("fontColor");
//                rt.mFontSizeLevel = job.optInt("fontSizeLevel");
//                rt.mBackgroundColor = job.optString("backgroundColor");
//                rt.mFontStyle = job.optInt("style");
//            } else {
//                //如果是不认识的type，只解析conent内容，然后当成普通文本处理
//                rt.mType = RichText.TYPE_NORMAL;
//                rt.mContent = job.optString("content");
//            }
//            return rt;
//        } catch (JSONException e) {
//            KasLog.e(TAG, "ParseRichJson error=" + e.toString());
//            return null;
//        }
//    }
//
//    private static void appendCombo(Context mContext, Spanny spanny, int combo) {
//        if (combo <= 1 || spanny == null) {
//            return;
//        }
//
//        String strnum = String.valueOf(combo);
//        boolean bcombo = false;
//        spanny.append(" ");
//        for (int j = 0; j < strnum.length(); j++) {
//            String sub = strnum.substring(j, j + 1);
//
//            Drawable d = getDrawableFromCache(sub);
//            if (d == null) {
//                int id = mContext.getResources().getIdentifier("zues_combo_" + sub, "drawable",
//                        mContext.getPackageName());
//                if (id > 0) {
//                    d = ContextCompat.getDrawable(mContext, id);
//                    int drawableWidth = d.getIntrinsicWidth();
//                    int drawableHeight = d.getIntrinsicHeight();
//                    d.setBounds(0, 0, drawableWidth, drawableHeight);
//                    saveDrawableToCache(sub, d);
//                }
//            }
//
//            if (d != null) {
//                bcombo = true;
//                spanny.append("", new VerticalImageSpan(d));
//            }
//        }
//        if (bcombo) {
//            Drawable d = getDrawableFromCache(COMBO_ICON_KEY);
//            if (null == d) {
//                d = ContextCompat.getDrawable(mContext, R.drawable.zues_combo);
//                int drawableWidth = d.getIntrinsicWidth();
//                int drawableHeight = d.getIntrinsicHeight();
//                d.setBounds(0, 0, drawableWidth, drawableHeight);
//                saveDrawableToCache(COMBO_ICON_KEY, d);
//            }
//            spanny.append("", new VerticalImageSpan(d));
//        }
//    }
//
//    private static Drawable getDrawableFromCache(String key) {
//        if (mDrawableCache == null) {
//            return null;
//        }
//
//        return mDrawableCache.get(key);
//    }
//
//    private static void saveDrawableToCache(String key, Drawable value) {
//        if (Utils.isEmpty(key) || value == null) {
//            return;
//        }
//        if (mDrawableCache == null) {
//            // 0~9 再加一个 连击 的符号共11个
//            mDrawableCache = new WeakReferenceCache<>(11);
//        }
//        mDrawableCache.put(key, value);
//    }
//
//    private static String appendAlpha(String color, @IntRange(from = 0, to = 255) int alpha) {
//        if (TextUtils.isEmpty(color) || color.length() > 7 || alpha < 0) {
//            return color;
//        }
//        String hex = Integer.toHexString(alpha);
//        return color.replace("#", "#" + hex);
//    }
//
//
//    private static StringBuilder initRichText(StringBuilder sb, String content, String color) {
//        sb.append("<![JSON[{\"type\": 1, \"content\": \"").append(content).append("\", \"fontColor\":\"")
//                .append(color).append("\"}]]>");
//        return sb;
//    }
//
//    /**
//     * 处理特殊权限用户自己发的弹幕信息
//     *
//     */
//    public static String createMyRichText(String content, List<String> colors, boolean wholeColor, int colorIndex) {
//        if (colors == null)
//            return content;
//        if (colors.size() == 0)
//            return content;
//        StringBuilder sb = new StringBuilder();
//        if (wholeColor) {
//            initRichText(sb, content, colors.get(colorIndex));
//        } else {
//            int size = colors.size();
//            for (int i = 0; i < content.length(); i++) {
//                int charColor = (colorIndex + i) % size;
//                initRichText(sb, String.valueOf(content.charAt(i)), colors.get(charColor));
//            }
//        }
//        return sb.toString();
//    }
//}
