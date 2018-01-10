package study.microcoffee.order.rest.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;

import study.microcoffee.order.domain.DrinkType;
import study.microcoffee.order.domain.Order;

/**
 * Integration tests of {@link OrderRestService}.
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@TestPropertySource("/application-test.properties")
public class OrderRestServiceIT {

    private static final String POST_SERVICE_PATH = "/coffeeshop/{coffeeShopId}/order";
    private static final String GET_SERVICE_PATH = "/coffeeshop/{coffeeShopId}/order/{orderId}";

    private static final int COFFEE_SHOP_ID = 10;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void saveOrderAndReadBackShouldReturnSavedOrder() throws Exception {
        Order newOrder = new Order.Builder() //
            .type(new DrinkType("Latte", "Coffee")) //
            .size("Small") //
            .drinker("Dagbjørn") //
            .selectedOption("skimmed milk") //
            .build();

        MvcResult result = mockMvc.perform(post(POST_SERVICE_PATH, COFFEE_SHOP_ID) //
            .content(toJson(newOrder)) //
            .contentType(MediaType.APPLICATION_JSON_UTF8)) //
            .andExpect(status().isCreated()) //
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)) //
            .andExpect(jsonPath("$.type.name").value("Latte")) //
            .andExpect(jsonPath("$.drinker").value("Dagbjørn")) //
            .andReturn();

        MockHttpServletResponse response = result.getResponse();
        Order savedOrder = toObject(response.getContentAsString(), Order.class);

        assertThat(response.getHeader(HttpHeaders.LOCATION)).endsWith(savedOrder.getId());

        mockMvc.perform(get(GET_SERVICE_PATH, COFFEE_SHOP_ID, savedOrder.getId()) //
            .accept(MediaType.APPLICATION_JSON_UTF8)) //
            .andExpect(status().isOk()) //
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)) //
            .andExpect(content().json(toJson(savedOrder)));
    }

    @Test
    public void getOrderWhenNoOrderShouldReturnNoContent() throws Exception {
        String orderId = "1111111111111111";

        mockMvc.perform(get(GET_SERVICE_PATH, COFFEE_SHOP_ID, orderId) //
            .accept(MediaType.APPLICATION_JSON_UTF8)) //
            .andExpect(status().isNoContent());
    }

    private String toJson(Object value) throws JsonProcessingException {
        return objectMapper.writeValueAsString(value);
    }

    private <T> T toObject(String json, Class<T> classType) throws IOException {
        return objectMapper.readValue(json, classType);
    }

    @Configuration
    @Import({ OrderRestService.class })
    @ComponentScan(basePackages = { "study.microcoffee.order.security", "study.microcoffee.order.consumer.creditrating" })
    @EnableMongoRepositories(basePackages = "study.microcoffee.order.repository")
    @EnableCircuitBreaker
    static class Config {

        @Value("${mongo.database.host}")
        private String mongoDatabaseHost;

        @Value("${mongo.database.port}")
        private int mongoDatabasePort;

        @Value("${mongo.database.name}")
        private String mongoDatabaseName;

        @Bean
        public MongoDbFactory mongoDbFactory() {
            return new SimpleMongoDbFactory(new MongoClient(mongoDatabaseHost, mongoDatabasePort), mongoDatabaseName);
        }

        @Bean
        public MongoTemplate mongoTemplate() throws Exception {
            return new MongoTemplate(mongoDbFactory());
        }
    }
}
