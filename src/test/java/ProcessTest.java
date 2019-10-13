import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import transaction.AbstractTransaction;

class ProcessTest {

    @Test
    public void transactionsDoNotRepeat() {
        TestObjectStringContent testObjectStringContent = new TestObjectStringContent();
        Process<TestObjectStringContent> process = new Process<>(testObjectStringContent);

        process.addTransaction(new TestTransactionAddOne(), "TestTransactionAddOne");

        process.doProcess();
        Assertions.assertEquals("1", testObjectStringContent.getContent());

        process.doProcess();
        Assertions.assertEquals("1", testObjectStringContent.getContent());
    }

    @Test
    public void transactionsEnrichesPreviousResult() {
        TestObjectStringContent testObjectStringContent = new TestObjectStringContent();
        Process<TestObjectStringContent> process = new Process<>(testObjectStringContent);

        process.addTransaction(new TestTransactionAddOne(), "TestTransactionAddOne");
        process.addTransaction(new TestTransactionAddTwo(), "TestTransactionAddTwo");

        process.doProcess();
        Assertions.assertEquals("12", testObjectStringContent.getContent());
    }

    @Test
    public void transactionWithError() {
        TestObjectStringContent testObjectStringContent = new TestObjectStringContent();
        Process<TestObjectStringContent> process = new Process<>(testObjectStringContent);

        process.addTransaction(new TestTransactionAddOne(), "TestTransactionAddOne");
        process.addTransaction(new TestTransactionWithError(), "TestTransactionWithError");
        process.addTransaction(new TestTransactionAddTwo(), "TestTransactionAddTwo");

        process.doProcess();
        Assertions.assertEquals("1", testObjectStringContent.getContent());
        Assertions.assertTrue(process.hasErrors());
    }

    @Test
    public void transactionWithNonStoppingError() {
        TestObjectStringContent testObjectStringContent = new TestObjectStringContent();
        Process<TestObjectStringContent> process = new Process<>(testObjectStringContent);

        process.addTransaction(new TestTransactionAddOne(), "TestTransactionAddOne");
        process.addTransaction(new TestTransactionWithError(), "TestTransactionWithError").continueOnError();
        process.addTransaction(new TestTransactionAddTwo(), "TestTransactionAddTwo");

        process.doProcess();
        Assertions.assertEquals("12", testObjectStringContent.getContent());
        Assertions.assertTrue(process.hasErrors());
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