
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    private static Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private final ExecutorService cleanerExecutor;
    private boolean isStopped;
    private boolean doThrow;
    private int interations;

    public Main() {
        this.cleanerExecutor = Executors.newSingleThreadExecutor();
        this.isStopped = false;
        this.doThrow = false;
        this.interations = 0;
    }

    public void setStopped(boolean stopped) {
        isStopped = stopped;
    }

    public void stop() throws InterruptedException {
        setStopped(true);
        this.cleanerExecutor.shutdownNow();
        this.cleanerExecutor.awaitTermination(3, TimeUnit.SECONDS);
        LOGGER.info("shutdown complete");
    }

    public void start() {
        cleanerExecutor.submit(() -> {
                    LOGGER.info("started thread");
                    while (!isStopped)
                    {
                        try
                        {
                            Thread.sleep(1_000);
                            LOGGER.info("iteration {}", this.interations++);

                            if (doThrow) {
                                LOGGER.error("will throw");
                                throw new RuntimeException("oops, make sure you uncomment the catch(Throwable)");
                            }
                        } catch (InterruptedException e) {
                            LOGGER.warn("interrupted, time to exit;");
                            break;
                        } /*catch (Throwable t) {
                            LOGGER.error("catch throwable, reset do throw");
                            doThrow = false;
                        }*/
                    }
                }
        );
    }

    public static void main(String args[]) throws InterruptedException {
        Main m = new Main();
        m.start();

        for (int i = 0; i < 5; i++) {
            Thread.sleep(1_000);
        }

        m.doThrow = true;

        for (int i = 0; i < 5; i++) {
            Thread.sleep(1_000);
        }

        LOGGER.info("should have seen \"iteration N\" messages after \"will throw\". If not, uncomment the catch(Throwable)");

        m.stop();
    }
}
