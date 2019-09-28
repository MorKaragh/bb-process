import java.util.ArrayList;
import java.util.List;

public class ProcessDefinition<T> {


    private final T processedObject;

    private List<TransactionDefinition<T>> transactions = new ArrayList<>();
    private boolean hasErrors = false;

    public ProcessDefinition(T processedObject) {
        this.processedObject = processedObject;
    }

    public TransactionDefinition<T>  addTransaction(AbstractTransaction<T> transaction) {
        TransactionDefinition<T> definition = new TransactionDefinition<>(transaction);
        transactions.add(definition);
        return definition;
    }

    public void doProcess() {
        for (TransactionDefinition<T> transaction : transactions) {
            if (TransactionStatus.NOT_STARTED.equals(transaction.getStatus())) {
                try {
                    transaction.getTransaction().process(processedObject);
                    transaction.setStatus(TransactionStatus.FINISHED);
                } catch (Exception e) {
                    hasErrors = true;
                    transaction.setStatus(TransactionStatus.ERROR);
                    if (!transaction.isContinueOnError()) {
                        return;
                    }
                }
            }
        }
    }

    public boolean hasErrors() {
        return hasErrors;
    }
}
