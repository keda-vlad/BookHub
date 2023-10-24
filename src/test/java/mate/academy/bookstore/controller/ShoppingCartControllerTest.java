package mate.academy.bookstore.controller;

import static mate.academy.bookstore.util.TestShoppingCartProvider.validShoppingCartDto;
import static mate.academy.bookstore.util.TestUserProvider.authentication;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.com.github.dockerjava.core.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.bookstore.dto.shoppingcarts.ShoppingCartDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShoppingCartControllerTest {
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
    @DisplayName("Verify getShoppingCart() method works for ShoppingCartController")
    @Sql(scripts = {
            "classpath:database/shoppingcart/create-shopping-cart.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/shoppingcart/remove-shopping-cart.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getShoppingCart_ValidAuth_ReturnShoppingCartDto() throws Exception {
        ShoppingCartDto expected = validShoppingCartDto();
        Authentication authentication = authentication();

        MvcResult result = mockMvc.perform(
                        get("/cart")
                                .contentType(APPLICATION_JSON.getMediaType())
                                .with(SecurityMockMvcRequestPostProcessors
                                        .authentication(authentication))
                )
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartDto.class
        );
        assertEquals(expected, actual);
    }
}
