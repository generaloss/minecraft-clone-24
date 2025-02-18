package generaloss.mc24.server.common;

@FunctionalInterface
public interface DirConsumer {

    void accept(int x, int y, int z);

}
