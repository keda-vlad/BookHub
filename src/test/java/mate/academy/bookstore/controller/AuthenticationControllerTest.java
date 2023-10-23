package mate.academy.bookstore.controller;

import static mate.academy.bookstore.util.TestUserProvider.validRegistrationRequest;
import static mate.academy.bookstore.util.TestUserProvider.validRegistrationResponse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.bookstore.dto.user.UserLoginRequestDto;
import mate.academy.bookstore.dto.user.UserLoginResponseDto;
import mate.academy.bookstore.dto.user.UserRegistrationRequestDto;
import mate.academy.bookstore.dto.user.UserRegistrationResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.com.github.dockerjava.core.MediaType;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationControllerTest {
    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Verify login() method works for AuthenticationController")
    void login_ValidRequestDto_ReturnUserLoginResponseDto() throws Exception {
        UserLoginRequestDto requestDto = new UserLoginRequestDto("admin@example.com", "admin");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON.getMediaType())
                                .content(jsonRequest)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        UserLoginResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), UserLoginResponseDto.class
        );
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.token());
    }

    @DisplayName("Verify register() method works for AuthenticationController")
    @WithMockUser(username = "user")
    @Test
    @Sql(scripts = {
            "classpath:database/user/remove-registered-user.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void register_ValidRequestDto_ReturnUserRegistrationResponseDto() throws Exception {
        UserRegistrationRequestDto requestDto = validRegistrationRequest();
        UserRegistrationResponseDto expected = validRegistrationResponse();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON.getMediaType())
                                .content(jsonRequest)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        UserRegistrationResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), UserRegistrationResponseDto.class
        );
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }
}
