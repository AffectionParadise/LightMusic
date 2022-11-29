package net.doge.utils;

import java.awt.event.KeyEvent;
import java.util.*;

/**
 * @Author yzx
 * @Description 快捷键工具类
 * @Date 2020/12/15
 */
public class KeyUtils {
    private static final Map<Integer, String> map = new HashMap<>();

    static {
        map.put(KeyEvent.VK_F1, "F1");
        map.put(KeyEvent.VK_F2, "F2");
        map.put(KeyEvent.VK_F3, "F3");
        map.put(KeyEvent.VK_F4, "F4");
        map.put(KeyEvent.VK_F5, "F5");
        map.put(KeyEvent.VK_F6, "F6");
        map.put(KeyEvent.VK_F7, "F7");
        map.put(KeyEvent.VK_F8, "F8");
        map.put(KeyEvent.VK_F9, "F9");
        map.put(KeyEvent.VK_F10, "F10");
        map.put(KeyEvent.VK_F11, "F11");
        map.put(KeyEvent.VK_F12, "F12");
        map.put(KeyEvent.VK_A, "A");
        map.put(KeyEvent.VK_B, "B");
        map.put(KeyEvent.VK_C, "C");
        map.put(KeyEvent.VK_D, "D");
        map.put(KeyEvent.VK_E, "E");
        map.put(KeyEvent.VK_F, "F");
        map.put(KeyEvent.VK_G, "G");
        map.put(KeyEvent.VK_H, "H");
        map.put(KeyEvent.VK_I, "I");
        map.put(KeyEvent.VK_J, "J");
        map.put(KeyEvent.VK_K, "K");
        map.put(KeyEvent.VK_L, "L");
        map.put(KeyEvent.VK_M, "M");
        map.put(KeyEvent.VK_N, "N");
        map.put(KeyEvent.VK_O, "O");
        map.put(KeyEvent.VK_P, "P");
        map.put(KeyEvent.VK_Q, "Q");
        map.put(KeyEvent.VK_R, "R");
        map.put(KeyEvent.VK_S, "S");
        map.put(KeyEvent.VK_T, "T");
        map.put(KeyEvent.VK_U, "U");
        map.put(KeyEvent.VK_V, "V");
        map.put(KeyEvent.VK_W, "W");
        map.put(KeyEvent.VK_X, "X");
        map.put(KeyEvent.VK_Y, "Y");
        map.put(KeyEvent.VK_Z, "Z");
        map.put(KeyEvent.VK_BACK_QUOTE, "`");
        map.put(KeyEvent.VK_0, "0");
        map.put(KeyEvent.VK_1, "1");
        map.put(KeyEvent.VK_2, "2");
        map.put(KeyEvent.VK_3, "3");
        map.put(KeyEvent.VK_4, "4");
        map.put(KeyEvent.VK_5, "5");
        map.put(KeyEvent.VK_6, "6");
        map.put(KeyEvent.VK_7, "7");
        map.put(KeyEvent.VK_8, "8");
        map.put(KeyEvent.VK_9, "9");
        map.put(KeyEvent.VK_MINUS, "-");
        map.put(KeyEvent.VK_EQUALS, "=");
        map.put(KeyEvent.VK_EXCLAMATION_MARK, "!");
        map.put(KeyEvent.VK_AT, "@");
        map.put(KeyEvent.VK_NUMBER_SIGN, "#");
        map.put(KeyEvent.VK_DOLLAR, "$");
        map.put(KeyEvent.VK_CIRCUMFLEX, "^");
        map.put(KeyEvent.VK_AMPERSAND, "&");
        map.put(KeyEvent.VK_ASTERISK, "*");
        map.put(KeyEvent.VK_LEFT_PARENTHESIS, "(");
        map.put(KeyEvent.VK_RIGHT_PARENTHESIS, ")");
        map.put(KeyEvent.VK_UNDERSCORE, "_");
        map.put(KeyEvent.VK_PLUS, "+");
        map.put(KeyEvent.VK_TAB, "Tab");
        map.put(KeyEvent.VK_ENTER, "Enter");
        map.put(KeyEvent.VK_OPEN_BRACKET, "[");
        map.put(KeyEvent.VK_CLOSE_BRACKET, "]");
        map.put(KeyEvent.VK_BACK_SLASH, "\\");
        map.put(KeyEvent.VK_SEMICOLON, ";");
        map.put(KeyEvent.VK_COLON, ":");
        map.put(KeyEvent.VK_QUOTE, "'");
        map.put(KeyEvent.VK_QUOTEDBL, "\"");
        map.put(KeyEvent.VK_COMMA, ",");
        map.put(KeyEvent.VK_PERIOD, ".");
        map.put(KeyEvent.VK_SLASH, "/");
        map.put(KeyEvent.VK_SPACE, "Space");
        map.put(KeyEvent.VK_BACK_SPACE, "Back");
        map.put(KeyEvent.VK_ENTER, "Enter");
        map.put(KeyEvent.VK_TAB, "Tab");
        map.put(KeyEvent.VK_CAPS_LOCK, "CapsLk");
        map.put(KeyEvent.VK_CONTROL, "Ctrl");
        map.put(KeyEvent.VK_SHIFT, "Shift");
        map.put(KeyEvent.VK_ALT, "Alt");
        map.put(KeyEvent.VK_WINDOWS, "Win");
        map.put(KeyEvent.VK_ESCAPE, "Esc");
        map.put(KeyEvent.VK_DELETE, "Delete");
        map.put(KeyEvent.VK_UP, "↑");
        map.put(KeyEvent.VK_DOWN, "↓");
        map.put(KeyEvent.VK_LEFT, "←");
        map.put(KeyEvent.VK_RIGHT, "→");
        map.put(KeyEvent.VK_HOME, "Home");
        map.put(KeyEvent.VK_END, "End");
        map.put(KeyEvent.VK_PAGE_UP, "Pgup");
        map.put(KeyEvent.VK_PAGE_DOWN, "Pgdn");
        map.put(KeyEvent.VK_INSERT, "Insert");
        map.put(KeyEvent.VK_PRINTSCREEN, "PrtSc");
        map.put(KeyEvent.VK_NUM_LOCK, "NumLock");
    }

    /**
     * KeyEvent 转为字符串
     *
     * @param
     * @return
     */
    public static String toStr(int code) {
        return map.get(code);
    }

    /**
     * 将 KeyList 中元素连接成字符串
     *
     * @param keyList
     * @return
     */
    public static String join(List<Integer> keyList) {
        StringJoiner sj = new StringJoiner(" + ");
        for (int i = 0, len = keyList.size(); i < len; i++) {
            sj.add(toStr(keyList.get(i)));
        }
        return sj.toString();
    }

    /**
     * 字符串转为 KeyCode 列表
     *
     * @param
     * @return
     */
    public static LinkedList<Integer> strToCodes(String str) {
        String[] sp = str.split(" ");
        LinkedList<Integer> codes = new LinkedList<>();
        for (String s : sp)
            if (!s.isEmpty()) codes.add(Integer.parseInt(s));
        return codes;
    }

    /**
     * KeyCode 列表转为字符串
     *
     * @param
     * @return
     */
    public static String codesToStr(List<Integer> codes) {
        StringJoiner sj = new StringJoiner(" ");
        for (int i = 0, len = codes.size(); i < len; i++) {
            sj.add(String.valueOf(codes.get(i)));
        }
        return sj.toString();
    }
}
