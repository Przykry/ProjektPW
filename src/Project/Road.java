package Project;

import java.util.List;
import java.util.concurrent.Semaphore;

import static java.lang.Thread.sleep;

/**
 * Created by Przykry on 24.05.2017.
 */
public class Road {
    private static int MAXQUEUE = 6;
    private static final Semaphore carEnters = new Semaphore(1, true);
    private static final Semaphore ferryLeave = new Semaphore(0, true);
    private static final Semaphore queueRead = new Semaphore(1,true);
    private static final Semaphore maxQueue = new Semaphore(MAXQUEUE,true);
    private List<Ferry> ferries;
    private List<Car> cars;
    private int carIterator = 0;
    private boolean firstCar = false;
    int queue=0;

    Road(List<Ferry> ferries,List<Car> cars){
        this.ferries = ferries;
        this.cars = cars;
    }

    public void ferryIsBack(){
        ferryLeave.release(4);
    }

    public void ferryIsLeave(Ferry ferry){
        ferryLeave.tryAcquire(4 - ferry.getCarSize());
    }

    public int getCarIterator() {
        return carIterator;
    }

    Ferry getCloserFerry(){
        Ferry sth = null;
        try {
            int distance = 0;
            ferryLeave.acquire();
            maxQueue.release();
            for (Ferry ferry : ferries) {
                if (ferry.isPort() && ferry.getCarInQueue() + ferry.getCarSize() < 4) {
                    if (ferry.getY() >= distance) {
                        distance = ferry.getY();
                        sth = ferry;
                    }
                }
            }
            sth.incQueue();
        }catch (InterruptedException e){
            e.printStackTrace();
        } finally {
            return sth;
        }
    }

    void carEnterPort(Car car){
        try {
            carEnters.acquire();
            queueRead.acquire();
            maxQueue.acquire();
            car.setQueue(queue);
            queue++;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            queueRead.release();
        }
    }

    void firstCarLeft(){
        firstCar = false;
    }

    void carInPort(Car car){
        if(car.getX() == 30) {
            carEnters.release();
        }
    }


    void goToFirstFreeFerry(Car car, Ferry closestFerry){
        try {
            car.setGoToFerry(true);
            decQueue(car);
            firstCarLeft();
        } catch (NullPointerException e){
                car.setClosestFerry();
        }
    }



    public void decQueue(Car car){
        try {
            queueRead.acquire();
            queue--;
            car.setQueueReduced(true);
            for(Car c : cars) {
                if(c.getQueue()>0) c.decQueue();
            }
            queueRead.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void firstToFerry(Car car){
        if(!firstCar) {
            if (car.getX() >= 180  && car.getX() < 200) if (car.getY() >= 500) if (queueNumber(car) == 0) {
                firstCar = true;
                car.setQueueReduced(false);
                car.setFirstCar(true);
            }
        }
    }

    int queueNumber(Car car){
        try {
            queueRead.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            int q = car.getQueue();
            queueRead.release();
            return q;
        }

    }

}
