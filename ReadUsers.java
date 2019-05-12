import java.util.*;
import java.io.*;

public class ReadUsers {

    // This method reads the txt and returns a HashMap
    public static HashMap<String, Integer> UserValidation(String path) {
        File file = null;
        Scanner input = null;
        try {
            file = new File(path);
            input = new Scanner(file);
            input.useDelimiter("-\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        HashMap<String, Integer> validation = new HashMap();
        while (input.hasNextLine()) {
            String data = input.nextLine();
            String username = data.split(" ")[0];
            int password = Integer.parseInt(data.split(" ")[1]);
            validation.put(username, password);
        }
        return validation;
    }
}
