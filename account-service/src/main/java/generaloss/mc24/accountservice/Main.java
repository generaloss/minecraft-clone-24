package generaloss.mc24.accountservice;

import generaloss.mc24.accountservice.network.RequestListener;
import jpize.util.io.FastReader;

public class Main {

    public static void main(String[] args) {
        final RequestListener requestListener = new RequestListener();
        final FastReader reader = new FastReader();
        while(!requestListener.isClosed()) {
            Thread.yield();
            if(reader.hasNext()){
                final String line = reader.nextLine();
                System.out.println("Check line: '" + line + "'");
            }
        }
        requestListener.close();
    }

}