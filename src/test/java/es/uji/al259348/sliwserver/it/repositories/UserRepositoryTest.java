package es.uji.al259348.sliwserver.it.repositories;

import es.uji.al259348.sliwserver.Main;
import es.uji.al259348.sliwserver.model.User;
import es.uji.al259348.sliwserver.repositories.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Main.class)
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    public void save() {
        User user = new User();
        user.setName("TestUser");

        user = userRepository.save(user);

        assertNotNull(user);
        assertNotNull(user.getId());
        System.out.println(user);

        String id = user.getId();

        user = userRepository.findOne(id);

        assertNotNull(user);
        assertNotNull(user.getId());
        System.out.println(user);

    }

}