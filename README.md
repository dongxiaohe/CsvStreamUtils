CsvStreamUtils
==============
CsvStreamUtils is a CSV tookit written in Scala to provide a functional way to handle CSV file or stream. It can be imported in Java or Scala. In order to get rid of the boilerplate to parse CSV file in a imperative way, it allows users to use more composable way to handle CSV parsing. 

### Using CsvStreamUtils (You can use it in Java as well)
``` Java
resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"

libraryDependencies ++= Seq(
  "com.github.dannywe" % "csvstreamutils_2.10" % "1.03"
)
```

There are several features can be used for the CSV parsing

+ Composable way to handle CSV line by line
+ Error handling
+ Modularity

### The issue: The imperative way of handling CSV IO
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
+ The code is hard to reusable because of many division of the code like header validation, error limit and so on.
+ It is not a modular, so it is not composable.
+ If we add more line computation like take or drop, to achieve that would make implementation even worse.

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
+ For java 7, it can use a ```Function<String[], User>``` from <b> guawa </b> to transform a String[] to User


#### 2. Error handling
``` Java
File file = new File("src/test/resources/users_invalid_1_rows.csv");
Result<User> result = service.parse(file, User::new);
assertThat(result.isFailed(), is(true));
List<LineErrorConverter> failureResult = result.getFailureResult();
assertThat(failureResult.size(), is(1));
List<SimplifiedErrorContainer> errorContainer = failureResult.get(0).getViolations()
```
<b>SimplifiedErrorContainer</b> has <b> 3 fields </b> to specify the column error
+ lineNumber: Int
+ columnName: String
+ errorMessage: String 

#### 3. Using default error message
``` Java
The CSV file

username,company,interest,team
Cloud,AusRegistry,Swimming,dev
James,AusRegistry,,                  // error line
Andres,AusRegistry,,dev              //error line
Andrew,AusRegistry,Table tennis,     //error line
Jason,AusRegistry,Noisy,dev
Varol,AusRegistry,Table tennis,dev


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
List<LineErrorConverter> failureResult = result.getFailureResult();
String formattedErrorMessage = result.getFormattedErrorMessage();

System.out.println(formattedErrorMessage)

//Line 3 Column Interest may not be empty.
//Line 3 Column Team may not be empty.
//Line 4 Column Interest may not be empty.
//Line 5 Column Team may not be empty.

```

#### 4. Composable operation for the line

+ If we want to ignore first <b> 3 </b> rows, because there may be some comment, header or whatever.
``` Java
StreamOperationBuilder<User> builder = new StreamOperationBuilder<>();
Result<User> result = service.parse(file, User::new, builder.drop(3));
```
+ If we want to only get the <b> third </b> line, we can use <b> andThen </b> to compose it
``` Java
StreamOperationBuilder<User> builder = new StreamOperationBuilder<>();
Result<User> result = service.parse(file,
 User::new,
 builder.drop(2).andThen(builder.take(1)));
```
+ If we want to ignore <b> first 3 </b> rows and stop if to iterate if one row has user name is "James"
``` Java
StreamOperationBuilder<User> builder = new StreamOperationBuilder<>();
Result<User> result = service.parse(file,
 User::new,
 builder.drop(3).andThen(builder.takeWhile(t -> !"James".equals(t.getName))));
```

#### 5. Errors customization interface

+ Error lines can be limited
``` Java
File file = new File("src/test/resources/users_invalid_100_rows.csv");
Result<User> result = service.parse(file, User::new, 5); // Only return maximum 5 lines of error
List<LineErrorConverter> failureResult = result.getFailureResult();
assertThat(failureResult.size(), is(5)); //only return 5 lines error


```
+ Cutomize the format of error message
``` Java
//to implement ErrorLineFormatter
public class CustomizedErrorLineFormatter implements ErrorLineFormatter {
    @Override
    public String format(int lineNumber, String columnName, String errorMessage) {
        return "Column " + columnName + " in Line " + lineNumber + " has error: " + errorMessage;
    }
}
//use it
String customizedErrorMessage = result.getFormattedErrorMessage(new CustomizedErrorLineFormatter());

System.out.println(customizedErrorMessage);
//Column Interest in Line 3 has error: may not be empty
//Column Team in Line 3 has error: may not be empty
//Column Interest in Line 4 has error: may not be empty
//Column Team in Line 5 has error: may not be empty
```
