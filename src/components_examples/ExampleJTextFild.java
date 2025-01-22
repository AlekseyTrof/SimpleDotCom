package components_examples;

import javax.swing.*;
import java.awt.*;

public class ExampleJTextFild {
    static JFrame frame = new JFrame("Example Field");
    static JTextField textField;

    public static void main(String[] args) {
        ExampleJTextFild fild = new ExampleJTextFild();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        textField = new JTextField("Your name", 20);

        textField.requestFocus();
        frame.getContentPane().add(BorderLayout.NORTH,textField);

        frame.setSize(400, 400);
        frame.setVisible(true);
    }
}
