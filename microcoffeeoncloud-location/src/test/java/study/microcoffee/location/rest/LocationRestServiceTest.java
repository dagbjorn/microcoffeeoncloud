package study.microcoffee.location.rest;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import study.microcoffee.location.repository.LocationRepository;

/**
 * Unit tests of {@link LocationRestService}.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(LocationRestService.class)
@TestPropertySource(properties = { "logging.level.study.microcoffee=DEBUG" })
public class LocationRestServiceTest {

    private static final String SERVICE_PATH = "/coffeeshop/nearest/{latitude}/{longitude}/{maxdistance}";

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
    public void getNearestCoffeeShopWhenOriginHeaderSetShouldReturnCORSHeaders() throws Exception {
        final String expectedContent = "{ location: 'here' }";
        final String expectedOriginHeader = "http://localhost:8080";

        given(locationRepositoryMock.findNearestCoffeeShop(anyDouble(), anyDouble(), anyLong())).willReturn(expectedContent);

        mockMvc.perform(get(SERVICE_PATH, 1, 2, 3) //
            .accept(MediaType.APPLICATION_JSON_UTF8) //
            .header(HttpHeaders.ORIGIN, expectedOriginHeader)) //
            .andExpect(status().isOk()) //
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, Matchers.equalTo(expectedOriginHeader))) //
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)) //
            .andExpect(content().json(expectedContent));
    }
}
