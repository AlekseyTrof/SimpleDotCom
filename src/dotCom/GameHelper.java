package dotCom;

import java.io.*;

public class GameHelper {
    public String getUserInput(String prompt) {
        String inputLine = null;
        System.out.print(prompt + " ");
        try {
            BufferedReader is = new BufferedReader(new InputStreamReader(System.in));
            inputLine = is.readLine();
            if (inputLine.isEmpty()) {
                return null;
            }
        }
        catch (IOException e) {
            System.out.println("IOException:" + e);
        }
        return inputLine;
    }
}
