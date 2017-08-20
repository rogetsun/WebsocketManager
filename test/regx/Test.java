package regx;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    
    public static Pattern p = Pattern.compile("(?<=\\{)([^\\}]+)(?=\\})");
    
    public static void main(String[] args) {
        Map<String, String> m = new HashMap<>();
        m.put("x1", "111111");
        m.put("x2", "22222");
        
        String a = "a@{x1}|{x2}@{x3}";
        Matcher matcher = p.matcher(a);
        while (matcher.find()) {
            String key = matcher.group();
            System.out.println(key);
            a = a.replaceAll("\\{" + key + "\\}", m.getOrDefault(key, ""));
        }
        
        System.out.println(a);
    }
}
