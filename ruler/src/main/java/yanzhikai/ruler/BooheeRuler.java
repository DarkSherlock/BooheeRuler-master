package yanzhikai.ruler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import yanzhikai.ruler.InnerRulers.BottomHeadRuler;
import yanzhikai.ruler.InnerRulers.InnerRuler;
import yanzhikai.ruler.InnerRulers.LeftHeadRuler;
import yanzhikai.ruler.InnerRulers.RightHeadRuler;
import yanzhikai.ruler.InnerRulers.TopHeadRuler;
import yanzhikai.ruler.cursor.CursorFacatory;
import yanzhikai.ruler.cursor.ICursor;

/**
 * 用于包着尺子的外壳，用于画选取光标、外壳
 */

public class BooheeRuler extends ViewGroup {
    private final String TAG = "ruler";
    private Context mContext;
    //尺子Style定义
    public static final int TOP_HEAD = 1 , BOTTOM_HEAD = 2, LEFT_HEAD = 3, RIGHT_HEAD = 4;
    @IntDef({TOP_HEAD, BOTTOM_HEAD, LEFT_HEAD, RIGHT_HEAD})
    @Retention(RetentionPolicy.SOURCE)
    public  @interface RulerStyle {}
    private @BooheeRuler.RulerStyle int mStyle = TOP_HEAD;
    //内部的尺子
    private InnerRuler mInnerRuler;
    //最小最大刻度值(以0.1kg为单位)
    private int mMinScale = 464, mMaxScale = 2000;
    //大小刻度的长度
    private int mSmallScaleLength = 30, mBigScaleLength = 60;
    //大小刻度的粗细
    private int mSmallScaleWidth = 3, mBigScaleWidth = 5;
    //数字字体大小
    private int mTextSize = 28;
    //数字Text距离顶部高度
    private int mTextMarginHead = 120;
    //数字Text距离刻度线的距离
    private int mTextMarginLine = 20;

    //刻度间隔
    private int mInterval = 18;
    //数字Text颜色
    private
    @ColorInt
    int mTextColor = getResources().getColor(R.color.colorLightBlack);
    //刻度颜色
    private
    @ColorInt
    int mScaleColor = getResources().getColor(R.color.colorGray);
    //初始的当前刻度
    private float mCurrentScale = 0;
    //一格大刻度多少格小刻度
    private int mCount = 10;
    //光标drawable
    private Drawable mCursorDrawable;
    //尺子两端的padding
    private int mPaddingStartAndEnd = 0;
    private int mPaddingLeft = 0,mPaddingTop = 0,mPaddingRight = 0,mPaddingBottom = 0;
    //尺子背景
    private Drawable mRulerBackGround;
    private int mRulerBackGroundColor = getResources().getColor(R.color.colorDirtyWithe);
    //是否启用边缘效应
    private boolean mCanEdgeEffect = true;
    //边缘颜色
    private @ColorInt int mEdgeColor  = getResources().getColor(R.color.colorForgiven);
    //刻度乘积因子
    private float mFactor = 0.1f;

    //光标
    protected ICursor mCursor;
    //是否使用自己定义的光标
    private boolean isUseCustomCursor = false;

    //TriangleCursor的三角形宽
    private int mTriangleWidth;
    //TriangleCursor的三角形高
    private int mTriangleHeight;

