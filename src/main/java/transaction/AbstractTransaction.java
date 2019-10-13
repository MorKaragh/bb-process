package transaction;

public interface AbstractTransaction<T> {

    T process(T object);

}
