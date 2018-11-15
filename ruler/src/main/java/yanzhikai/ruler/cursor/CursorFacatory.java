package yanzhikai.ruler.cursor;

import yanzhikai.ruler.BooheeRuler;

/**
 * desc
 * created by liangtiande
 * date 2018/11/12
 */
public class CursorFacatory {

    public static ICursor generateCursor(@BooheeRuler.DisplayMode int mode){
        ICursor cursor = null;
        switch (mode){
            case BooheeRuler.DisplayMode.MODE_SCALE_TEXT_BOTTOM:
                cursor = new TriangleCursor();
                break;
            case BooheeRuler.DisplayMode.MODE_SCALE_TEXT_TOP:
                cursor = new LinearCursor();
                break;
        }

        return cursor;
    }
}
