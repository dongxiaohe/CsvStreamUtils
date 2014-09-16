package integration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class JavaFileUtils {

    public java.util.Iterator<String> getStrings() {
        File file = new File("C:\\workspace\\spike\\dummy.txt");


        String tempLine = null;
        BufferedReader br = null;
        List<String> list = new ArrayList<>();
        int lineCount = 0;
        System.out.println(System.currentTimeMillis());
        try {
            br = new BufferedReader(new FileReader(file));
            while ((tempLine = br.readLine()) != null) {
                list.add(tempLine);
                lineCount += 1;
                if (lineCount == 5000000 ) break;
            }

//            System.out.println(list.size());
            System.out.println(System.currentTimeMillis());
        } catch (Exception e) {
            System.out.println("br error: " + e.getMessage());

            System.out.println(file);
        }
        return list.iterator();
    }
}
