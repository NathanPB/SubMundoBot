package piva.sbb.bot;

import me.piva.utils.LibLoader;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class Boot {
    public static void main(String[] args) {
        System.out.println("Loading libraries...");
        try {
            URL url = new File("libs", "PyvaUtils-1.0.jar").toURI().toURL();
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(ClassLoader.getSystemClassLoader(), url);

            LibLoader.loadMultiple(new File("libs"),
                    "JDA-4.2.0_168.jar",
                    "slf4j-simple-1.7.30.jar",
                    "HikariCP-3.4.5.jar",
                    "mysql-connector-java-8.0.21.jar",
                    "json-20200518.jar");
        } catch (InvocationTargetException | MalformedURLException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        System.out.println("Libraries loaded");
        Core.start();
    }
}