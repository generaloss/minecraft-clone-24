package generaloss;

import generaloss.mc24.server.column.ColumnPos;

public class Main {

    public static void main(String[] args) {
        final ColumnPos pos = new ColumnPos(100, 100);
        System.out.println(ColumnPos.unpack(pos.getPacked()));
    }

}
