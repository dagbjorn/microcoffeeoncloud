package study.microcoffee.order.rest.menu;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import study.microcoffee.order.repository.MenuRepository;
import study.microcoffee.order.rest.menu.MenuRestService;

/**
 * Unit tests of {@link MenuRestService}.
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@TestPropertySource(properties = { "logging.level.study.microcoffee=DEBUG" })
public class MenuRestServiceTest {

    private static final String SERVICE_PATH = "/coffeeshop/menu";

    @MockBean
    private MenuRepository menuRepositoryMock;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getCoffeeMenuShouldReturnCoffeeMenu() throws Exception {
        final String expectedMenu = "{ \"menu\": \"coffee\" }";

        given(menuRepositoryMock.getCoffeeMenu()).willReturn(expectedMenu);

        mockMvc.perform(get(SERVICE_PATH).accept(MediaType.APPLICATION_JSON_UTF8)) //
            .andExpect(status().isOk()) //
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)) //
            .andExpect(content().json(expectedMenu));
    }

    @Configuration
    @Import(MenuRestService.class)
    static class Config {
    }
}
