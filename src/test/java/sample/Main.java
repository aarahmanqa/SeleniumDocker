package sample;

import com.google.common.collect.Lists;
import org.apache.http.impl.io.SocketOutputBuffer;
import org.testng.TestNG;

import java.io.File;
import java.sql.SQLOutput;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Args[0] = " + args[0]);
        TestNG testng = new TestNG();
        List<String> suites = Lists.newArrayList();
        suites.add(System.getProperty("user.dir")+ args[0]);
        testng.setTestSuites(suites);
        testng.run();

    }
}
