package transaction;

public interface TransactionRepository<T> {

    TransactionDefinition<T> load(String id);

    void changeStatus(TransactionDefinition<T> transaction);
}
