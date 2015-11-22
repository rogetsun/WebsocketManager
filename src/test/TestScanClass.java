package test;

import com.uv.util.ScanClassUtil;

import java.util.Set;

/**
 * Created by uv2sun on 15/11/22.
 */
public class TestScanClass {
    public static void main(String[] args) {
        Set<Class<?>> set = ScanClassUtil.getClasses("test.TransferQueue");
        System.out.println(set.size());
        for (Class<?> aClass : set) {
            System.out.println(aClass);
        }
    }
}
