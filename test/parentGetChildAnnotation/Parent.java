package parentGetChildAnnotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Created by uv2sun on 2016/12/29.
 */
public class Parent {
    public void init() {
        Field[] fs = this.getClass().getDeclaredFields();
        for (Field f : fs) {
            System.out.println(f);
            System.out.println(f.getType());
            Annotation[] as = f.getAnnotationsByType(TestAnnotation.class);
            System.out.println("an len " + as.length);
            for (Annotation a : as) {
                System.out.println(a);
                System.out.println(a.getClass());
            }
        }
    }
}
