package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.control.TreeItem;

/**
 * @author Pietro
 * @param <T>
 */
public class Database<T extends DatabaseEntry> {

    private static Database instanceDatabase;
    private RandomAccessFile raf;
    private Map<T, Integer> index;
    private final int lengthField;
    private final int lengthRow;
    private String emptyRow;
   

    // Singleton pattern
    public static Database getInstance(String fileName, int lenghtField, int numFields) {
        if (instanceDatabase == null) {
            instanceDatabase = new Database<>(fileName, lenghtField, numFields);
        }
        return instanceDatabase;
    }

    // The private constructor allow only one instance
    private Database(String fileName, int lengthField, int numFields) {
        this.lengthField = lengthField;
        this.lengthRow = (lengthField + 1) * numFields + 3;
        emptyRow = "";
        for (int i = 0; i < numFields; i++) {
            for (int j = 0; j < lengthField; j++) {
                emptyRow += " ";
            }
            emptyRow += ",";
        }

        index = new HashMap<>();
        try {
            raf = new RandomAccessFile(new File(fileName), "rw");
        } catch (FileNotFoundException ex) {
            System.err.println("The file was not found!");
        }
    }

    // Get row at the index i, O(1)
    private String getRow(int i) {
        String readLine = null;
        try {
            raf.seek(i != 0 ? 2 + lengthRow * i : 2);
            readLine = raf.readLine();
        } catch (IOException ex) {
        }
        return readLine;
    }

    // Write row at the index i, O(1)
    private void writeRow(int i, String row) {
        try {
            raf.seek(i != 0 ? lengthRow * i : 0);
            raf.writeUTF(row + "\n");
        } catch (IOException ex) {
        }
    }

    // Create row in the first empty place, O(n)
    public void createRow(T p) {
        boolean loop = true;
        int i = 0;
        do {
            String getRow = getRow(i);
            if (getRow == null || getRow.equals(emptyRow)) {
                loop = false;
                writeRow(i, p.formatFileds(lengthField));
                index.put(p, i);
            }
            i++;
        } while (loop);
    }

    // Update row, O(1)
    public void updateRow(T old, T edit) {
        Integer i = index.get(old);
        writeRow(i, edit.formatFileds(lengthField));
    }

    // Delete row, O(1)
    public void deleteRow(T p) {
        Integer i = index.get(p);
        writeRow(i, emptyRow);
    }

    // Fill a list with all rows in the database
    public List<TreeItem<T>> getTreeList(Class<T> clazz) {
        List<TreeItem<T>> list = new ArrayList<>();
        index.clear();
        boolean loop = true;
        int i = 0;
        do {
            String getRow = getRow(i);
            if (getRow == null) {
                break;
            } else {
                String[] split = getRow.replace(" ", "").replace("-", " ").split(",");
                if (split.length > 1) {
                    try {
                        T p = clazz.newInstance();
                        p.setFields(split);
                        list.add(new TreeItem<>(p));
                        index.put(p, i);
                    } catch (InstantiationException | IllegalAccessException ex) {
                    }
                }
            }
            i++;
        } while (loop);
        return list;
    }

    // Close the file
    public void close() {
        try {
            raf.close();
        } catch (IOException ex) {

        }
    }

    @Override
    public String toString() {
        String result = "";
        boolean loop = true;
        int i = 0;
        do {
            String getRow = getRow(i++);
            if (getRow == null) {
                loop = false;
            } else {
                result += getRow + "\n";
            }
        } while (loop);
        return result.replace(" ", "");
    }
}
