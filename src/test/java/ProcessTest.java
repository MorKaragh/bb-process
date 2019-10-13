import org.junit.jupiter.api.Test;
import transaction.AbstractTransaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProcessTest {

    @Test
    public void transactionsDoNotRepeat() {
        TestObjectStringContent testObjectStringContent = new TestObjectStringContent();
        Process<TestObjectStringContent> process = new Process<>(testObjectStringContent, "processId");

        process.addTransaction(new TestTransactionAddOne(), "TestTransactionAddOne");

        process.doProcess();
        assertEquals("1", testObjectStringContent.getContent());

        process.doProcess();
        assertEquals("1", testObjectStringContent.getContent());
    }

    @Test
    public void transactionsEnrichesPreviousResult() {
        TestObjectStringContent testObjectStringContent = new TestObjectStringContent();
        Process<TestObjectStringContent> process = new Process<>(testObjectStringContent, "processId");

        process.addTransaction(new TestTransactionAddOne(), "TestTransactionAddOne");
        process.addTransaction(new TestTransactionAddTwo(), "TestTransactionAddTwo");

        process.doProcess();
        assertEquals("12", testObjectStringContent.getContent());
    }

    @Test
    public void transactionWithError() {
        TestObjectStringContent testObjectStringContent = new TestObjectStringContent();
        Process<TestObjectStringContent> process = new Process<>(testObjectStringContent, "processId");

        process.addTransaction(new TestTransactionAddOne(), "TestTransactionAddOne");
        process.addTransaction(new TestTransactionWithError(), "TestTransactionWithError");
        process.addTransaction(new TestTransactionAddTwo(), "TestTransactionAddTwo");

        process.doProcess();
        assertEquals("1", testObjectStringContent.getContent());
        assertTrue(process.hasErrors());
    }

    @Test
    public void transactionWithNonStoppingError() {
        TestObjectStringContent testObjectStringContent = new TestObjectStringContent();
        Process<TestObjectStringContent> process = new Process<>(testObjectStringContent, "processId");

        process.addTransaction(new TestTransactionAddOne(), "TestTransactionAddOne");
        process.addTransaction(new TestTransactionWithError(), "TestTransactionWithError").continueOnError();
        process.addTransaction(new TestTransactionAddTwo(), "TestTransactionAddTwo");

        process.doProcess();
        assertEquals("12", testObjectStringContent.getContent());
        assertTrue(process.hasErrors());
    }

    @Test
    public void errorHandling() {
        TestObjectStringContent testObjectStringContent = new TestObjectStringContent();
        Process<TestObjectStringContent> process = new Process<>(testObjectStringContent, "processId");

        process.addTransaction(new TestTransactionAddOne(), "TestTransactionAddOne");
        process.addTransaction(new TestTransactionWithError(), "TestTransactionWithError")
                .addErrorHandler((e, object) -> {
                    if (e instanceof RuntimeException && object.getContent().equals("1")) {
                        object.setContent(object.getContent() + "-handledError");
                    }
                    throw new Exception();
                });

        process.doProcess();
        assertEquals("1-handledError", testObjectStringContent.getContent());
    }

    class TestTransactionAddOne implements AbstractTransaction<TestObjectStringContent> {
        @Override
        public TestObjectStringContent process(TestObjectStringContent object) {
            return object.setContent(object.getContent() + "1");
        }
    }


    class TestTransactionAddTwo implements AbstractTransaction<TestObjectStringContent> {
        @Override
        public TestObjectStringContent process(TestObjectStringContent object) {
            return object.setContent(object.getContent() + "2");
        }
    }

    class TestTransactionWithError implements AbstractTransaction<TestObjectStringContent> {
        @Override
        public TestObjectStringContent process(TestObjectStringContent object) {
            throw new RuntimeException("exception");
        }
    }

    class TestObjectStringContent {
        private String content = "";

        public String getContent() {
            return content;
        }

        public TestObjectStringContent setContent(String content) {
            this.content = content;
            return this;
        }
    }


}