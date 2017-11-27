package org.serdar.deeplearning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        // pidof java
        // ps #pid#
        // nohup java -jar 783-0.0.1-SNAPSHOT.jar >> /dev/null 2>&1 &
    }

}