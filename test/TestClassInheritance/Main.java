package TestClassInheritance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Class<? extends AIf>> list = new ArrayList<>();
        list.add(AImpl.class);
        System.out.println(Arrays.toString(list.toArray()));
        System.out.println(AImpl.class.isInterface());
        System.out.println(AIf.class.isInterface());
        System.out.println(AImpl.class.isAssignableFrom(AIf.class));
        System.out.println(AIf.class.isAssignableFrom(AImpl.class));
        System.out.println(Object.class.isAssignableFrom(AImpl.class));
    }
}
