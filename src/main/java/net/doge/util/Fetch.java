//package net.doge.util;
//
//import net.doge.entity.core.lyric.LyricData;
//import net.doge.entity.core.lyric.Statement;
//import net.doge.util.core.http.HttpRequest;
//import net.doge.util.core.http.constant.Header;
//
//import java.io.File;
//import java.util.List;
//
//public class Fetch {
//    public static void main(String[] args) {
//        String url = "https://www.missevan.com/sound/senddm";
//        int soundId = 11988218;
//        double offset = 0;
//        LyricData lyricData = new LyricData(new File("C:\\Users\\30710\\Desktop\\1.txt"));
//        List<Statement> statements = lyricData.getStatements();
//        Statement stmt = statements.get(0);
//        if ("···".equals(stmt.getPlainLyric())) statements.remove(0);
//        for (Statement st : statements) {
//            HttpRequest request = HttpRequest.post(url)
//                    .form("sound_id", soundId)
//                    .form("border", "true")
//                    .form("color", "#ffffff")
//                    .form("date", System.currentTimeMillis() / 1000)
//                    .form("dbid", "37353259")
//                    .form("hash", "D5332364")
//                    .form("mode", "4")
//                    .form("pool", "0")
//                    .form("position", "absolute")
//                    .form("size", "25")
//                    .form("stime", st.getTime() + offset)
//                    .form("text", st.getPlainLyric())
//                    .cookie("equip_id=8cbce4d4-5100-11f0-8d2a-8e48fbd01546; buvid3=52714E6A-CF81-9268-7ABC-3C7EED2C2DF584362infoc; b_nut=1761913284; token=6904a9caec605f21bfa4bf76%7Ccf97a030e9067323c987613c00daff14%7C1761913290%7C8e187a3417fcecce; gray_uid=23866574; buvid4=9710C569-F938-A415-47D9-D3F65F7CBEAF10841-025120118-1w84%2BFUIR%2BC%2BMAtVbvLBRlvjv%2BT%2B0AkQnpkWQe13bNBPukE9Z%2BA1rvfXxtusY1IH; acw_tc=0aef816617657083471772181eed12d44c4ace2f62f968bf5a8fd2d50e11e1; MSESSID=e5tihsr6v0seigj5ept8al0gam")
//                    .referer(String.format("https://www.missevan.com/sound/player?id=%s", soundId))
//                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
//                            "(KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36 Edg/107.0.1418.35")
//                    .header(Header.ORIGIN, "https://www.missevan.com");
//            String body = request.executeAsStr();
//            System.out.println(body);
//        }
//
/// /        String str = FileUtil.readStr(new File("C:\\Users\\30710\\Desktop\\1.txt"));
/// /        List<String> gs = RegexUtil.findAllGroup0("[A-Z_]+_TIP", str);
/// /        List<String> gs2 = RegexUtil.findAllGroup0("\".*?\"", str);
/// /        for (int i = 0, s = gs.size(); i < s; i++) {
/// /            String s1 = gs.get(i);
/// /            String s2 = gs2.get(i);
/// /            String[] words = s1.split("_");
/// /            StringBuilder sb = new StringBuilder();
/// /            for (String w : words) {
/// /                sb.append(w.substring(0, 1).toUpperCase() + w.substring(1).toLowerCase());
/// /            }
/// /            System.out.println(String.format("langJson.put(\"%s\", new String[]{%s, \"\"});", sb.substring(0, 1).toLowerCase()+sb.substring(1), s2));
/// /        }
/// /        System.out.println();
/// /        for (int i = 0, s = gs.size(); i < s; i++) {
/// /            String s1 = gs.get(i);
/// /            String[] words = s1.split("_");
/// /            StringBuilder sb = new StringBuilder();
/// /            for (String w : words) {
/// /                sb.append(w.substring(0, 1).toUpperCase() + w.substring(1).toLowerCase());
/// /            }
/// /            System.out.println(String.format("private final String %s = I18n.getText(\"%s\");", s1, sb.substring(0, 1).toLowerCase()+sb.substring(1)));
/// /        }
//    }
//}
