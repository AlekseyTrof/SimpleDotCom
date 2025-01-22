package components_examples;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class ExampleJCheckBox implements ItemListener {
    JCheckBox checkBox;

    public static void main(String[] args) {
        ExampleJCheckBox gui = new ExampleJCheckBox();
        gui.go();
    }

    public void go() {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        checkBox = new JCheckBox("Goes to 11");
        checkBox.addItemListener(this);

        panel.add(checkBox);

        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.getContentPane().add(BorderLayout.SOUTH, checkBox);

        frame.setSize(300, 300);
        frame.setVisible(true);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        String onOrOff = "Off";
        if (checkBox.isSelected()) {
            onOrOff = "on";
            checkBox.isSelected();
            System.out.println("Check box is " + onOrOff);
        } else {
            onOrOff = "Off";
            checkBox.isSelected();
            System.out.println("Check box is " + onOrOff);
        }
    }
}
