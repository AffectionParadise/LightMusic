package net.doge.util.system;

import com.alibaba.fastjson2.JSONArray;
import net.doge.util.common.JsonUtil;

import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

/**
 * @Author Doge
 * @Description 快捷键工具类
 * @Date 2020/12/15
 */
public class KeyUtil {
    /**
     * KeyEvent 转为字符串
     *
     * @param
     * @return
     */
    public static String toStr(int code) {
        if (code >= KeyEvent.VK_0 && code <= KeyEvent.VK_9 ||
                code >= KeyEvent.VK_A && code <= KeyEvent.VK_Z) {
            return String.valueOf((char) code);
        }

        switch (code) {
            case KeyEvent.VK_ENTER:
                return "Enter";
            case KeyEvent.VK_BACK_SPACE:
                return "Backspace";
            case KeyEvent.VK_TAB:
                return "Tab";
            case KeyEvent.VK_CANCEL:
                return "Cancel";
            case KeyEvent.VK_CLEAR:
                return "Clear";
            case KeyEvent.VK_COMPOSE:
                return "Compose";
            case KeyEvent.VK_PAUSE:
                return "Pause";
            case KeyEvent.VK_CAPS_LOCK:
                return "Capslock";
            case KeyEvent.VK_ESCAPE:
                return "Esc";
            case KeyEvent.VK_SPACE:
                return "Space";
            case KeyEvent.VK_PAGE_UP:
                return "Pageup";
            case KeyEvent.VK_PAGE_DOWN:
                return "Pagedown";
            case KeyEvent.VK_END:
                return "End";
            case KeyEvent.VK_HOME:
                return "Home";
            case KeyEvent.VK_LEFT:
                return "←";
            case KeyEvent.VK_UP:
                return "↑";
            case KeyEvent.VK_RIGHT:
                return "→";
            case KeyEvent.VK_DOWN:
                return "↓";
            case KeyEvent.VK_BEGIN:
                return "Begin";

            // modifiers
            case KeyEvent.VK_SHIFT:
                return "Shift";
            case KeyEvent.VK_CONTROL:
                return "Ctrl";
            case KeyEvent.VK_ALT:
                return "Alt";
            case KeyEvent.VK_META:
                return "Meta";
            case KeyEvent.VK_ALT_GRAPH:
                return "Altgraph";

            // punctuation
            case KeyEvent.VK_COMMA:
                return ",";
            case KeyEvent.VK_PERIOD:
                return ".";
            case KeyEvent.VK_SLASH:
                return "/";
            case KeyEvent.VK_SEMICOLON:
                return ";";
            case KeyEvent.VK_EQUALS:
                return "=";
            case KeyEvent.VK_OPEN_BRACKET:
                return "[";
            case KeyEvent.VK_BACK_SLASH:
                return "\"";
            case KeyEvent.VK_CLOSE_BRACKET:
                return "]";

            // numpad numeric keys handled below
            case KeyEvent.VK_MULTIPLY:
                return "Nummul";
            case KeyEvent.VK_ADD:
                return "Numadd";
            case KeyEvent.VK_SEPARATOR:
                return "Numsep";
            case KeyEvent.VK_SUBTRACT:
                return "Numsub";
            case KeyEvent.VK_DECIMAL:
                return "Numdec";
            case KeyEvent.VK_DIVIDE:
                return "Numdiv";
            case KeyEvent.VK_DELETE:
                return "Delete";
            case KeyEvent.VK_NUM_LOCK:
                return "Numlock";
            case KeyEvent.VK_SCROLL_LOCK:
                return "Scrolllock";

            case KeyEvent.VK_WINDOWS:
                return "Win";
            case KeyEvent.VK_CONTEXT_MENU:
                return "Contextmenu";

            case KeyEvent.VK_F1:
                return "F1";
            case KeyEvent.VK_F2:
                return "F2";
            case KeyEvent.VK_F3:
                return "F3";
            case KeyEvent.VK_F4:
                return "F4";
            case KeyEvent.VK_F5:
                return "F5";
            case KeyEvent.VK_F6:
                return "F6";
            case KeyEvent.VK_F7:
                return "F7";
            case KeyEvent.VK_F8:
                return "F8";
            case KeyEvent.VK_F9:
                return "F9";
            case KeyEvent.VK_F10:
                return "F10";
            case KeyEvent.VK_F11:
                return "F11";
            case KeyEvent.VK_F12:
                return "F12";
            case KeyEvent.VK_F13:
                return "F13";
            case KeyEvent.VK_F14:
                return "F14";
            case KeyEvent.VK_F15:
                return "F15";
            case KeyEvent.VK_F16:
                return "F16";
            case KeyEvent.VK_F17:
                return "F17";
            case KeyEvent.VK_F18:
                return "F18";
            case KeyEvent.VK_F19:
                return "F19";
            case KeyEvent.VK_F20:
                return "F20";
            case KeyEvent.VK_F21:
                return "F21";
            case KeyEvent.VK_F22:
                return "F22";
            case KeyEvent.VK_F23:
                return "F23";
            case KeyEvent.VK_F24:
                return "F24";

            case KeyEvent.VK_PRINTSCREEN:
                return "PrtSc";
            case KeyEvent.VK_INSERT:
                return "Insert";
            case KeyEvent.VK_HELP:
                return "Help";
            case KeyEvent.VK_BACK_QUOTE:
                return "`";
            case KeyEvent.VK_QUOTE:
                return "'";

            case KeyEvent.VK_KP_UP:
                return "↑";
            case KeyEvent.VK_KP_DOWN:
                return "↓";
            case KeyEvent.VK_KP_LEFT:
                return "←";
            case KeyEvent.VK_KP_RIGHT:
                return "→";

            case KeyEvent.VK_AMPERSAND:
                return "&";
            case KeyEvent.VK_ASTERISK:
                return "*";
            case KeyEvent.VK_QUOTEDBL:
                return "\"";
            case KeyEvent.VK_LESS:
                return "<";
            case KeyEvent.VK_GREATER:
                return ">";
            case KeyEvent.VK_BRACELEFT:
                return "(";
            case KeyEvent.VK_BRACERIGHT:
                return ")";
            case KeyEvent.VK_AT:
                return "@";
            case KeyEvent.VK_COLON:
                return ":";
            case KeyEvent.VK_CIRCUMFLEX:
                return "^";
            case KeyEvent.VK_DOLLAR:
                return "$";
            case KeyEvent.VK_EURO_SIGN:
                return "€";
            case KeyEvent.VK_EXCLAMATION_MARK:
                return "!";
            case KeyEvent.VK_INVERTED_EXCLAMATION_MARK:
                return "¡";
            case KeyEvent.VK_LEFT_PARENTHESIS:
                return "{";
            case KeyEvent.VK_NUMBER_SIGN:
                return "#";
            case KeyEvent.VK_MINUS:
                return "-";
            case KeyEvent.VK_PLUS:
                return "+";
            case KeyEvent.VK_RIGHT_PARENTHESIS:
                return "}";
            case KeyEvent.VK_UNDERSCORE:
                return "_";
        }

        // 数字键盘 0-9
        if (code >= KeyEvent.VK_NUMPAD0 && code <= KeyEvent.VK_NUMPAD9) {
            String numpad = "Num";
            char c = (char) (code - KeyEvent.VK_NUMPAD0 + '0');
            return numpad + c;
        }

        if ((code & 0x01000000) != 0) {
            return String.valueOf((char) (code ^ 0x01000000));
        }
        String unknown = "Unknown";
        return unknown + " code: 0x" + Integer.toString(code, 16);
    }

    /**
     * 将 code 列表中元素连接成字符串
     *
     * @param keys
     * @return
     */
    public static String join(List<Integer> keys) {
        StringJoiner sj = new StringJoiner(" + ");
        for (Integer key : keys) sj.add(toStr(key));
        return sj.toString();
    }

    /**
     * Json 数组转为 code 列表
     *
     * @param
     * @return
     */
    public static List<Integer> jsonArrayToKeys(JSONArray array) {
        List<Integer> codes = new LinkedList<>();
        if (JsonUtil.notEmpty(array)) {
            for (int i = 0, s = array.size(); i < s; i++) codes.add(array.getIntValue(i));
        }
        return codes;
    }

    /**
     * code 列表转为 Json 数组
     *
     * @param
     * @return
     */
    public static JSONArray keysToJsonArray(List<Integer> keys) {
        JSONArray array = new JSONArray();
        array.addAll(keys);
        return array;
    }
}
