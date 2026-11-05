package com.example.demo;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DemoThymeleafApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void contextLoads() {
        // Verifie que le contexte Spring Boot démarre correctement
        assertThat(userRepository).isNotNull();
    }

    @Test
    void testListUsers() throws Exception {
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");
        userRepository.save(user);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("user-list"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    void testShowAddForm() throws Exception {
        mockMvc.perform(get("/adduser"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-user"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void testAddUserSuccess() throws Exception {
        mockMvc.perform(post("/adduser")
                .param("name", "John Doe")
                .param("email", "john.doe@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getName()).isEqualTo("John Doe");
        assertThat(users.get(0).getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void testAddUserValidationFailure() throws Exception {
        mockMvc.perform(post("/adduser")
                .param("name", "")
                .param("email", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("add-user"))
                .andExpect(model().hasErrors());

        assertThat(userRepository.count()).isEqualTo(0);
    }
}
