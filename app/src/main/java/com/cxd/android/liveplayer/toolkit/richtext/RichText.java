package com.cxd.android.liveplayer.toolkit.richtext;


import java.io.Serializable;

public class RichText implements Serializable {
    private static final long serialVersionUID = -601848252661601162L;

    // 普通文字
    public static final int TYPE_NORMAL = -1;
    // richtext
    public static final int TYPE_RICHTEXT = 1;
    // image
    public static final int TYPE_IMAGE = 2;
    // link
    public static final int TYPE_LINK = 3;
    // combo, 本地定义的数字，用于录制端
    public static final int TYPE_COMBO = 4;

    // 001
    public static final int FONTSTYLE_BOLD = 1;
    // 010
    public static final int FONTSTYLE_ITALIC = 2;
    // 100
    public static final int FONTSTYLE_UNDERLINE = 4;


    // 字体颜色
    public String mFontColor;
    // 字体大小等级，非具体字体大小数值
    public int mFontSizeLevel;
    // 字体背景色
    public String mBackgroundColor;
    // 字体样式，粗体斜体等
    public int mFontStyle = 0;
    public String mContent;
    public String mImage;
    public String mUrlDesc;
    public String mUrl;
    public int mType;
    public int mCombo;

    @Override
    public String toString() {
        return "type=" + mType +
                " content=" + mContent +
                " fontcolor=" + mFontColor +
                " fontsizelevel=" + mFontSizeLevel +
                " background=" + mBackgroundColor +
                " fontstyle=" + mFontStyle +
                " mimage=" + mImage +
                " urldesc=" + mUrlDesc +
                " url=" + mUrl +
                " combo=" + mCombo;
    }


}