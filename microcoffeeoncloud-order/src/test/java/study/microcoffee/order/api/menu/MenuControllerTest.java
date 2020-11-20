package study.microcoffee.order.api.menu;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import study.microcoffee.order.CharacterEncodingFilterTestConfig;
import study.microcoffee.order.common.logging.HttpLoggingFilterTestConfig;
import study.microcoffee.order.repository.MenuRepository;

/**
 * Unit tests of {@link MenuController}.
 */
@WebMvcTest(MenuController.class)
@TestPropertySource(properties = { "logging.level.study.microcoffee=DEBUG" })
@Import({ HttpLoggingFilterTestConfig.class, CharacterEncodingFilterTestConfig.class })
public class MenuControllerTest {

    private static final String SERVICE_PATH = "/api/coffeeshop/menu";

    @MockBean
    private MenuRepository menuRepositoryMock;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getCoffeeMenuShouldReturnCoffeeMenu() throws Exception {
        final String expectedMenu = "{ \"menu\": \"coffee (kå·fee)\" }";

        given(menuRepositoryMock.getCoffeeMenu()).willReturn(expectedMenu);

        mockMvc.perform(get(SERVICE_PATH).accept(MediaType.APPLICATION_JSON)) //
            .andExpect(status().isOk()) //
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)) //
            .andExpect(content().json(expectedMenu));
    }
}
