package integration;

import com.github.dannywe.csv.appJava.CsvService;
import com.github.dannywe.csv.builder.StreamOperationBuilder;
import org.junit.Before;
import org.junit.Test;
import com.github.dannywe.csv.validation.LineErrorConverter;
import com.github.dannywe.csv.vo.Result;
import com.github.dannywe.csv.vo.SimplifiedErrorContainer;

import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.StringContains.containsString;

public class CsvReaderIntegrationTest {

    private CsvService service;

    @Before
    public void setUp() throws Exception {
        service = new CsvService();
    }

    @Test
    public void shouldUseCsvServiceToParseReader() throws Exception {
        Result<User> result = service.parse(new FileReader("src/test/resources/users.csv"), User::new);

        assertThat(result.isSuccessful(), is(true));

        List<User> userList = result.getResult();

        assertThat(userList.size(), is(7));

        assertThat(userList.get(0).getName(), is("username"));
        assertThat(userList.get(0).getCompany(), is("company"));
        assertThat(userList.get(0).getInterest(), is("interest"));
        assertThat(userList.get(0).getTeam(), is("team"));
        assertThat(userList.get(1).getName(), is("Cloud"));
        assertThat(userList.get(2).getName(), is("James"));
        assertThat(userList.get(3).getName(), is("Andres"));
        assertThat(userList.get(4).getName(), is("Yanhui"));
        assertThat(userList.get(5).getName(), is("Jason"));
        assertThat(userList.get(6).getName(), is("Varol"));
    }

    @Test
    public void shouldUseCsvServiceToParseCsvFile() throws Exception {
        File file = new File("src/test/resources/users.csv");

        Result<User> result = service.parse(file, User::new);

        assertThat(result.isSuccessful(), is(true));

        List<User> userList = result.getResult();

        assertThat(userList.size(), is(7));

        assertThat(userList.get(0).getName(), is("username"));
        assertThat(userList.get(0).getCompany(), is("company"));
        assertThat(userList.get(0).getInterest(), is("interest"));
        assertThat(userList.get(0).getTeam(), is("team"));
        assertThat(userList.get(1).getName(), is("Cloud"));
        assertThat(userList.get(2).getName(), is("James"));
        assertThat(userList.get(3).getName(), is("Andres"));
        assertThat(userList.get(4).getName(), is("Yanhui"));
        assertThat(userList.get(5).getName(), is("Jason"));
        assertThat(userList.get(6).getName(), is("Varol"));
    }

    @Test
    public void shouldUseCsvServiceToOperateStreamAndParseCsvFile() throws Exception {
        File file = new File("src/test/resources/users.csv");

        StreamOperationBuilder<User> builder = new StreamOperationBuilder<>();

        Result<User> result = service.parse(file, User::new, builder.drop(1).andThen(builder.take(1)));

        assertThat(result.isSuccessful(), is(true));

        List<User> userList = result.getResult();

        assertThat(userList.size(), is(1));

        assertThat(userList.get(0).getName(), is("Cloud"));
        assertThat(userList.get(0).getCompany(), is("AusRegistry"));
        assertThat(userList.get(0).getInterest(), is("Swimming"));
        assertThat(userList.get(0).getTeam(), is("dev"));
    }

    @Test
    public void shouldUseCsvServiceToGetErrorsFromFile() throws Exception {
        File file = new File("src/test/resources/users_invalid_1_rows.csv");

        Result<User> result = service.parse(file, User::new);

        assertThat(result.isFailed(), is(true));

        List<LineErrorConverter> failureResult = result.getFailureResult();

        assertThat(failureResult.size(), is(1));

        assertThat(failureResult.get(0).getViolations().size(), is(3));

        Iterator<SimplifiedErrorContainer> iterator = failureResult.get(0).getViolations().iterator();

        SimplifiedErrorContainer company = iterator.next();

        assertThat(company.columnName(), notNullValue());
        assertThat(company.errorMessage(), is("may not be empty"));

        SimplifiedErrorContainer interest = iterator.next();

        assertThat(interest.columnName(), notNullValue());
        assertThat(interest.errorMessage(), is("may not be empty"));

        SimplifiedErrorContainer team = iterator.next();
        assertThat(team.columnName(), notNullValue());
        assertThat(team.errorMessage(), is("may not be empty"));
    }

    @Test
    public void shouldUseCsvServiceToGetSpecifiedColumnErrorMessage() throws Exception {
        File file = new File("src/test/resources/users_invalid_1_rows.csv");

        Result<User> result = service.parse(file, User::new);

        assertThat(result.isFailed(), is(true));

        String customizedErrorMessage = result.getFormattedErrorMessage();

        assertThat(customizedErrorMessage, is("Line 3 Column Company may not be empty.\n"
            + "Line 3 Column Interest may not be empty.\n"
            + "Line 3 Column Team may not be empty."));

        assertThat(result.getFormattedErrorMessage((lineNumber, columnName, errorMessage) -> "", ""), is(""));
    }

