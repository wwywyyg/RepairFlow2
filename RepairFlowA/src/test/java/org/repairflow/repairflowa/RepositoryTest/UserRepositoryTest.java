package org.repairflow.repairflowa.RepositoryTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.repairflow.repairflowa.Pojo.UserPojo.Role;
import org.repairflow.repairflowa.Pojo.UserPojo.User;
import org.repairflow.repairflowa.Repository.UserRepository;
import org.repairflow.repairflowa.support.IntegrationTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import javax.swing.text.html.Option;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
/**
 * @author guangyang
 * @date 3/8/26 18:21
 * @description TODO: Description
 */
public class UserRepositoryTest extends IntegrationTestBase {
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldSaveUser() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@doe.com");
        user.setPasswordHash("password112233");
        user.setPhone("1234567890");
        user.setRole(Role.CUSTOMER);
        user.setActive(true);
        User savedUser = userRepository.save(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void shouldNotAllowDuplicateEmail(){
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@doe.com");
        user.setPasswordHash("password112233");
        user.setPhone("1234567890");
        user.setRole(Role.CUSTOMER);
        user.setActive(true);
        User savedUser = userRepository.saveAndFlush(user);

        User anotherUser = new User();
        anotherUser.setFirstName("hello");
        anotherUser.setLastName("world");
        anotherUser.setEmail("john@doe.com");
        anotherUser.setPasswordHash("password112233");
        anotherUser.setPhone("1234567890");
        anotherUser.setRole(Role.CUSTOMER);
        anotherUser.setActive(true);

        assertThatThrownBy(()->userRepository.saveAndFlush(anotherUser))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("email");
    }

    @Test
    void shouldFindUserByEmail() {
        User user = new User();
        user.setFirstName("tom");
        user.setLastName("Dick");
        user.setEmail("tom@dick.com");
        user.setPasswordHash("password112233");
        user.setPhone("1234567890");
        user.setRole(Role.CUSTOMER);
        user.setActive(true);
        User savedUser = userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmail("tom@dick.com");
        assertThat(foundUser.isPresent()).isTrue();
        assertThat(foundUser.get().getFirstName()).isEqualTo(savedUser.getFirstName());
    }

    @Test
    void shouldReturnEmptyWhenUserDoesNotExist() {
        Optional<User> found = userRepository.findByEmail("hello@world.com");
        assertThat(found).isEmpty();
    }


    @Test
    void shouldReturnTrueWhenEmailExists() {
        User user = new User();
        user.setFirstName("hello");
        user.setLastName("world");
        user.setEmail("hello@world.com");
        user.setPasswordHash("password112233");
        user.setPhone("1234567890");
        user.setRole(Role.CUSTOMER);
        user.setActive(true);
        User savedUser = userRepository.save(user);

        boolean exists = userRepository.existsByEmail("hello@world.com");
        assertThat(exists).isTrue();
    }

}
