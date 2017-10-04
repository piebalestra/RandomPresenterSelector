package model;

/**
 * @author Pietro
 */
public class Person implements DatabaseEntry {

    private String firstName;
    private String lastName;
    private String date;
    private Boolean assent;

    public Person() {
        this.firstName = "";
        this.lastName = "";
        this.date = "";
        this.assent = false;
    }

    public Person(String firstName, String lastName, String date) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
        this.assent = false;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean isAssent() {
        return assent;
    }

    public void setAssent(Boolean assent) {
        this.assent = assent;
    }

    @Override
    public String formatFileds(int lenghtField) {
        return String.format(String.format("%1$-" + lenghtField + "s", firstName.replace(",", "").substring(0, firstName.length() > lenghtField ? lenghtField : firstName.length()))) + ","
              + String.format(String.format("%1$-" + lenghtField + "s", lastName.replace(",", "").substring(0, lastName.length() > lenghtField ? lenghtField : lastName.length()))) + ","
              + String.format(String.format("%1$-" + lenghtField + "s",     date.replace(" ", "-").substring(0, date.length() > lenghtField ? lenghtField : date.length()))) + ",";
    }

    @Override
    public void setFields(String[] fields) {
        if (fields.length > 1) {
            setFirstName(fields[0]);
            setLastName(fields[1]);
            setDate(fields.length > 2 ? fields[2] : "");
        }
    }

    @Override
    public String toString() {
        return "Person{" + "firstName=" + firstName + ", lastName=" + lastName + ", date=" + date + '}';
    }
}
