package parentGetChildAnnotation;

/**
 * Created by uv2sun on 2016/12/29.
 */
public class Child extends Parent {


    @TestAnnotation()
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
