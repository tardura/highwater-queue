import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class HighwaterQueue<T> extends ArrayBlockingQueue<T> {

    final ReentrantLock floodingLock;
    private Consumer<T> floodOperation;

   public HighwaterQueue(int highwaterMark, Consumer<T> floodOperation){
        super(highwaterMark, true);
        floodingLock = new ReentrantLock(true);
        this.floodOperation = floodOperation;
    }

    private boolean reachedHighwaterMark() {
        return remainingCapacity() == 0;
    }

    private void flashFlood(){
        this.stream().forEach(floodOperation);
        this.clear();
    }

    private void checkForAndProcessHighwaterMark(){
        if(reachedHighwaterMark()){
            flashFlood();
        }
    }

    @Override
    public boolean add(T t){
        final ReentrantLock lock = this.floodingLock;
        lock.lock();
        try{
            checkForAndProcessHighwaterMark();
            return super.add(t);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean offer(T t) {
        final ReentrantLock lock = this.floodingLock;
        lock.lock();
        try{
            checkForAndProcessHighwaterMark();
            return super.offer(t);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void put(T t) throws InterruptedException {
        final ReentrantLock lock = this.floodingLock;
        lock.lock();
        try{
            checkForAndProcessHighwaterMark();
            super.put(t);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean offer(T t, long timeout, TimeUnit unit) throws InterruptedException {
        final ReentrantLock lock = this.floodingLock;
        lock.lock();
        try {
            checkForAndProcessHighwaterMark();
            return super.offer(t, timeout, unit);
        } finally {
            lock.unlock();
        }
    }
}
