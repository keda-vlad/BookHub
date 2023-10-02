package mate.academy.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import mate.academy.bookstore.dto.order.ChangeStatusDto;
import mate.academy.bookstore.dto.order.OrderDto;
import mate.academy.bookstore.dto.order.PlaceAnOrderDto;
import mate.academy.bookstore.dto.orderitem.OrderItemDto;
import mate.academy.bookstore.model.Order;
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
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerTest {
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
    @DisplayName("Verify placeAnOrder() method works for OrderController")
    @Sql(scripts = {
            "classpath:database/order/create-shopping-cart.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/order/remove-shopping-cart.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void placeAnOrder_ValidRequestDto_ReturnOrderDto() throws Exception {
        OrderDto expected = new OrderDto(
                1L,
                17L,
                Set.of(new OrderItemDto(1L, 1L, 1)),
                null,
                BigDecimal.valueOf(99.99),
                Order.Status.PENDING
        );
        PlaceAnOrderDto requestDto = new PlaceAnOrderDto("SomeAddress");
        Authentication authentication = authentication();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post("/orders")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON.getMediaType())
                                .with(SecurityMockMvcRequestPostProcessors
                                        .authentication(authentication))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        OrderDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), OrderDto.class
        );
        EqualsBuilder.reflectionEquals(expected, actual, "id", "orderDate");
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Verify getOrders() method works for OrderController")
    @Sql(scripts = {
            "classpath:database/order/create-order.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/order/remove-order.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getOrders_ValidRequestDto_ReturnListOrderDto() throws Exception {
        OrderDto expected = new OrderDto(
                1L,
                17L,
                Set.of(new OrderItemDto(1L, 1L, 1)),
                LocalDateTime.parse("2012-09-08T00:00:00"),
                BigDecimal.valueOf(99.99),
                Order.Status.PENDING
        );
        Authentication authentication = authentication();
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/orders")
                                .contentType(MediaType.APPLICATION_JSON.getMediaType())
                                .with(SecurityMockMvcRequestPostProcessors
                                        .authentication(authentication))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        OrderDto[] allOrders = objectMapper.readValue(
                result.getResponse().getContentAsString(), OrderDto[].class
        );
        List<OrderDto> actual = Arrays.stream(allOrders).toList();
        Assertions.assertEquals(1, actual.size());
        Assertions.assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual.get(0), "id", "orderItems")
        );
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Verify updateStatus() method works for OrderController")
    @Sql(scripts = {
            "classpath:database/order/create-order.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/order/remove-order.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateStatus_ValidRequestDto_ReturnOrderDto() throws Exception {
        OrderDto expected = new OrderDto(
                1L,
                17L,
                Set.of(new OrderItemDto(1L, 13L, 1)),
                LocalDateTime.parse("2012-09-08T00:00:00"),
                BigDecimal.valueOf(99.99),
                Order.Status.DELIVERED
        );
        ChangeStatusDto requestDto = new ChangeStatusDto(Order.Status.DELIVERED.name());
        Authentication authentication = authentication();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.patch("/orders/1")
                                .contentType(MediaType.APPLICATION_JSON.getMediaType())
                                .content(jsonRequest)
                                .with(SecurityMockMvcRequestPostProcessors
                                        .authentication(authentication))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        OrderDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), OrderDto.class
        );
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id", "orderItems"));
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
