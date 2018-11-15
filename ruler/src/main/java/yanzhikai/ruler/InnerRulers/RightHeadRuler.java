package yanzhikai.ruler.InnerRulers;

import android.content.Context;
import android.graphics.Canvas;

import yanzhikai.ruler.BooheeRuler;
import yanzhikai.ruler.Utils.RulerStringUtil;

/**
 * 头向→的尺子
 */
public class RightHeadRuler extends VerticalRuler {
    public static final int MARGIN = 20;

    public RightHeadRuler(Context context, BooheeRuler booheeRuler) {
        super(context, booheeRuler);
    }


    //画刻度和字
    @Override
    protected void drawScale(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        float start = (getScrollY() - mDrawOffset) / mParent.getInterval() + mParent.getMinScale();
        float end = (getScrollY() + height + mDrawOffset) / mParent.getInterval() + mParent.getMinScale();
        for (float i = start; i <= end; i++) {
            float locationY = (i - mParent.getMinScale()) * mParent.getInterval();

            if (i >= mParent.getMinScale() && i <= mParent.getMaxScale()) {
                String value = RulerStringUtil.resultValueOf(i, mParent.getFactor());

                if (mScaleTextMode == BooheeRuler.DisplayMode.MODE_SCALE_TEXT_BOTTOM) {
                    if (i % mCount == 0) {
                        canvas.drawLine(mLineStartCoordinate, locationY, mLineStartCoordinate - mParent.getBigScaleLength(), locationY, mBigScalePaint);
                        canvas.drawText(value, mTextStartCoordinate, locationY + mParent.getTextSize() / 2, mTextPaint);
                    } else {
                        canvas.drawLine(mLineStartCoordinate, locationY, mLineStartCoordinate - mParent.getSmallScaleLength(), locationY, mSmallScalePaint);
                    }
                } else {
                    if (i % mCount == 0) {
                        canvas.drawLine(width - mParent.getBigScaleLength(), locationY, width, locationY, mBigScalePaint);
                        canvas.drawText(value, mTextStartCoordinate, locationY + mParent.getTextSize() / 2, mTextPaint);
                    } else {
                        canvas.drawLine(width - mParent.getSmallScaleLength(), locationY, width, locationY, mSmallScalePaint);
                    }
                }
            }
        }
    }

    @Override
    protected void drawOutLine(Canvas canvas) {
        //画轮廓线
        canvas.drawLine(canvas.getWidth() - 1, getScrollY(), canvas.getWidth() - 1, getScrollY() + canvas.getHeight(), mOutLinePaint);
    }

    //画边缘效果
    @Override
    protected void drawEdgeEffect(Canvas canvas) {
        if (mParent.canEdgeEffect()) {
            if (!mStartEdgeEffect.isFinished()) {
                int count = canvas.save();
                canvas.translate((getWidth() - mParent.getCursorWidth()), 0);

                if (mStartEdgeEffect.draw(canvas)) {
                    postInvalidateOnAnimation();
                }
                canvas.restoreToCount(count);
            } else {
                mStartEdgeEffect.finish();
            }
            if (!mEndEdgeEffect.isFinished()) {
                int count = canvas.save();
                canvas.rotate(180);
                canvas.translate(-getWidth(), -mLength);
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
        String value = RulerStringUtil.resultValueOf(mParent.getMaxScale(), mParent.getFactor());
        int textLength = value.length();
        mTextWidthSum = 0;
        float[] textWidths = new float[textLength];
        mTextPaint.getTextWidths(value, textWidths);
        for (float textWidth : textWidths) {
            mTextWidthSum += textWidth;
        }
        mLineStartCoordinate = getWidth() - mParent.getTextMarginHead() - mTextWidthSum - mParent.getTextMarginLine();
        mTextStartCoordinate = getWidth() - mParent.getTextMarginHead() - mTextWidthSum / 2;
    }
}
