package es.uji.al259348.sliwserver.it.services;

import es.uji.al259348.sliwserver.Main;
import es.uji.al259348.sliwserver.model.Sample;
import es.uji.al259348.sliwserver.model.User;
import es.uji.al259348.sliwserver.repositories.SampleRepository;
import es.uji.al259348.sliwserver.services.UserService;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Main.class)
public class UserServiceImplTest {

    @Autowired
    UserService userService;

    @Autowired
    SampleRepository sampleRepository;

    @BeforeClass
    public static void setUp() {

    }

    @Test
    public void configure() {

        User user = userService.getUser("1");
        List<Sample> samples = sampleRepository.findByUserIdAndValid(user.getId(), true);

        userService.configure(user, samples);

    }

}