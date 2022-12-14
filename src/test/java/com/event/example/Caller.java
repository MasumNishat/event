package com.event.example;

import org.nishat.util.event.EventManager;
import org.nishat.util.log.DebugManager;
import org.nishat.util.log.Log;

import java.util.Random;

public class Caller implements Runnable {
    private final int[] divisors = {2, 3, 5, 7, 11, 13};

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        while(true) {
            int rand = new Random().nextInt();
            Log.i("Number", rand+"");
            for (int divisor : divisors) {
                if (rand % divisor == 0) {
                    try {
                        Log.i("Calling", "eventOf" + divisor+" of "+ divisor+" group");
                        EventManager.getInstance().getEvent(String.valueOf(divisor),
                                "eventOf" + divisor).call(new EventParam(this, rand));
                        if (divisor == 11) {
                            synchronized (this) {
                                Log.i("Wait", "divisible by 11");
                                wait(1000);
                                Log.i("Wait", "realised");
                            }
                        }
                    } catch (Throwable e) {
                        DebugManager.getInstance().print(e);
                    }
                }
            }
        }
    }
}
