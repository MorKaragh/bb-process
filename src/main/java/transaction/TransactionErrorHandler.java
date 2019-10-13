package transaction;

public interface TransactionErrorHandler<T> {

    void handle(Exception e, T object) throws Exception;

}
