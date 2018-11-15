package yanzhikai.ruler.cursor;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import yanzhikai.ruler.BooheeRuler;
import yanzhikai.ruler.InnerRulers.InnerRuler;


/**
 * desc
 * created by liangtiande
 * date 2018/11/12
 */
public class LinearCursor implements ICursor {

    @Override
    public void onPreDraw(Drawable drawable,int style, int width, int height, InnerRuler innerRuler) {
        switch (style){
            case BooheeRuler.TOP_HEAD:
                drawable.setBounds((width - drawable.getIntrinsicWidth()) / 2, 0, 
                        (width + drawable.getIntrinsicWidth()) / 2, drawable.getIntrinsicHeight());
                break;
            case BooheeRuler.BOTTOM_HEAD:
                drawable.setBounds((width - drawable.getIntrinsicWidth()) / 2, height - drawable.getIntrinsicHeight()
                            , (width + drawable.getIntrinsicWidth()) / 2, height);
                break;
            case BooheeRuler.LEFT_HEAD:
                drawable.setBounds(0, (height - drawable.getIntrinsicWidth()) / 2
                        , drawable.getIntrinsicHeight(), (height + drawable.getIntrinsicWidth()) / 2);
                break;
            case BooheeRuler.RIGHT_HEAD:
                drawable.setBounds(width - drawable.getIntrinsicHeight(), (height - drawable.getIntrinsicWidth()) / 2
                        , width,(height + drawable.getIntrinsicWidth()) / 2);
                break;
        }
    }

    @Override
    public void onDrawCursor(Canvas canvas, Drawable drawable, int style, int mode) {
        drawable.draw(canvas);
    }
}
