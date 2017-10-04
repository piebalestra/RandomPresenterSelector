package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Pietro
 * @param <T>
 */
public class ReadCSV<T extends DatabaseEntry> {

    String fileName;
    List<T> list;

    public ReadCSV(String fileName, Class clazz) {
        this.fileName = fileName;
        this.list = new ArrayList<>();
        parseFile(clazz);
    }

    private void parseFile(Class<T> clazz) {
        try {
            Stream<String> stream = Files.lines(Paths.get(fileName));
            stream.forEach((String l) -> {
                String[] split = l.split(",");
                try {
                    T p = clazz.newInstance();
                    p.setFields(split);
                    list.add(p);
                } catch (InstantiationException | IllegalAccessException ex) {
                }

            });
        } catch (IOException ex) {
            System.err.println("The file " + fileName + " has been moved.");
        }
    }

    public String getFileName() {
        return fileName;
    }

    public List<T> getList() {
        return list;
    }
}
