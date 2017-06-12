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
    private static final Semaphore carEnters[] = new Semaphore[Port.getFerriesNumber()];
    private static final Semaphore letCarGo[] = new Semaphore[Port.getFerriesNumber()];
    private static final Semaphore ferryPalceCheack = new Semaphore(1, true);
    private static final Semaphore carDrive = new Semaphore(1, true);
    int i=0;
    private int carInIntersection[] = new int[Port.getFerriesNumber()];

    ExitRoad(List<Ferry> ferries) {
        ExitRoad.ferries = ferries;
        for(int i=0;i<Port.getFerriesNumber();i++){
            letCarGo[i] = new Semaphore(4,true);
            carEnters[i] = new Semaphore(1,true);
            carInIntersection[i] = 0;
        }
    }

    void carOnRoad(Ferry ferry) {
        try {
            ferryPalceCheack.acquire();
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
        ferryPalceCheack.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
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


    int carCloserFerry(Car car){
        int ferryNumber = 0;
        try {
            ferryPalceCheack.acquire();
            ferryNumber = (car.getY())/140;
            if(ferries.size() * 100 + 60 < car.getY()) ferryNumber = -1;
            ferryPalceCheack.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            return ferryNumber;
        }
    }

    void letCarFirst(int currentFerry) {
            try {
                carEnters[currentFerry].acquire();
                for(int i = currentFerry; i < Port.getFerriesNumber(); i++) {
                    letCarGo[i].tryAcquire(4);
                    carInIntersection[i]++;
                    //System.out.println(carInIntersection[i]  + "  " + i );
                }
                carEnters[currentFerry].release();
            } catch (InterruptedException e) {}
    }

    void carOutOfIntersection(int currentFerry){

            try {
                carEnters[currentFerry].acquire();
                carInIntersection[currentFerry]--;
               // System.out.println(carInIntersection[currentFerry]);
                if (carInIntersection[currentFerry] == 0) letCarGo[currentFerry].release(4);
                carEnters[currentFerry].release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    void carOnRoad(int currentFerry){
        letCarGo[currentFerry].release();
    }


    void  tryEscape(int currentFerry){
        try{
            letCarGo[currentFerry].acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void carDive(){
        try {
            carDrive.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void carWait(){
        carDrive.release();
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
