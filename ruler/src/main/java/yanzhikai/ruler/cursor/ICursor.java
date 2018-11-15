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
public interface ICursor {

    void onPreDraw(Drawable drawable, int style, int width, int height, InnerRuler innerRuler);

    void onDrawCursor(Canvas canvas, Drawable drawable, int style, @BooheeRuler.DisplayMode int mode);
}
