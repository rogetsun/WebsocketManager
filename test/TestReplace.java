import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestReplace {
    public static void main(String[] args) {
        String a = "1234567890";
        List<String> strings = new ArrayList<>();
        
        for (int i = 0; i < 10; i++) {
            strings.add(a.replace((i % 10) + "", "a"));
        }
        System.out.println(Arrays.toString(strings.toArray()));
    }
}
