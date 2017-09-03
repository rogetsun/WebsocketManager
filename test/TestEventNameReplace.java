import com.uv.util.EventNameUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestEventNameReplace {
    public static void main(String[] args) {
        String en = "a@{id}@{type}@{age}";
        Map<String, List<String>> pm = new HashMap<>();
        EventNameUtil.getPlaceholderParamKey(en).forEach(key -> {
            List<String> ps = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                ps.add(key + "-" + (i + 1));
            }
            pm.put(key, ps);
        });
        pm.remove("type");
        
        List<String> l = EventNameUtil.eventNameParamReplace(en, pm);
        System.out.println(l);
        System.out.println(l.size());
    }
}
