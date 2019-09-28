public class TransactionDefinition<T> {

    private AbstractTransaction<T> transaction;
    private TransactionStatus status = TransactionStatus.NOT_STARTED;
    private boolean continueOnError = false;

    public TransactionDefinition(AbstractTransaction<T> transaction) {
        this.transaction = transaction;
    }

    public TransactionDefinition<T> setTransaction(AbstractTransaction<T> transaction) {
        this.transaction = transaction;
        return this;
    }

    public AbstractTransaction<T> getTransaction() {
        return transaction;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public TransactionDefinition<T> setStatus(TransactionStatus status) {
        this.status = status;
        return this;
    }

    public void continueOnError() {
        continueOnError = true;
    }

    public boolean isContinueOnError() {
        return continueOnError;
    }
}
