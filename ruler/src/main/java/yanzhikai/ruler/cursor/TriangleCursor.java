package yanzhikai.ruler.cursor;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;

import yanzhikai.ruler.BooheeRuler;
import yanzhikai.ruler.InnerRulers.InnerRuler;
import yanzhikai.ruler.Utils.ReflectionUtil;

/**
 * desc
 * created by liangtiande
 * date 2018/11/13
 */
public class TriangleCursor implements ICursor {
    private static final String TAG = "TriangleCursor";
    private Paint mPaint;
    private Path mPath;
    private Drawable mDrawable;

    public TriangleCursor() {
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setAntiAlias(true);

        mPath = new Path();

    }

    private void initPath(InnerRuler innerRuler, int style) {
        Rect bounds = mDrawable.getBounds();
        if (mDrawable instanceof GradientDrawable) {
            GradientDrawable gradientDrawable = (GradientDrawable) mDrawable;
            Object gradientState = ReflectionUtil.getFieldValue(gradientDrawable, "mGradientState");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ColorStateList colorStateList = (ColorStateList) ReflectionUtil.getFieldValue(gradientState, "mSolidColors");
                if (colorStateList != null) {
                    mPaint.setColor(colorStateList.getDefaultColor());
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ColorStateList colorStateList = (ColorStateList) ReflectionUtil.getFieldValue(gradientState, "mColorStateList");
                if (colorStateList != null) {
                    mPaint.setColor(colorStateList.getDefaultColor());
                }
            } else {
                Object solidColor = ReflectionUtil.getFieldValue(gradientState, "mSolidColor");
                if (solidColor != null && solidColor instanceof Integer) {
                    int color = (int) solidColor;
                    if (color != 0) {
                        mPaint.setColor(color);
                    }
                }
            }
        }

        //实例化路径
        mPath.reset();
        BooheeRuler booheeRuler = innerRuler.getBooheeRuler();
        int triangleWidth = booheeRuler.getTriangleWidth();
        int triangleHeight = booheeRuler.getTriangleHeight();
        switch (style) {
            case BooheeRuler.TOP_HEAD:
                mPath.moveTo(bounds.left, bounds.bottom);// 此点为多边形的起点
                mPath.quadTo(bounds.left + (bounds.right - bounds.left) / 2, (bounds.bottom + (bounds.right - bounds.left) / 2), bounds.right, bounds.bottom);
                mPath.lineTo(bounds.right, bounds.top);
                mPath.lineTo(bounds.right + triangleWidth, bounds.top - triangleHeight);
                mPath.lineTo(bounds.left - triangleWidth, bounds.top - triangleHeight);
                mPath.lineTo(bounds.left, bounds.top);
                break;
            case BooheeRuler.BOTTOM_HEAD:
                mPath.moveTo(bounds.left, bounds.top);// 此点为多边形的起点
                mPath.quadTo(bounds.left + (bounds.right - bounds.left) / 2, bounds.top - (bounds.right - bounds.left) / 2, bounds.right, bounds.top);
                mPath.lineTo(bounds.right, bounds.bottom);
                mPath.lineTo(bounds.right + triangleWidth, bounds.bottom + triangleHeight);
                mPath.lineTo(bounds.left - triangleWidth, bounds.bottom + triangleHeight);
                mPath.lineTo(bounds.left, bounds.bottom);
                break;
            case BooheeRuler.LEFT_HEAD:
                mPath.moveTo(bounds.right, bounds.top);
                mPath.quadTo((bounds.right + (bounds.bottom - bounds.top) / 2), bounds.top + (bounds.bottom - bounds.top) / 2, bounds.right, bounds.bottom);
                mPath.lineTo(bounds.left, bounds.bottom);
                mPath.lineTo(bounds.left - triangleHeight, bounds.bottom + triangleWidth);
                mPath.lineTo(bounds.left - triangleHeight, bounds.top - triangleWidth);
                mPath.lineTo(bounds.left, bounds.top);
                break;
            case BooheeRuler.RIGHT_HEAD:
                mPath.moveTo(bounds.left, bounds.top);
                mPath.quadTo(bounds.left - ((bounds.bottom - bounds.top) / 2), bounds.top + ((bounds.bottom - bounds.top) / 2), bounds.left, bounds.bottom);
                mPath.lineTo(bounds.right, bounds.bottom);
                mPath.lineTo(bounds.right + triangleHeight, bounds.bottom + triangleWidth);
                mPath.lineTo(bounds.right + triangleHeight, bounds.top - triangleWidth);
                mPath.lineTo(bounds.right, bounds.top);
                break;
        }


        mPath.close(); // 使这些点构成封闭的多边形
    }

    @Override
    public void onPreDraw(Drawable drawable, int style, int width, int height, InnerRuler innerRuler) {
        switch (style) {
            case BooheeRuler.TOP_HEAD:
                drawable.setBounds((width - drawable.getIntrinsicWidth()) / 2,
                        (int) (innerRuler.getLineStartCoordinate()),
                        (width + drawable.getIntrinsicWidth()) / 2,
                        (int) innerRuler.getLineStartCoordinate() + drawable.getIntrinsicHeight());
                break;
            case BooheeRuler.BOTTOM_HEAD:
                drawable.setBounds((width - drawable.getIntrinsicWidth()) / 2,
                        (int) (innerRuler.getLineStartCoordinate() - drawable.getIntrinsicHeight()),
                        (width + drawable.getIntrinsicWidth()) / 2,
                        (int) innerRuler.getLineStartCoordinate());
                break;
            case BooheeRuler.LEFT_HEAD:
                drawable.setBounds((int) (innerRuler.getLineStartCoordinate()),
                        (height - drawable.getIntrinsicWidth()) / 2,
                        (int) (innerRuler.getLineStartCoordinate() + drawable.getIntrinsicHeight()),
                        (height + drawable.getIntrinsicWidth()) / 2);
                break;
            case BooheeRuler.RIGHT_HEAD:
                drawable.setBounds((int) (innerRuler.getLineStartCoordinate() - drawable.getIntrinsicHeight()),
                        (height - drawable.getIntrinsicWidth()) / 2,
                        (int) (innerRuler.getLineStartCoordinate()),
                        (height + drawable.getIntrinsicWidth()) / 2);
                break;
        }

        if (mDrawable == null) {
            mDrawable = drawable;
            onDrawableSet(innerRuler, style);
        }
    }

    @Override
    public void onDrawCursor(Canvas canvas, Drawable drawable, int style, int mode) {
        canvas.drawPath(mPath, mPaint);
    }

    private void onDrawableSet(InnerRuler innerRuler, int style) {
        initPath(innerRuler, style);
    }
}
