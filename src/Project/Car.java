package Project;

import java.awt.*;
import java.util.Random;

import static java.lang.Thread.sleep;

/**
 * Created by Przykry on 24.05.2017.
 */
public class Car implements Runnable {
    private int x,y;
    private static int radius = 30;
    private int queue;
    private boolean isPort,carOnFerry,firstCar,drive = true,carStillOnFerry = false;
    private Ferry closestFerry = null;
    Random randomGenerator = new Random();
    int red = randomGenerator.nextInt(256);
    int green = randomGenerator.nextInt(256);
    int blue = randomGenerator.nextInt(256);
    Color randomColour = new Color(red,green,blue);
    Road road;
    ExitRoad exitRoad;
    private boolean parkInFerry = false;
    private boolean goToFerry = false;
    private boolean canGoAway = false;
    private boolean queueReduced  = false;
    private boolean firstStep = true;
    private boolean wasPassedFerry = false;
    private int currentFerry = -1;
    private int availableFerry[] = new int[Port.getFerriesNumber()];
    private int ferryIterator;
    private boolean carOnRoad = true;

    public void move(int direction){
        if(direction == 1) x+=2;
        else if(direction == -1) x-=2;
        else if(direction == 2) y+=2;
        else if(direction == -2) y-=2;
    }

    Car(int x, int y,Road road,ExitRoad exitRoad){
        this.x = x;
        this.y = y;
        drive = false;
        this.road = road;
        this.exitRoad = exitRoad;
        for(int i : availableFerry){
            i=0;
        }
    }


    void thisIsPort(){
        if(x == 180) {
            isPort = true;
        }
    }

    void goAway(){
        if(x < 590 + radius || this.y >= 530) move(1);
        else if(this.y < 530) {
            move(2);
        }
    }

    void setClosestFerry(){closestFerry = road.getCloserFerry();}


    public void drawCar(Graphics gc){
        gc.setColor(randomColour);
        gc.fillOval(x,y,radius,radius);
    }

    boolean itsQueue(){
        return x <= 180 && 180 - road.queueNumber(this) * radius >= x;
    }


    void firstStep(){
        if (itsQueue()) {
            thisIsPort();
            move(1);
            road.carInPort(this);
            road.firstToFerry(this);
        } else if (firstCar && y > 500 && !canGoAway) {
            move(-2);
        } else if (firstCar && y <= 500 && closestFerry == null) {
            setClosestFerry();
            road.goToFirstFreeFerry(this, closestFerry);
            firstStep = false;
        }
    }

    void secondStep() throws InterruptedException{
        if (goToFerry) {
            if (this.getY() <= closestFerry.getY()) {
                goToFerry = false;
                this.isParkInFerry(true);
            } else move(-2);
        } else if (parkInFerry) {
            if (x >= closestFerry.freeParkingPlace()) {
                closestFerry.carInFerry(this);
                this.setCarOnFerry(true);
                parkInFerry = false;
                canGoAway = true;
                carStillOnFerry = true;
                closestFerry.decQueue();
                while (!exitRoad.ferryIsOnShore(closestFerry)) sleep(30);
            } else move(1);
        }
    }

    @Override
    public void run() {
        road.carEnterPort(this);
        while(true) {
            try {
                sleep(10);
                if(firstStep) {
                    firstStep();
                }
                else if(!canGoAway) {
                    secondStep();
                }
                else if (canGoAway) {
                    int currentFerry = exitRoad.carCloserFerry(this);
                    if(carStillOnFerry) {
                        exitRoad.tryEscape(currentFerry);
                        exitRoad.carOnRoad(currentFerry);
                        carStillOnFerry = false;
                    }
                    else {
                        if(currentFerry > -1 && !wasPassedFerry) {
                            ferryIterator = currentFerry;
                            exitRoad.letCarFirst(currentFerry);
                            wasPassedFerry = true;
                        }
                        else if(currentFerry * 100 + 35  < y && currentFerry > -1 && availableFerry[currentFerry] == 0 ){
                            exitRoad.carOutOfIntersection(currentFerry);
                            availableFerry[currentFerry] = 1;
                        }
                        goAway();
                    }
                    if (x > 810) {
                        break;
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Ferry getClosestFerry() {
        return closestFerry;
    }

    public static int getRadius() {
        return radius;
    }

    public void setCanGoAway(boolean canGoAway) {
        this.canGoAway = canGoAway;
    }

    public boolean getCarOnFerry() {
        return carOnFerry;
    }

    public void setCarOnFerry(boolean carOnFerry) {
        this.carOnFerry = carOnFerry;
    }

    public void isParkInFerry(boolean parkInFerry) {
        this.parkInFerry = parkInFerry;
    }

    public void setQueueReduced(boolean queueReduced) {
        this.queueReduced = queueReduced;
    }

    public boolean isWasPassedFerry() {
        return wasPassedFerry;
    }

    public void setWasPassedFerry(boolean wasPassedFerry) {
        this.wasPassedFerry = wasPassedFerry;
    }

    public boolean isCarOnFerry() {
        return carOnFerry;
    }

    public void setQueue(int queue) {
        this.queue = queue;
    }

    public int decQueue() {
        return queue--;
    }

    public int getX() {return x;}

    public void setFirstCar(boolean firstCar) {
        this.firstCar = firstCar;
    }

    public int getY() {
        return y;
    }

    public void stayOnFerry(){
        x+=5;
    }

    public int getAvailableFerry(int i) {
        return availableFerry[i];
    }

    public void setAvailableFerry(int i) {
        this.availableFerry[i] = 1;
    }

    public int getCurrentFerry() {
        return currentFerry;
    }

    public void setCurrentFerry(int currentFerry) {
        this.currentFerry = currentFerry;
    }

    public boolean isGoToFerry() {
        return goToFerry;
    }

    public void setGoToFerry(boolean goToFerry) {
        this.goToFerry = goToFerry;
    }

    public void setDrive(boolean drive) {
        this.drive = drive;
    }

    public int getQueue() {
        return queue;
    }
}
