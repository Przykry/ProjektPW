package Project;

import javax.swing.*;
import java.awt.*;

public class Main {
    private static JFrame frame = new JFrame();


    public static void main(String [] args){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        frame.setBounds((int)width/5,(int)(height/11),800,600);
        frame.setTitle("Ferrys");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setFocusable(true);
        //frame.setUndecorated(true);
        frame.add(new Port(800,600));
        frame.setVisible(true);
    }
}
