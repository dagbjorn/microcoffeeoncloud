package study.microcoffee.creditrating.health;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Unit tests of {@link HealthCheckController}.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(HealthCheckController.class)
@TestPropertySource("/application-test.properties")
public class HealthCheckControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void isReadyShouldSucceed() throws Exception {
        mockMvc.perform(get("/internal/isready") //
            .accept(MediaType.ALL_VALUE)) //
            .andExpect(status().isOk());
    }

    @Test
    public void isAliveShouldSucceed() throws Exception {
        mockMvc.perform(get("/internal/isalive") //
            .accept(MediaType.ALL_VALUE)) //
            .andExpect(status().isOk());
    }
}
