package ThreadsAndLocks.DiningPhilosophers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophers {

    public static void main(String[] args) {
        try {
            CountDownLatch latch = new CountDownLatch(5);
            ExecutorService executorService = Executors.newFixedThreadPool(5);

            ChopStick chopStick1 = new ChopStick();
            ChopStick chopStick2 = new ChopStick();
            ChopStick chopStick3 = new ChopStick();
            ChopStick chopStick4 = new ChopStick();
            ChopStick chopStick5 = new ChopStick();

            ReentrantLock chopLock1 = new ReentrantLock();
            ReentrantLock chopLock2 = new ReentrantLock();
            ReentrantLock chopLock3 = new ReentrantLock();
            ReentrantLock chopLock4 = new ReentrantLock();
            ReentrantLock chopLock5 = new ReentrantLock();

            Philosopher philosopher1 = new Philosopher("Phi1", chopStick5, chopStick1, chopLock5, chopLock1, latch);
            Philosopher philosopher2 = new Philosopher("Phi2", chopStick1, chopStick2, chopLock1, chopLock2, latch);
            Philosopher philosopher3 = new Philosopher("Phi3", chopStick2, chopStick3, chopLock2, chopLock3, latch);
            Philosopher philosopher4 = new Philosopher("Phi4", chopStick3, chopStick4, chopLock3, chopLock4, latch);
            Philosopher philosopher5 = new Philosopher("Phi5", chopStick4, chopStick5, chopLock4, chopLock5, latch);

            executorService.submit(philosopher1);
            executorService.submit(philosopher2);
            executorService.submit(philosopher3);
            executorService.submit(philosopher4);
            executorService.submit(philosopher5);

            latch.await();
            executorService.shutdown();

        } catch (InterruptedException e) {
            System.err.println("error in executing philosopher's threads");
            e.printStackTrace();
        }

    }

}

class Philosopher implements Runnable {
    private final static int TOTAL_BYTES_TO_EAT = 5;

    public String name;
    public ChopStick chopStick1;
    public ChopStick chopStick2;

    public ReentrantLock chopLock1;
    public ReentrantLock chopLock2;

    public int bytesEaten = 0;

    private final CountDownLatch latch;

    public Philosopher(String name, ChopStick chopStick1, ChopStick chopStick2, ReentrantLock chopstickLock1,
            ReentrantLock chopstickLock2, CountDownLatch latch) {

        this.name = name;
        this.chopStick1 = chopStick1;
        this.chopStick2 = chopStick2;
        this.chopLock1 = chopstickLock1;
        this.chopLock2 = chopstickLock2;
        this.latch = latch;
    }

    private void eatUsingChopsticks() {
        while (bytesEaten < Philosopher.TOTAL_BYTES_TO_EAT) {
            try {
                chopLock1.tryLock(2, TimeUnit.SECONDS);
                chopLock2.tryLock(2, TimeUnit.SECONDS);
                chopStick1.use(this);
                chopStick2.use(this);

                bytesEaten++;

            } catch (InterruptedException e) {
                System.err.println(this.name + " could not find chopstick this time");
                e.printStackTrace();
            } finally {

                if (chopLock1.isHeldByCurrentThread())
                    chopLock1.unlock();

                if (chopLock2.isHeldByCurrentThread())
                    chopLock2.unlock();
            }
        }
        latch.countDown();
        System.out.println(this.name + " is done eating");
    }

    @Override
    public void run() {
        this.eatUsingChopsticks();
    }

}

class ChopStick {

    public synchronized void use(Philosopher philosopher) {
        System.out.println("philosopher " + philosopher.name + " used a chopstick");

    }
}
