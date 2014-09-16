package integration;

import org.hibernate.validator.constraints.NotEmpty;

public class User {

    @NotEmpty
    private String name;
    @NotEmpty
    private String company;
    @NotEmpty
    private String interest;
    @NotEmpty
    private String team;

    public User(String[] input) {
        this.name = input[0];
        this.company = input[1];
        this.interest = input[2];
        this.team = input[3];
    }

    public String[] produce() {
        return new String[] {name, company, interest, team};
    }


    public String getName() {
        return name;
    }

    public String getCompany() {
        return company;
    }

    public String getInterest() {
        return interest;
    }

    public String getTeam() {
        return team;
    }
}