    @Test
    public void shouldUseCsvServiceAndColumnMapperToGetSpecifiedColumnErrorMessage() throws Exception {
        File file = new File("src/test/resources/users_invalid_1_rows_sorted.csv");

        Result<User> result = service.parse(file, User::new);

        assertThat(result.isFailed(), is(true));

        String customizedErrorMessage = result.getSortedFormattedErrorMessage(new SortedColumnMapper());

        assertThat(customizedErrorMessage, is("Line 3 Column User name may not be empty.\n"
                + "Line 3 Column Company may not be empty.\n"
                + "Line 3 Column Interest may not be empty.\n"
                + "Line 3 Column Team may not be empty."));
    }

    @Test
    public void shouldUseCsvServiceToShowMissingColumnsByUsingSortedFormattedErrorMessage() throws Exception {
        File file = new File("src/test/resources/users_invalid_3_missing_column.csv");

        Result<User> result = service.parse(file, User::new);

        assertThat(result.isFailed(), is(true));

        String customizedErrorMessage = result.getSortedFormattedErrorMessage(new SortedColumnMapper());

        assertThat(customizedErrorMessage, containsString("Line 3 may have missing columns"));
        assertThat(customizedErrorMessage, containsString("Line 4 may have missing columns"));
        assertThat(customizedErrorMessage, containsString("Line 5 may have missing columns"));
    }

    @Test
    public void shouldUseCsvServiceToShowMissingColumnsByUsingFormattedErrorMessage() throws Exception {
        File file = new File("src/test/resources/users_invalid_3_missing_column.csv");

        Result<User> result = service.parse(file, User::new);

        assertThat(result.isFailed(), is(true));

        String customizedErrorMessage = result.getFormattedErrorMessage();

        assertThat(customizedErrorMessage, containsString("Line 3 may have missing columns"));
        assertThat(customizedErrorMessage, containsString("Line 4 may have missing columns"));
        assertThat(customizedErrorMessage, containsString("Line 5 may have missing columns"));
    }

    @Test
    public void shouldUseCsvServiceToShowMissingColumnsAndColumnErrorMessage() throws Exception {
        File file = new File("src/test/resources/users_invalid_1_row_column_errors.csv");

        Result<User> result = service.parse(file, User::new);

        assertThat(result.isFailed(), is(true));

        String customizedErrorMessage = result.getSortedFormattedErrorMessage(new SortedColumnMapper());

        assertThat(customizedErrorMessage, containsString("Line 3 may have missing columns"));
        assertThat(customizedErrorMessage, containsString("Line 4 Column Company may not be empty."));
    }

    @Test
    public void shouldUseCsvServiceToGetErrorsFromAllTheRows() throws Exception {
        File file = new File("src/test/resources/users_invalid_3_rows.csv");

        Result<User> result = service.parse(file, User::new);

        assertThat(result.isFailed(), is(true));

        List<LineErrorConverter> failureResult = result.getFailureResult();

        assertThat(failureResult.size(), is(3));

        String formattedErrorMessage = result.getFormattedErrorMessage();

        assertThat(formattedErrorMessage, containsString("Line 3 Column Interest may not be empty."));
        assertThat(formattedErrorMessage, containsString("Line 3 Column Team may not be empty."));
        assertThat(formattedErrorMessage, containsString("Line 4 Column Interest may not be empty."));
        assertThat(formattedErrorMessage, containsString("Line 5 Column Team may not be empty."));
    }

    @Test
    public void shouldUseCsvServiceWithBufferedValidation() {
        File file = new File("src/test/resources/users.csv");

        Result<User> result = service.parse(file, User::new, 3);

        assertThat(result.isFailed(), is(false));
        assertThat(result.isSuccessful(), is(true));
    }

    @Test
    public void shouldGetBufferedValidationResult() throws Exception {
        File file = new File("src/test/resources/users_invalid_3_rows.csv");

        Result<User> result = service.parse(file, User::new, 1);

        assertThat(result.isFailed(), is(true));

        List<LineErrorConverter> failureResult = result.getFailureResult();

        assertThat(failureResult.size(), is(1));

        Result<User> overflowResult = service.parse(file, User::new, 4);

        assertThat(overflowResult.getFailureResult().size(), is(3));

    }

    @Test
    public void shouldUseCsvServiceWithStreamOperationAndBufferedValidation() throws Exception {
        File file = new File("src/test/resources/users_invalid_3_rows.csv");

        StreamOperationBuilder<User> builder = new StreamOperationBuilder<>();

        Result<User> result = service.parse(file,
                User::new,
                builder.drop(1).andThen(builder.take(1)),
                3);

        assertThat(result.isSuccessful(), is(true));

        List<User> userList = result.getResult();

        assertThat(userList.size(), is(1));

        assertThat(userList.get(0).getName(), is("Cloud"));
        assertThat(userList.get(0).getCompany(), is("AusRegistry"));
        assertThat(userList.get(0).getInterest(), is("Swimming"));
        assertThat(userList.get(0).getTeam(), is("dev"));
    }

}
