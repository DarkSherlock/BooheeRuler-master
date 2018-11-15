package yanzhikai.ruler.InnerRulers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import yanzhikai.ruler.BooheeRuler;
import yanzhikai.ruler.Utils.RulerStringUtil;

/**
 * 头向下的尺子
 */

@SuppressLint("ViewConstructor")
public class BottomHeadRuler extends HorizontalRuler {

    public BottomHeadRuler(Context context, BooheeRuler booheeRuler) {
        super(context, booheeRuler);
    }

    //画刻度和字
    @Override
    protected void drawScale(Canvas canvas) {
        float start = (getScrollX() - mDrawOffset) / mParent.getInterval() + mParent.getMinScale();
        float end = (getScrollX() + canvas.getWidth() + mDrawOffset) / mParent.getInterval() + mParent.getMinScale();
        int height = canvas.getHeight();

        for (float i = start; i <= end; i++) {
            float locationX = (i - mParent.getMinScale()) * mParent.getInterval();
            if (i >= mParent.getMinScale() && i <= mParent.getMaxScale()) {
                if (mScaleTextMode == BooheeRuler.DisplayMode.MODE_SCALE_TEXT_BOTTOM) {
                    if (i % mCount == 0) {
                        canvas.drawLine(locationX, mLineStartCoordinate, locationX, mLineStartCoordinate - mParent.getBigScaleLength(), mBigScalePaint);
                        canvas.drawText(RulerStringUtil.resultValueOf(i, mParent.getFactor()), locationX, mTextStartCoordinate, mTextPaint);
                    } else {
                        canvas.drawLine(locationX, mLineStartCoordinate, locationX, mLineStartCoordinate - mParent.getSmallScaleLength(), mSmallScalePaint);
                    }
                } else {
                    if (i % mCount == 0) {
                        canvas.drawLine(locationX, height - mParent.getBigScaleLength(), locationX, height, mBigScalePaint);
                        canvas.drawText(RulerStringUtil.resultValueOf(i, mParent.getFactor()), locationX, height - mParent.getTextMarginHead(), mTextPaint);
                    } else {
                        canvas.drawLine(locationX, height - mParent.getSmallScaleLength(), locationX, height, mSmallScalePaint);
                    }
                }

            }
        }
    }

    @Override
    protected void drawOutLine(Canvas canvas) {
        //画轮廓线
        canvas.drawLine(getScrollX(), canvas.getHeight()-1, getScrollX() + canvas.getWidth(), canvas.getHeight()-1, mOutLinePaint);
    }

    //画边缘效果
    @Override
    protected void drawEdgeEffect(Canvas canvas) {
        if (mParent.canEdgeEffect()) {
            if (!mStartEdgeEffect.isFinished()) {
                int count = canvas.save();
                canvas.rotate(270);
                canvas.translate(-getHeight(), 0);
                if (mStartEdgeEffect.draw(canvas)) {
                    postInvalidateOnAnimation();
                }
                canvas.restoreToCount(count);
            } else {
                mStartEdgeEffect.finish();
            }
            if (!mEndEdgeEffect.isFinished()) {
                int count = canvas.save();
                canvas.rotate(90);
                canvas.translate((getHeight() - mParent.getCursorHeight()), -mLength);
                if (mEndEdgeEffect.draw(canvas)) {
                    postInvalidateOnAnimation();
                }
                canvas.restoreToCount(count);
            } else {
                mEndEdgeEffect.finish();
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float textHeight = fontMetrics.descent - fontMetrics.ascent;
        mLineStartCoordinate = getHeight() - mParent.getTextMarginHead() - textHeight -mParent.getTextMarginLine();
        mTextStartCoordinate = getHeight() - mParent.getTextMarginHead();
    }
}
