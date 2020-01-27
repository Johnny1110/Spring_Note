@FunctionalInterface
public interface MyInterface<I, O> {
    O apply(I i);
}
