import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class HighwaterQueueTest {

    private HighwaterQueue<Integer> highwaterQueue;
    private LinkedList<Integer> flushedValues;

    @Before
    public void setup(){
        flushedValues = new LinkedList<Integer>();
        highwaterQueue = new HighwaterQueue<Integer>(1, x -> flushedValues.add(x));
    }

    @Test
    public void normalInsertionWorksAsExpected(){
        highwaterQueue.add(1);
        assertThat(highwaterQueue.peek(), is(1));
    }

    @Test
    public void insertionWithAFlushFlushesAsExpected(){
        highwaterQueue.add(1);
        highwaterQueue.add(2);
        assertThat(highwaterQueue.peek(), is(2));
        assertThat(highwaterQueue.contains(1), is(false));
        assertThat(flushedValues.get(0), is(1));
    }

    @Test
    public void insertionWithMultipleFlushesFunctionsAsExpected(){
        highwaterQueue = new HighwaterQueue<Integer>(2, x-> flushedValues.add(x));
        highwaterQueue.add(1);
        highwaterQueue.add(2);
        highwaterQueue.add(3);
        assertThat(highwaterQueue.peek(), is(3));
        assertThat(highwaterQueue.contains(1), is(false));
        assertThat(highwaterQueue.contains(2), is(false));
        assertThat(flushedValues.get(0), is(1));
        assertThat(flushedValues.get(1), is(2));
    }

    @Test
    public void normalOfferWorksAsExpected(){
        highwaterQueue.offer(1);
        assertThat(highwaterQueue.peek(), is(1));
    }

    @Test
    public void offerWithMultipleFlushesFunctionsAsExpected(){
        highwaterQueue = new HighwaterQueue<Integer>(2, x -> flushedValues.add(x));
        highwaterQueue.offer(1);
        highwaterQueue.offer(2);
        highwaterQueue.offer(3);
        assertThat(highwaterQueue.peek(), is(3));
        assertThat(highwaterQueue.contains(1), is(false));
        assertThat(highwaterQueue.contains(2), is(false));
        assertThat(flushedValues.get(0), is(1));
        assertThat(flushedValues.get(1), is(2));
    }

    @Test
    public void normalPutWorksAsExpected() throws InterruptedException{
        highwaterQueue.put(1);
        assertThat(highwaterQueue.peek(), is(1));
    }

    @Test
    public void putWithMultipleFlushesFunctionsAsExpected() throws InterruptedException {
        highwaterQueue = new HighwaterQueue<Integer>(2, x -> flushedValues.add(x));
        highwaterQueue.put(1);
        highwaterQueue.put(2);
        highwaterQueue.put(3);
        assertThat(highwaterQueue.peek(), is(3));
        assertThat(highwaterQueue.contains(1), is(false));
        assertThat(highwaterQueue.contains(2), is(false));
        assertThat(flushedValues.get(0), is(1));
        assertThat(flushedValues.get(1), is(2));
    }

    @Test
    public void normalOfferWithTimeoutWorksAsExpected() throws InterruptedException{
        highwaterQueue.offer(1, 10l, TimeUnit.SECONDS);
        assertThat(highwaterQueue.peek(), is(1));
    }

    @Test
    public void offerWithTimeoutWithMultipleFlushesFunctionsAsExpected() throws InterruptedException{
        highwaterQueue = new HighwaterQueue<Integer>(2, x -> flushedValues.add(x));
        highwaterQueue.offer(1, 10l, TimeUnit.MINUTES);
        highwaterQueue.offer(2, 10l, TimeUnit.MINUTES);
        highwaterQueue.offer(3, 10l, TimeUnit.MINUTES);
        assertThat(highwaterQueue.peek(), is(3));
        assertThat(highwaterQueue.contains(1), is(false));
        assertThat(highwaterQueue.contains(2), is(false));
        assertThat(flushedValues.get(0), is(1));
        assertThat(flushedValues.get(1), is(2));
    }


}


