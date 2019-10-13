package transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionDefinition<T> {

    private final String transactionId;
    private AbstractTransaction<T> transaction;
    private TransactionStatus status = TransactionStatus.NOT_STARTED;
    private boolean continueOnError = false;
    private List<TransactionErrorHandler<T>> handlers = new ArrayList<>();

    public TransactionDefinition(AbstractTransaction<T> transaction, String transactionId) {
        this.transaction = transaction;
        this.transactionId = transactionId;
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

    public String getTransactionId() {
        return transactionId;
    }

    public List<TransactionErrorHandler<T>> getHandlers() {
        return handlers;
    }

    public TransactionDefinition<T> setHandlers(List<TransactionErrorHandler<T>> handlers) {
        this.handlers = handlers;
        return this;
    }

    public TransactionDefinition<T> addErrorHandler(TransactionErrorHandler<T> ha) {
        handlers.add(ha);
        return this;
    }
}
