package cn.mrray.repository;

import cn.mrray.ZuulServerApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserRepositoryTest extends ZuulServerApplicationTests {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testInsertUser() throws Exception {

        //String username = "admin";
        //User admin = userRepository.findByUsername(username);
        //if (admin == null) {
        //    admin = new User();
        //    admin.setUsername(username);
        //    admin.setPassword(passwordEncoder.encode("admin"));
        //
        //    userRepository.saveAndFlush(admin);
        //}

        System.out.println(passwordEncoder.encode("admin"));
    }
}
