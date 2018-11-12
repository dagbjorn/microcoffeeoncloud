package study.microcoffee.order.api.menu;

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

import study.microcoffee.order.api.menu.MenuController;
import study.microcoffee.order.common.logging.HttpLoggingFilterTestConfig;
import study.microcoffee.order.repository.MenuRepository;

/**
 * Unit tests of {@link MenuController}.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(MenuController.class)
@TestPropertySource(properties = { "logging.level.study.microcoffee=DEBUG" })
@Import(HttpLoggingFilterTestConfig.class)
public class MenuControllerTest {

    private static final String SERVICE_PATH = "/api/coffeeshop/menu";

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
}
