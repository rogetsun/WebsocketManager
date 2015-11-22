package test.TestGetSupperClass;

/**
 * Created by uv2sun on 15/11/22.
 */
public class TestMain {
    public static void main(String[] args) {
        Class clazz = Sub.class.getSuperclass();
        System.out.println(clazz);
        System.out.println(clazz.getGenericSuperclass());
    }
}
