public interface ProcessRepository<T> {

    Process<T> load(T object);
    Process<T> load(String processId);

}
