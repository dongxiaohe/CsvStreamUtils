CsvStreamUtils
==============
CsvStreamUtils is a CSV tookit written in Scala to provide a functional way to handle CSV file or stream for Java or Scala. In order to get ride of boilerplate to parse CSV file in a imperative way, it allows users to use more composable way to handle CSV parsing. 

+ Composable way to handle CSV line
+ Limited error
+ Modularity

### The problem: The imperative way of handling CSV IO
``` Java
List<ViewObject> voList = Lists.newArrayList();
try (CSVReader csvReader = csvFactory.createReader(csv)) {
  String[] csvColumns;
  int count = 0;
  csvColumns = csvReader.readNext();
  
  validateHeader(csvColumns, headers);
  
  csvPersistentStrategy.validationContext();
  //you want to have error limit not return all the errors
  while (errors.size() <= ERROR_LIMIT 
   && (csvColumns = csvReader.readNext()) != null) {
      errors.addAll(csvLineValidator.validateCsvLine(csvColumns,
      lineNo, validationContext));
    
      lineNo++;
      voList.add(factory.create(csvColumns));
  } catch (IOException ex) {
    //error handling
  }
}
```
####There are several issues related to this implementation. 
+ The code is hard to reusable because of many division of the code like validation of header, error limit and so on.
+ It is not a modular, so it is not composable.
+ If we add more line computation like take or drop, this implementation would be even worse.

### A composable way of handling CSV IO in CsvStreamUtils

#### 1. Transform CSV content to a List<T>
``` Java
CsvService service = new CsvService();
File file = new File("src/test/resources/users.csv");
Result<User> result = service.parse(file, User::new);
if (result.isSuccessful()) {
   List<User> userList = result.getResult();
}
```
+ User::new is java 8 lamda, so it basically tell CsvStreamUtils how to construct User object from an array of String.
+ For java 7, it can use a ```Function<String[], User>```


#### 2. Error handling
``` Java
File file = new File("src/test/resources/users_invalid_1_rows.csv");
Result<User> result = service.parse(file, User::new);
assertThat(result.isFailed(), is(true));
List<LineErrorConverter> failureResult = result.getFailureResult();
assertThat(failureResult.size(), is(1));
List<SimplifiedErrorContainer> errorContainer = failureResult.get(0).getViolations()

//SimplifiedErrorContainer has fields
+ lineNumber: Int
+ columnName: String
+ errorMessage: String 

to specify the column error

```

#### 3. Error handling with default formatted string
``` Java
User.class

public class User {

    @NotEmpty
    private String name;
    @NotEmpty
    private String company;
    @NotEmpty
    private String interest;
    @NotEmpty
    private String team;
  
  
  ...
}

File file = new File("src/test/resources/users_invalid_3_rows.csv");
Result<User> result = service.parse(file, User::new);
assertThat(result.isFailed(), is(true));
List<LineErrorConverter> failureResult = result.getFailureResult();
String formattedErrorMessage = result.getFormattedErrorMessage();
System.out.println(formattedErrorMessage)
//Line 3 Column Interest may not be empty.
//Line 3 Column Team may not be empty.
//Line 4 Column Interest may not be empty.
//Line 5 Column Team may not be empty.

```

#### 4. Stream operation builder applied for the line

+ If we want to ignore first 3 rows, because there may be some comment, header or whatever.
``` Java
StreamOperationBuilder<User> builder = new StreamOperationBuilder<>();
Result<User> result = service.parse(file, User::new, builder.drop(3));
```
+ If we want to get the <b> third </b> line, we can use andThen to compose it
``` Java
StreamOperationBuilder<User> builder = new StreamOperationBuilder<>();
Result<User> result = service.parse(file,
 User::new,
 builder.drop(2).andThen(builder.take(1)));
```
+ If we want to ignore first 3 rows and stop if to iterate if row has user name is "James"
``` Java
StreamOperationBuilder<User> builder = new StreamOperationBuilder<>();
Result<User> result = service.parse(file,
 User::new,
 builder.drop(3).andThen(builder.takeWhile(t -> !"James".equals(t.getName))));
```

#### 5. Limited error

+ only return maximum 5 lines of error intead of all
``` Java
File file = new File("src/test/resources/users_invalid_100_rows.csv");
Result<User> result = service.parse(file, User::new, 5);
assertThat(result.isFailed(), is(true));
List<LineErrorConverter> failureResult = result.getFailureResult();
assertThat(failureResult.size(), is(1));


```
