package integration;

import com.github.dannywe.csv.base.Cont;
import com.github.dannywe.csv.base.Next;
import com.github.dannywe.csv.base.Stop;
import com.github.dannywe.csv.base.writer.Iterator;

import java.io.BufferedReader;
import java.io.IOException;

public class FileIteratorAdaptor implements Iterator<User> {

    private BufferedReader bufferedReader;

    public FileIteratorAdaptor(BufferedReader bufferedReader) {

        this.bufferedReader = bufferedReader;
    }

//    @Override
//    public boolean hasNext() {
//        return bufferedReader.readLine()
//    }


    @Override
    public Next<User> next() throws IOException {
        String s = bufferedReader.readLine();
        System.out.println(s);
        if (s == null) return new Stop<>();
        return new Cont<>(new User(s.split(",")));

//        String s = bufferedReader.readLine();
//        System.out.println(s);
//        if (s == null) return new Stop;
//        return new User(s.split(","));
    }

    @Override
    public void close() throws IOException {
        bufferedReader.close();
    }

}
