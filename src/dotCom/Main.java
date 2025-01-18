package dotCom;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        int numOfGuess = 0;
        GameHelper helper = new GameHelper();

        DotCom theDotCom = new DotCom();
        int random = (int) (Math.random() * 17);

        ArrayList<String> loc = new ArrayList<>();
        loc.add(random + "");
        loc.add((random + 1) + "");
        loc.add((random + 2) + "");
        theDotCom.setLocationCells(loc);
        boolean isAlive = true;

        while (isAlive) {
            String guess = helper.getUserInput("Введите число:");
            String result = theDotCom.checkYourself(guess);
            numOfGuess++;
            if (result.equals("Потопил(а)")) {
                isAlive = false;
                System.out.println(result);
                System.out.println("Вам потребовалось " + numOfGuess + " попыток(и)");
            } else {
                System.out.println(result);
            }
        }
    }
}