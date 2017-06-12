package Project;

import static java.lang.Thread.sleep;

/**
 * Created by Przykry on 27.05.2017.
 */
public class FerryClock implements Runnable{ {
}
    Ferry ferry;
    public FerryClock(Ferry ferry){
        this.ferry = ferry;
    }

    @Override
    public void run() {
        while(true) {
            try {
               if(!ferry.getItsTime()) sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
                ferry.setItsTime(true);
            try {
                sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
