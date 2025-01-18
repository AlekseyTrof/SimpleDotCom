package dotCom;

import java.util.ArrayList;

public class DotCom {
    private ArrayList<String> locationCells;
    private String result = "Мимо";

    public void setLocationCells(ArrayList<String> location) {
        locationCells = location;
    }

    public String checkYourself(String userInput) {
        int index = locationCells.indexOf(userInput);
        if (index >= 0) {
            locationCells.remove(index);
            if (locationCells.isEmpty()) {
                this.result = "Потопил(а)";
            } else {
                this.result = "Попал(а)";
            }
        }
        return this.result;
    }
}
