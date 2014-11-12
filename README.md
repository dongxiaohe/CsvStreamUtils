CsvStreamUtils
==============
CsvStreamUtils is a CSV toolkit written in Scala to provide a functional way to handle CSV files or streams. It can be imported in Java or Scala. In order to get rid of the boilerplate to parse CSV file in a imperative way, it allows users to use more composable way to handle CSV parsing. 

### Using CsvStreamUtils (You can use it in Java as well)

    resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"

    libraryDependencies ++= Seq(
      "com.github.dannywe" % "csvstreamutils_2.10" % "1.03"
    )

There are several features that can be used for the CSV parsing

+ A composable way to handle CSV parsing line by line
+ Error handling
+ Modularity

### The issue: The imperative way of handling CSV IO

```java
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


#### There are several issues related to this implementation. 

+ The code is hard to reuse because of many division of the code like header validation, error limit and so on.
+ It is not modular, so it is not composable.
+ If we add more line computation such as take or drop, the implementation would be even worse.

### A composable way of handling CSV IO in CsvStreamUtils

#### 1. Transform CSV content to a List<T>

```java
CsvService service = new CsvService();
File file = new File("src/test/resources/users.csv");
Result<User> result = service.parse(file, User::new);
if (result.isSuccessful()) {
   List<User> userList = result.getResult();
}
```

+ `User::new` is Java 8 method reference syntax to the constructor. In this example it acts as a factory for CsvStreamUtils to construct `User` objects from arrays of `String`.
+ For use in Java 7, replace this reference with an instance of ```Function<String[], User>``` from [Guava](https://github.com/google/guava) to transform a `String[]` to a `User`.


#### 2. Error handling

```java
File file = new File("src/test/resources/users_invalid_1_rows.csv");
Result<User> result = service.parse(file, User::new);
assertThat(result.isFailed(), is(true));
List<LineErrorConverter> failureResult = result.getFailureResult();
assertThat(failureResult.size(), is(1));
List<SimplifiedErrorContainer> errorContainer = failureResult.get(0).getViolations()
```

The `SimplifiedErrorContainer` has 3 fields to specify errors:

1. `lineNumber: Int`
2. `columnName: String`
3. `errorMessage: String`


#### 3. Parsing and collecting errors

Given the CSV file:

    username,company,interest,team
    Cloud,AusRegistry,Swimming,dev
    James,AusRegistry,,                  // error line
    Andres,AusRegistry,,dev              // error line
    Andrew,AusRegistry,Table tennis,     // error line
    Jason,AusRegistry,Noisy,dev
    Varol,AusRegistry,Table tennis,dev

And the `User` class:

```java
public class User {
    @NotEmpty
    private String name;
    @NotEmpty
    private String company;
    @NotEmpty
    private String interest;
    @NotEmpty
    private String team;
    // ...
}
```
    
We can parse the file like so:

```java
File file = new File("src/test/resources/users_invalid_3_rows.csv");
Result<User> result = service.parse(file, User::new);
```
    
And extract all errors:

```java
List<LineErrorConverter> failureResult = result.getFailureResult();
String formattedErrorMessage = result.getFormattedErrorMessage();

System.out.println(formattedErrorMessage)

//Line 3 Column Interest may not be empty.
//Line 3 Column Team may not be empty.
//Line 4 Column Interest may not be empty.
//Line 5 Column Team may not be empty.
```


#### 4. Operations for row parsing

Dropping rows:

```java
// the first 3 rows are part of the header
StreamOperationBuilder<User> builder = new StreamOperationBuilder<>();
Result<User> result = service.parse(file, User::new, 
    builder.drop(3)
);
```

Limiting rows parsed:

```java
// the info is in the first line
builder.take(1);
```
    
Parsing rows whilst a predicate holds true:

```java
// we don't want to know about James
builder.takeWhile(user -> !"James".equals(user.getName));
```
    
And finally, Composing operations to target specific rows:

```java
// the info is on line 3
builder.drop(3).andThen(builder.takeWhile(t -> !"James".equals(t.getName))));
```


#### 5. Customisation of errors

Limiting the number of errors:

```java
File file = new File("src/test/resources/users_invalid_100_rows.csv");
Result<User> result = service.parse(file, User::new, 5); // Only return maximum 5 lines of error
List<LineErrorConverter> failureResult = result.getFailureResult();
assertThat(failureResult.size(), is(5)); //only return 5 lines error
```

Customise the format of error messages

```java
//to implement ErrorLineFormatter
public class CustomizedErrorLineFormatter implements ErrorLineFormatter {
    @Override
    public String format(int lineNumber, String columnName, String errorMessage) {
        return "Column " + columnName + " in Line " + lineNumber + " has error: " + errorMessage;
    }
}

// usage
String customizedErrorMessage = result.getFormattedErrorMessage(new CustomizedErrorLineFormatter());

System.out.println(customizedErrorMessage);

//Column Interest in Line 3 has error: may not be empty
//Column Team in Line 3 has error: may not be empty
//Column Interest in Line 4 has error: may not be empty
//Column Team in Line 5 has error: may not be empty
```

