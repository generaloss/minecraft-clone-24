package generaloss.mc24.server.common;

@FunctionalInterface
public interface XYZConsumer {

    void accept(int x, int y, int z);

}