package mate.academy.bookstore.controller;

import static mate.academy.bookstore.util.TestOrderProvider.validDeliveredOrderDto;
import static mate.academy.bookstore.util.TestOrderProvider.validOrderDto;
import static mate.academy.bookstore.util.TestUserProvider.authentication;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.com.github.dockerjava.core.MediaType.APPLICATION_JSON;
import static org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import mate.academy.bookstore.dto.order.ChangeStatusDto;
import mate.academy.bookstore.dto.order.OrderDto;
import mate.academy.bookstore.dto.order.PlaceAnOrderDto;
import mate.academy.bookstore.model.Order;
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
        OrderDto expected = validOrderDto();
        PlaceAnOrderDto requestDto = new PlaceAnOrderDto("SomeAddress");
        Authentication authentication = authentication();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        post("/orders")
                                .content(jsonRequest)
                                .contentType(APPLICATION_JSON.getMediaType())
                                .with(SecurityMockMvcRequestPostProcessors
                                        .authentication(authentication))
                )
                .andExpect(status().isOk())
                .andReturn();

        OrderDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), OrderDto.class
        );
        reflectionEquals(expected, actual, "id", "orderDate");
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
        OrderDto expected = validOrderDto();
        Authentication authentication = authentication();

        MvcResult result = mockMvc.perform(
                        get("/orders")
                                .contentType(APPLICATION_JSON.getMediaType())
                                .with(SecurityMockMvcRequestPostProcessors
                                        .authentication(authentication))
                )
                .andExpect(status().isOk())
                .andReturn();

        OrderDto[] allOrders = objectMapper.readValue(
                result.getResponse().getContentAsString(), OrderDto[].class
        );
        List<OrderDto> actual = Arrays.stream(allOrders).toList();
        assertEquals(1, actual.size());
        assertTrue(reflectionEquals(expected, actual.get(0), "id", "orderItems"));
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
        OrderDto expected = validDeliveredOrderDto();
        ChangeStatusDto requestDto = new ChangeStatusDto(Order.Status.DELIVERED.name());
        Authentication authentication = authentication();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        patch("/orders/1")
                                .contentType(APPLICATION_JSON.getMediaType())
                                .content(jsonRequest)
                                .with(SecurityMockMvcRequestPostProcessors
                                        .authentication(authentication))
                )
                .andExpect(status().isOk())
                .andReturn();

        OrderDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), OrderDto.class
        );
        assertTrue(reflectionEquals(expected, actual, "id", "orderItems"));
    }
}
