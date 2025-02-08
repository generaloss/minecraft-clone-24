package generaloss.mc24.server.world;

@FunctionalInterface
public interface DirConsumer {

    void accept(int x, int y, int z);

}