    //显示模式
    protected int mDisplayMode;
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {DisplayMode.MODE_SCALE_TEXT_TOP, DisplayMode.MODE_SCALE_TEXT_BOTTOM})
    public  @interface DisplayMode {
        int MODE_SCALE_TEXT_TOP = 0;
        int MODE_SCALE_TEXT_BOTTOM = 1;
    }

    private boolean isDrawOutLine = true;

    public BooheeRuler(Context context) {
        super(context);
        initRuler(context);

    }

    public BooheeRuler(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initRuler(context);

    }

    public BooheeRuler(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initRuler(context);

    }

    @SuppressWarnings("WrongConstant")
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BooheeRuler, 0, 0);
        mMinScale = typedArray.getInteger(R.styleable.BooheeRuler_minScale, mMinScale);
        mMaxScale = typedArray.getInteger(R.styleable.BooheeRuler_maxScale, mMaxScale);
        mSmallScaleWidth = typedArray.getDimensionPixelSize(R.styleable.BooheeRuler_smallScaleWidth, mSmallScaleWidth);
        mSmallScaleLength = typedArray.getDimensionPixelSize(R.styleable.BooheeRuler_smallScaleLength, mSmallScaleLength);
        mBigScaleWidth = typedArray.getDimensionPixelSize(R.styleable.BooheeRuler_bigScaleWidth, mBigScaleWidth);
        mTriangleWidth = typedArray.getDimensionPixelSize(R.styleable.BooheeRuler_triangleWidth, 10);
        mTriangleHeight = typedArray.getDimensionPixelSize(R.styleable.BooheeRuler_triangleHeight, 10);
        mBigScaleLength = typedArray.getDimensionPixelSize(R.styleable.BooheeRuler_bigScaleLength, mBigScaleLength);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.BooheeRuler_numberTextSize, mTextSize);
        mTextMarginHead = typedArray.getDimensionPixelSize(R.styleable.BooheeRuler_textMarginHead, mTextMarginHead);
        mTextMarginLine = typedArray.getDimensionPixelSize(R.styleable.BooheeRuler_textMarginLine, mTextMarginLine);
        mInterval = typedArray.getDimensionPixelSize(R.styleable.BooheeRuler_scaleInterval, mInterval);
        mTextColor = typedArray.getColor(R.styleable.BooheeRuler_numberTextColor, mTextColor);
        mScaleColor = typedArray.getColor(R.styleable.BooheeRuler_scaleColor, mScaleColor);
        mCurrentScale = typedArray.getFloat(R.styleable.BooheeRuler_currentScale, (mMaxScale + mMinScale) / 2);
        mCount = typedArray.getInt(R.styleable.BooheeRuler_count, mCount);
        mCursorDrawable = typedArray.getDrawable(R.styleable.BooheeRuler_cursorDrawable);
        if (mCursorDrawable == null) {
            mCursorDrawable = getResources().getDrawable(R.drawable.cursor_shape);
        }
        mPaddingStartAndEnd = typedArray.getDimensionPixelSize(R.styleable.BooheeRuler_paddingStartAndEnd, mPaddingStartAndEnd);
        mStyle = typedArray.getInt(R.styleable.BooheeRuler_rulerStyle,mStyle);
        mRulerBackGround = typedArray.getDrawable(R.styleable.BooheeRuler_rulerBackGround);
        if (mRulerBackGround == null){
            mRulerBackGroundColor = typedArray.getColor(R.styleable.BooheeRuler_rulerBackGround,mRulerBackGroundColor);
        }
        mCanEdgeEffect = typedArray.getBoolean(R.styleable.BooheeRuler_canEdgeEffect,mCanEdgeEffect);
        mEdgeColor = typedArray.getColor(R.styleable.BooheeRuler_edgeColor,mEdgeColor);
        mFactor = typedArray.getFloat(R.styleable.BooheeRuler_factor,mFactor);
        mDisplayMode = typedArray.getInt(R.styleable.BooheeRuler_scaleTextMode,DisplayMode.MODE_SCALE_TEXT_BOTTOM);

        isDrawOutLine = typedArray.getBoolean(R.styleable.BooheeRuler_drawOutLine,true);
        typedArray.recycle();
    }

    private void initRuler(Context context) {
        mContext = context;
        switch (mStyle){
            case TOP_HEAD:
                mInnerRuler = new TopHeadRuler(context, this);
                paddingHorizontal();
                break;
            case BOTTOM_HEAD:
                mInnerRuler = new BottomHeadRuler(context, this);
                paddingHorizontal();
                break;
            case LEFT_HEAD:
                mInnerRuler = new LeftHeadRuler(context, this);
                paddingVertical();
                break;
            case RIGHT_HEAD:
                mInnerRuler = new RightHeadRuler(context, this);
                paddingVertical();
                break;
        }

        //设置全屏，加入InnerRuler
        mInnerRuler.setMode(mDisplayMode);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mInnerRuler.setLayoutParams(layoutParams);
        addView(mInnerRuler);
        //设置ViewGroup可画
        setWillNotDraw(false);

        initDrawable();
        initRulerBackground();
    }

    private void initRulerBackground(){
        if (mRulerBackGround != null){
            mInnerRuler.setBackground(mRulerBackGround);
        }else {
            mInnerRuler.setBackgroundColor(mRulerBackGroundColor);
        }
    }

    //在宽高初始化之后定义光标Drawable的边界
    private void initDrawable() {
        if (!isUseCustomCursor || mCursor == null){
            mCursor = CursorFacatory.generateCursor(mDisplayMode);
        }
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                if (mCursor!=null){
                    mCursor.onPreDraw(mCursorDrawable,mStyle,getWidth(),getHeight(),mInnerRuler);
                }

                return false;
            }
        });

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //画中间的选定光标，要在这里画，因为dispatchDraw()执行在onDraw()后面，这样子光标才能不被尺子的刻度遮蔽
        if (mCursor!=null){
            mCursor.onDrawCursor(canvas,mCursorDrawable,mStyle,mDisplayMode);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            mInnerRuler.layout(mPaddingLeft, mPaddingTop, r - l - mPaddingRight, b - t - mPaddingBottom);
        }
    }

    private void paddingHorizontal(){
        mPaddingLeft = mPaddingStartAndEnd;
        mPaddingRight = mPaddingStartAndEnd;
        mPaddingTop = 0;
        mPaddingBottom = 0;
    }

    private void paddingVertical(){
        mPaddingTop = mPaddingStartAndEnd;
        mPaddingBottom = mPaddingStartAndEnd;
        mPaddingLeft = 0;
        mPaddingRight = 0;
    }

    //设置回调
    public void setCallback(RulerCallback rulerCallback) {
        mInnerRuler.setRulerCallback(rulerCallback);

    }

    //设置当前进度
    public void setCurrentScale(float currentScale) {
        mCurrentScale = currentScale;
        mInnerRuler.setCurrentScale(currentScale);
    }

    //如果控件尺寸变化，中间光标的位置也要重新定义
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initDrawable();
    }

    public void refreshRuler(){
        initDrawable();
        mInnerRuler.init(mContext);
        mInnerRuler.refreshSize();
    }


    public int getEdgeColor() {
        return mEdgeColor;
    }

    //设置能否使用边缘效果
    public void setCanEdgeEffect(boolean canEdgeEffect) {
        this.mCanEdgeEffect = canEdgeEffect;
    }

    public float getFactor() {
        return mFactor;
    }

    public void setFactor(float factor) {
        this.mFactor = factor;
        mInnerRuler.postInvalidate();
    }

    public void setDisplayMode(@DisplayMode int displayMode) {
        mDisplayMode = displayMode;
        if (mInnerRuler!=null){
            mInnerRuler.setMode(displayMode);
        }
        refreshRuler();
        invalidate();
    }

    public void setCursor(@Nullable ICursor cursor){
        if (cursor == null){
            isUseCustomCursor = false;
            mCursor = null;
        }else {
            mCursor = cursor;
            isUseCustomCursor = true;
        }
        refreshRuler();
        invalidate();
    }

    public boolean canEdgeEffect(){
        return mCanEdgeEffect;
    }

    public float getCurrentScale() {
        return mCurrentScale;
    }

    public void setMinScale(int minScale) {
        this.mMinScale = minScale;
    }

    public int getMinScale() {
        return mMinScale;
    }

    public void setMaxScale(int maxScale) {
        this.mMaxScale = maxScale;
    }

    public int getMaxScale() {
        return mMaxScale;
    }


    public int getCursorWidth() {
        return mCursorDrawable.getIntrinsicWidth();
    }


    public int getCursorHeight() {
        return mCursorDrawable.getIntrinsicHeight();
    }


    public void setBigScaleLength(int bigScaleLength) {
        this.mBigScaleLength = bigScaleLength;
    }

    public int getBigScaleLength() {
        return mBigScaleLength;
    }

    public void setBigScaleWidth(int bigScaleWidth) {
        this.mBigScaleWidth = bigScaleWidth;
    }

    public int getBigScaleWidth() {
        return mBigScaleWidth;
    }

    public void setSmallScaleLength(int smallScaleLength) {
        this.mSmallScaleLength = smallScaleLength;
    }

    public int getSmallScaleLength() {
        return mSmallScaleLength;
    }

    public void setSmallScaleWidth(int smallScaleWidth) {
        this.mSmallScaleWidth = smallScaleWidth;
    }

    public int getSmallScaleWidth() {
        return mSmallScaleWidth;
    }

    public void setTextMarginTop(int textMarginTop) {
        this.mTextMarginHead = textMarginTop;
    }

    public int getTextMarginHead() {
        return mTextMarginHead;
    }

    public int getTextMarginLine() {
        return mTextMarginLine;
    }

    public void setTextMarginLine(int textMarginLine) {
        mTextMarginLine = textMarginLine;
    }

    public void setTextSize(int textSize) {
        this.mTextSize = textSize;
    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setInterval(int interval) {
        this.mInterval = interval;
    }

    public int getInterval() {
        return mInterval;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public int getScaleColor() {
        return mScaleColor;
    }

    public void setCount(int mCount) {
        this.mCount = mCount;
    }

    public int getCount() {
        return mCount;
    }

    public boolean isDrawOutLine() {
        return isDrawOutLine;
    }

    public void setDrawOutLine(boolean drawOutLine) {
        isDrawOutLine = drawOutLine;
    }

    public int getTriangleWidth() {
        return mTriangleWidth;
    }

    public void setTriangleWidth(int triangleWidth) {
        mTriangleWidth = triangleWidth;
        refreshRuler();
        invalidate();
    }

    public int getTriangleHeight() {
        return mTriangleHeight;
    }

    public void setTriangleHeight(int triangleHeight) {
        mTriangleHeight = triangleHeight;
        refreshRuler();
        invalidate();
    }
}
