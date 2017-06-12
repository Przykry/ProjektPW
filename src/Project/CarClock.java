package Project;

import java.util.Random;

import static java.lang.Thread.sleep;

/**
 * Created by Przykry on 29.05.2017.
 */
public class CarClock implements Runnable {
    Random rand = new Random();


    @Override
    public void run() {
        for(int i = 0; i < Port.getCarNumber() ; i++) {
            try {
                sleep(rand.nextInt(530) + 100);
                Port.carArrive();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
