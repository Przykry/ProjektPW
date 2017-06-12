package Project;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;


/**
 * Created by Przykry on 26.05.2017.
 */
public class Port extends JPanel implements ActionListener {
    int width,heigth;
    static int carNumber;
    static int ferriesNumber;
    Image backgroundImage;
    List<Ferry> ferries = new ArrayList<>();
    static List<Car> cars = new ArrayList<>();
    Thread ferriesThread[] = new Thread[7];
    static Thread[] carsThread = new Thread[120];
    Timer refresh = new Timer(60,this);
    static Road road;
    static ExitRoad exitRoad;
    static int carIterator = 0;
    static int distanceBetweenFerries = 100;

    public static int getDistanceBetweenFerries() {return distanceBetweenFerries;}

    public static int getFerriesNumber() {
        return ferriesNumber;
    }

    public Port(int width, int height){
        this.width = width;
        this.heigth = height;
        this.carNumber = 300;
        this.ferriesNumber = 3;
        road = new Road(ferries,cars);
        exitRoad = new ExitRoad(ferries);
        try {
            this.backgroundImage = getBackgroundImage("background");
        }
        catch(IOException e){
            e.printStackTrace();
        }
        for(int i=0;i<carNumber;i++) {
            cars.add(new Car(-Car.getRadius(), 530,road,exitRoad));
        }
        for(int i=1;i<=ferriesNumber;i++){
            ferries.add(new Ferry(440 - i *10 , distanceBetweenFerries * i,road,exitRoad));
        }
        road = new Road(ferries,cars);
        for(int i=0;i<ferriesNumber;i++) {
            ferriesThread[i] = new Thread(ferries.get(i));
            ferriesThread[i].setName("prom" + i);
            ferriesThread[i].start();
        }
        Thread t = new Thread(new CarClock());
        t.setName("carClock");
        t.start();
        this.setFocusable(true);
        this.setLayout(null);
        refresh.start();
    }

    Image getBackgroundImage(String nameBackground) throws IOException{
        return ImageIO.read(new File(nameBackground + ".png"));
    }


    static void carArrive(){
        Thread t = new Thread(cars.get(carIterator++));
        t.setName("car" + carIterator);
        t.start();
    }



    public void paintComponent(Graphics graphics) {
        Toolkit.getDefaultToolkit().sync();
        graphics.drawImage(backgroundImage,0,0, this);
        for(Ferry ferry :ferries) ferry.drawFerry(graphics);
        for(Car car : cars) car.drawCar(graphics);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    public static int getCarNumber() {
        return carNumber;
    }
}