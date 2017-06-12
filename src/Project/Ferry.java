package Project;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by Przykry on 24.05.2017.
 */
public class Ferry implements Runnable {
    int x;
    int y;
    boolean transport = false;
    boolean carsOnFerry = false;
    boolean waitForCars = true;
    boolean itsTime=false;
    int carInQueue=0;
    List<Car> cars = new ArrayList<>();
    FerryClock ferryClock = new FerryClock(this);
    Thread ferryClockThread = new Thread(ferryClock);
    Road road;
    ExitRoad exitRoad;

    int numberCarsInFerry(){return cars.size();}

    void backOff(){
        if(!isPort()) x-=5;;
    }

    void carInFerry(Car car){
        cars.add(car);
    }

    Car firstCar(){
        return cars.get(0);
    }

    Car carLeaveFerry(){
       return cars.remove(0);
    }

    public int getY() {return y;}

    public int getX() {return x;}

    public void setItsTime(boolean itsTime) {
        this.itsTime = itsTime;
    }

    public boolean getItsTime() {
        return itsTime;
    }

    public void incQueue(){carInQueue++;}

    public void decQueue(){carInQueue--;}

    public int getCarInQueue() {return carInQueue;}

    public int getCarSize() {return cars.size();}

    Ferry(int x, int y,Road road,ExitRoad exitRoad){
        this.x = x;
        this.y = y;
        this.transport = false;
        this.road = road;
        this.exitRoad = exitRoad;
        ferryClockThread.setName("FerryClock");
        ferryClockThread.start();
    }

    void goingToOtherSide(){
        if(x == 450) {
            carsOnFerry = false;
        }
        else {
            for (Car car:cars) {
                car.stayOnFerry();
            }
            x+=5;
        }
    }

    boolean isShore(){
        return  x == 450;
    }

    boolean isFull(){
        if(cars.size() == 4) {
            readyToTransport(true);
            return true;
        }
        else return false;
    }

    boolean isEmpty(){
        if(cars.size() == 0) return true;
        else return false;
    }

    void readyToTransport(boolean transport){
        if(transport) {
            this.transport = true;
            this.carsOnFerry = true;
            road.ferryIsLeave(this);
        }
        else {
            this.transport = false;
            this.carsOnFerry = false;
        }
    }

    void drawFerry(Graphics g){
        g.fillRect(x,y,140,35);
    }

    @Override
    public void run() {
        while (true){
            try {
                sleep(40);

                if(isEmpty() && !isPort()) backOff();
                else if(waitForCars && isPort()) {
                    waitForCars = false;
                    road.ferryIsBack();
                    readyToTransport(false);
                    itsTime = false;
                }
                else if (transport) {
                    if (carsOnFerry) goingToOtherSide();
                    else {
                        exitRoad.carOnRoad(this);
                    }
                } else {
                    readyToTransport(isFull() || (!isEmpty() && itsTime && carInQueue == 0));
                }
            }catch (InterruptedException e){ e.printStackTrace();}
        }
    }

    public int freeParkingPlace() {
        return this.x + 105 - cars.size() * (Car.getRadius() + 5);
    }

    public boolean isPort() {
        return x == 210;
    }

    public void setWaitForCars(boolean waitForCars) {
        this.waitForCars = waitForCars;
    }
}
