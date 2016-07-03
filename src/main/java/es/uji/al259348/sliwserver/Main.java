package es.uji.al259348.sliwserver;

import es.uji.al259348.sliwserver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Main implements CommandLineRunner {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    UserService userService;

    public static void main(String args[]) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) throws Exception {



    }

}
