package weighter;

/**
 * Created by ahmerb on 24/04/16.
 */
public interface Weighter<T> {
    /**
     * Takes an object of type T and assigns and returns a weight for it.
     *
     * @param t the object to get weight for.
     * @return the weight of the object.
     */
    double toWeight(T t);
}
