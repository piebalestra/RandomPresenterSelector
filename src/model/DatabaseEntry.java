package model;

/**
 * @author Pietro
 */
public interface DatabaseEntry {

    // Set all fields values.
    void setFields(String[] fields);
    
    // Bind and format all fields in a row.
    String formatFileds(int lenghtField);

}
