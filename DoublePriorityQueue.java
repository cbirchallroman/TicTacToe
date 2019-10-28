import java.util.Collections;
import java.util.Iterator;
import java.util.PriorityQueue;

public class DoublePriorityQueue<T> implements Iterable<T> {

    PriorityQueue<T> forward;
    PriorityQueue<T> backward;

    public DoublePriorityQueue(){

        forward = new PriorityQueue<>();
        backward = new PriorityQueue<>(Collections.reverseOrder());

    }

    public void add(T object){

        forward.add(object);
        backward.add(object);

    }

    public T peek(){

        return forward.peek();

    }

    public T peekLast(){

        return backward.peek();

    }

    public T pop(){

        //remove object from front of forward list and back of backward list
        T obj = forward.poll();
        backward.remove(obj);
        return obj;

    }

    public T popLast(){

        //remove object from front of backward list and back of forward list
        T obj = backward.poll();
        forward.remove(obj);
        return obj;

    }

    public void update(T obj){

        //remove object as it currently exists from queues
        remove(obj);

        //add object back
        add(obj);

    }

    public void remove(T obj){

        forward.remove(obj);
        backward.remove(obj);

    }

    public int size(){

        return forward.size();

    }

    @Override
    public Iterator<T> iterator() {

        //if we're iterating, just use the forward priority queue
        return forward.iterator();

    }
}
