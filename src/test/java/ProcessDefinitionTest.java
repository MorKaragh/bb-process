import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ProcessDefinitionTest {

    @Test
    public void transactionsDoNotRepeat() {
        TestObjectStringContent testObjectStringContent = new TestObjectStringContent();
        ProcessDefinition<TestObjectStringContent> process = new ProcessDefinition<>(testObjectStringContent);

        process.addTransaction(new TestTransactionAddOne());

        process.doProcess();
        Assertions.assertEquals("1", testObjectStringContent.getContent());

        process.doProcess();
        Assertions.assertEquals("1", testObjectStringContent.getContent());
    }

    @Test
    public void transactionsEnrichesPreviousResult() {
        TestObjectStringContent testObjectStringContent = new TestObjectStringContent();
        ProcessDefinition<TestObjectStringContent> process = new ProcessDefinition<>(testObjectStringContent);

        process.addTransaction(new TestTransactionAddOne());
        process.addTransaction(new TestTransactionAddTwo());

        process.doProcess();
        Assertions.assertEquals("12", testObjectStringContent.getContent());
    }

    @Test
    public void transactionWithError() {
        TestObjectStringContent testObjectStringContent = new TestObjectStringContent();
        ProcessDefinition<TestObjectStringContent> process = new ProcessDefinition<>(testObjectStringContent);

        process.addTransaction(new TestTransactionAddOne());
        process.addTransaction(new TestTransactionWithError());
        process.addTransaction(new TestTransactionAddTwo());

        process.doProcess();
        Assertions.assertEquals("1", testObjectStringContent.getContent());
        Assertions.assertTrue(process.hasErrors());
    }

    @Test
    public void transactionWithNonStoppingError() {
        TestObjectStringContent testObjectStringContent = new TestObjectStringContent();
        ProcessDefinition<TestObjectStringContent> process = new ProcessDefinition<>(testObjectStringContent);

        process.addTransaction(new TestTransactionAddOne());
        process.addTransaction(new TestTransactionWithError()).continueOnError();
        process.addTransaction(new TestTransactionAddTwo());

        process.doProcess();
        Assertions.assertEquals("12", testObjectStringContent.getContent());
        Assertions.assertTrue(process.hasErrors());
    }

    class TestTransactionAddOne extends AbstractTransaction<TestObjectStringContent> {
        @Override
        public TestObjectStringContent process(TestObjectStringContent object) {
            return object.setContent(object.getContent() + "1");
        }
    }


    class TestTransactionAddTwo extends AbstractTransaction<TestObjectStringContent> {
        @Override
        public TestObjectStringContent process(TestObjectStringContent object) {
            return object.setContent(object.getContent() + "2");
        }
    }

    class TestTransactionWithError extends AbstractTransaction<TestObjectStringContent> {
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