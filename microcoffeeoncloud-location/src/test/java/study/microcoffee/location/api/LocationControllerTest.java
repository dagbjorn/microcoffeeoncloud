package study.microcoffee.location.api;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import study.microcoffee.location.logging.HttpLoggingFilterTestConfig;
import study.microcoffee.location.repository.LocationRepository;

/**
 * Unit tests of {@link LocationController}.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(LocationController.class)
@TestPropertySource("/application-test.properties")
@Import(HttpLoggingFilterTestConfig.class)
public class LocationControllerTest {

    private static final String SERVICE_PATH = "/api/coffeeshop/nearest/{latitude}/{longitude}/{maxdistance}";

    @MockBean
    private LocationRepository locationRepositoryMock;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getNearestCoffeeShopWhenFoundShouldReturnLocation() throws Exception {
        final String expectedContent = "{ location: 'here' }";

        given(locationRepositoryMock.findNearestCoffeeShop(anyDouble(), anyDouble(), anyLong())).willReturn(expectedContent);

        mockMvc.perform(get(SERVICE_PATH, 1, 2, 3) //
            .accept(MediaType.APPLICATION_JSON_UTF8)) //
            .andExpect(status().isOk()) //
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)) //
            .andExpect(content().json(expectedContent));
    }

    @Test
    public void getNearestCoffeeShopWhenNotFoundShouldReturnNoContent() throws Exception {
        mockMvc.perform(get(SERVICE_PATH, 4, 5, 6) //
            .accept(MediaType.APPLICATION_JSON_UTF8)) //
            .andExpect(status().isNoContent());
    }

    @Test
    public void getNearestCoffeeShopWhenParameterTypeMismatchShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get(SERVICE_PATH, 4, 5, "6x") //
            .accept(MediaType.APPLICATION_JSON_UTF8)) //
            .andExpect(status().isBadRequest()) //
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void getNearestCoffeeShopWhenRepositoryErrorShouldReturnInternalServerError() throws Exception {
        given(locationRepositoryMock.findNearestCoffeeShop(anyDouble(), anyDouble(), anyLong()))
            .willThrow(new RuntimeException(("Mocked exception")));

        mockMvc.perform(get(SERVICE_PATH, 4, 5, 6) //
            .accept(MediaType.APPLICATION_JSON_UTF8)) //
            .andExpect(status().isInternalServerError()) //
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void getNearestCoffeeShopWhenRepositoryErrorAndAcceptAllContentTypesShouldReturnInternalServerError() throws Exception {
        given(locationRepositoryMock.findNearestCoffeeShop(anyDouble(), anyDouble(), anyLong()))
            .willThrow(new RuntimeException(("Mocked exception")));

        mockMvc.perform(get(SERVICE_PATH, 4, 5, 6) //
            .accept(MediaType.ALL)) //
            .andExpect(status().isInternalServerError()) //
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }
}
