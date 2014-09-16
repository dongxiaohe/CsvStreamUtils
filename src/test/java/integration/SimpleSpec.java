package integration;

import java.util.Iterator;

public class SimpleSpec {


    public static void main(String[] args) {
        JavaFileUtils javaFileUtils = new JavaFileUtils();

        Iterator<String> iterator = javaFileUtils.getStrings();
        javaFileUtils = null;
        iterator = null;

        System.out.println("");
    }

}
