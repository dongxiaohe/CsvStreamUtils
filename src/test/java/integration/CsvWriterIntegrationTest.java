package integration;

import app.CsvWriterService;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CsvWriterIntegrationTest {

    private CsvWriterService service;

    @Before
    public void setUp() throws Exception {
        service = new CsvWriterService();
    }

    @Test
    public void shouldWriteToOutputStream() throws IOException {
        String fileName = "src/test/resources/users.csv";
//        String fileName = "C:\\workspace\\spike\\dummy.txt";
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter("src/test/resources/output.csv"));
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        FileIteratorAdaptor adaptor = new FileIteratorAdaptor(reader);


        service.writeTOStream(adaptor, User::produce, fileWriter);

        BufferedReader result = new BufferedReader(new FileReader("src/test/resources/output.csv"));
        assertThat(result.readLine(), is("username,company,interest,team"));
    }
}
