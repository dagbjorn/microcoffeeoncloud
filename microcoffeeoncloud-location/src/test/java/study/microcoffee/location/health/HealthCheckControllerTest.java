package study.microcoffee.location.health;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Unit tests of {@link HealthCheckController}.
 */
@WebMvcTest(HealthCheckController.class)
@TestPropertySource("/application-test.properties")
class HealthCheckControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void isReadyShouldSucceed() throws Exception {
        mockMvc.perform(get("/internal/isready") //
            .accept(MediaType.ALL_VALUE)) //
            .andExpect(status().isOk());
    }

    @Test
    void isAliveShouldSucceed() throws Exception {
        mockMvc.perform(get("/internal/isalive") //
            .accept(MediaType.ALL_VALUE)) //
            .andExpect(status().isOk());
    }
}
