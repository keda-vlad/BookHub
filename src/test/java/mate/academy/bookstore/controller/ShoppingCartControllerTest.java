package mate.academy.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import mate.academy.bookstore.dto.cartitem.CartItemDto;
import mate.academy.bookstore.dto.shoppingcarts.ShoppingCartDto;
import mate.academy.bookstore.model.Role;
import mate.academy.bookstore.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.com.github.dockerjava.core.MediaType;

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
        ShoppingCartDto expected = new ShoppingCartDto(
                17L,
                17L,
                Set.of(new CartItemDto(1L, 1L, "some_title", 1))
        );
        Authentication authentication = authentication();
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/cart")
                                .contentType(MediaType.APPLICATION_JSON.getMediaType())
                                .with(SecurityMockMvcRequestPostProcessors
                                        .authentication(authentication))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartDto.class
        );
        Assertions.assertEquals(expected, actual);
    }

    private Authentication authentication() {
        User user = new User()
                .setId(17L)
                .setEmail("some_email@exam.com")
                .setRoles(Set.of(
                        new Role()
                                .setName(Role.RoleName.ROLE_ADMIN)
                                .setId(2L)));
        return new UsernamePasswordAuthenticationToken(
                user,
                user.getPassword(),
                user.getAuthorities()
        );
    }
}
