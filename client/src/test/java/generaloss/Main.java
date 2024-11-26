package generaloss;

import jpize.util.res.Resource;

import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Main {

    public static void main(String[] args) throws IOException {
        final ZipFile file = new ZipFile("./assets/client-pack.zip");

        final Enumeration<? extends ZipEntry> entries = file.entries();

        while (entries.hasMoreElements()) {
            final ZipEntry entry = entries.nextElement();
            if(!entry.isDirectory()) {
                System.out.println(entry.());
                final Resource resource = Resource.inte(entry.getName());
            }
        }

        file.close();
    }

}
