import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import transaction.*;

import java.util.ArrayList;
import java.util.List;

public class Process<T> {

    Logger log = LoggerFactory.getLogger(Process.class);

    private String processId;
    private T processedObject;
    private boolean hasErrors = false;
    private TransactionRepository<T> transactionRepository;
    private List<TransactionDefinition<T>> transactions = new ArrayList<>();

    public Process(T processedObject, String processId) {
        this.processId = processId;
        this.processedObject = processedObject;
    }

    public TransactionDefinition<T> addTransaction(AbstractTransaction<T> transaction, String transactionId) {
        TransactionDefinition<T> definition = new TransactionDefinition<>(transaction, transactionId);
        transactions.add(definition);
        return definition;
    }

    public void doProcess() {
        for (TransactionDefinition<T> transaction : transactions) {
            if (TransactionStatus.NOT_STARTED.equals(transaction.getStatus())) {
                try {
                    processedObject = transaction.getTransaction().process(processedObject);
                    transaction.setStatus(TransactionStatus.FINISHED);
                    transactionRepository.changeStatus(transaction);
                } catch (Exception e) {
                    if (CollectionUtils.isNotEmpty(transaction.getHandlers())) {
                        for (TransactionErrorHandler<T> h : transaction.getHandlers()) {
                            try {
                                h.handle(e, processedObject);
                            } catch (Exception ex) {
                                if (needStop(transaction)) {
                                    return;
                                }
                            }
                        }
                    } else {
                        if (needStop(transaction)) {
                            return;
                        }
                    }
                }
            }
        }
    }

    private boolean needStop(TransactionDefinition<T> transaction) {
        hasErrors = true;
        transaction.setStatus(TransactionStatus.ERROR);
        return !transaction.isContinueOnError();
    }

    public boolean hasErrors() {
        return hasErrors;
    }

    public String getProcessId() {
        return processId;
    }

    public Process<T> setProcessId(String processId) {
        this.processId = processId;
        return this;
    }
}
