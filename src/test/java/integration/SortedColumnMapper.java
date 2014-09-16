package integration;

import com.google.common.collect.Lists;
import com.github.dannywe.csv.mapper.Mapper;
import com.github.dannywe.csv.format.SortedColumn;

import java.util.List;

public class SortedColumnMapper implements Mapper<String, SortedColumn> {


    List<Container> list = Lists.newArrayList(new Container(1, "name", "User name"),
            new Container(2, "company", "Company"),
            new Container(3, "interest", "Interest"),
            new Container(4, "team", "Team"));

    public SortedColumn mapTo(String x) {

        for (Container container : list) {
            if (container.fieldName.equals(x)) {
                return new SortedColumn(container.columnNumber, container.columnName);
            }
        }

        throw null;
    }
}

class Container {

    int columnNumber;
    String fieldName;
    String columnName;

    Container(int columnNumber, String fieldName, String columnName) {
        this.columnNumber = columnNumber;
        this.fieldName = fieldName;
        this.columnName = columnName;
    }

}
