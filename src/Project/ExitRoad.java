package Project;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import static Project.Port.*;
import static java.lang.Thread.sleep;

/**
 * Created by Przykry on 27.05.2017.
 */
public class ExitRoad {
    private static List<Ferry> ferries;
    private static List<Car> cars = new ArrayList<>();
    private static final Semaphore carEnters = new Semaphore(1, true);
    private static final Semaphore letCarGo[] = new Semaphore[Port.getFerriesNumber()];
    private static final Semaphore ferryPalceCheack = new Semaphore(1, true);
    int i=0;
    private int carInIntersection = 0;

    ExitRoad(List<Ferry> ferries) {
        ExitRoad.ferries = ferries;
        for(int i=0;i<Port.getFerriesNumber();i++){
            letCarGo[i] = new Semaphore(1,true);
        }
    }

    void carOnRoad(Ferry ferry) {
            if (ferry.isShore()) {
                if (ferry.numberCarsInFerry() >= 1) {
                    if (ferry.firstCar().getX() >= 610) {
                        Car car = ferry.carLeaveFerry();
                        cars.add(car);
                    }
                }
                if (ferry.numberCarsInFerry() == 0) {
                    ferry.setWaitForCars(true);
                    ferry.backOff();
                }
            }
    }

    boolean ferryIsOnShore(Ferry cFerry){
        boolean isShore = false;
        try {
            ferryPalceCheack.acquire();
            if(cFerry.isShore()) isShore = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            ferryPalceCheack.release();
            return isShore;
        }
    }


    int carCloserFerry(Car car, Ferry ferry){
        int ferryNumber = 0;
        try {
            ferryPalceCheack.acquire();
            ferryNumber = (car.getY())/125;
            if(ferries.size() * 100 + 50 < car.getY()) ferryNumber = -1;
            ferryPalceCheack.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            return ferryNumber;
        }
    }

    void letCarFirst(Car car, Ferry cFerry) {
            int currentFerry = carCloserFerry(car, cFerry);
            try {
                carEnters.acquire();
                letCarGo[currentFerry].tryAcquire();
                carInIntersection++;
                carEnters.release();
            } catch (InterruptedException e) {}
    }

    void carOutOfIntersection(Car car, Ferry cFerry){
            int currentFerry = carCloserFerry(car, cFerry);
            try {
                carEnters.acquire();
                carInIntersection--;
                System.out.println(carInIntersection);
                if (carInIntersection == 0) letCarGo[currentFerry].release();
                carEnters.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    void carOnRoad(Car car,Ferry cFerry){
        letCarGo[ferries.indexOf(cFerry)].release();
    }


    void  tryEscapeFerry(Car car,Ferry cFerry){
        try{
            letCarGo[ferries.indexOf(cFerry)].acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//            if (ferries.size() * 100 + 40 > car.getY()) {
//                int currentFerry = carCloserFerry(car, cFerry);
//                if (carNearFerry(car,currentFerry) && car.getAvailableFerry(currentFerry) == 0 && !cFerry.equals(ferries.get(currentFerry)) && car.isWasPassedFerry()) {
//                    letCarGo[currentFerry].tryAcquire();
//                } else if (car.getY() + 30 >= ferries.get(currentFerry).getY() && car.getCurrentFerry() < currentFerry && letCarGo[currentFerry].availablePermits()<1) {
//                    letCarGo[currentFerry].release();
//                    car.setAvailableFerry(currentFerry);
//                    car.setCurrentFerry(currentFerry);
//                }
//                System.out.println(currentFerry + "  " + letCarGo[currentFerry].availablePermits());
//            }


}
