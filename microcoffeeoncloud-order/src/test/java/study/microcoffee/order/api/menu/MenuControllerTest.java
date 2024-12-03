package study.microcoffee.order.api.menu;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import study.microcoffee.order.CharacterEncodingFilterTestConfig;
import study.microcoffee.order.SecurityConfig;
import study.microcoffee.order.SecurityTestConfig;
import study.microcoffee.order.common.logging.HttpLoggingFilterTestConfig;
import study.microcoffee.order.repository.MenuRepository;

/**
 * Unit tests of {@link MenuController}.
 */
@WebMvcTest(MenuController.class)
@TestPropertySource("/application-test.properties")
@Import({ HttpLoggingFilterTestConfig.class, CharacterEncodingFilterTestConfig.class, SecurityTestConfig.class,
    SecurityConfig.class })
class MenuControllerTest {

    private static final String SERVICE_PATH = "/api/coffeeshop/menu";

    @MockitoBean
    private MenuRepository menuRepositoryMock;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getCoffeeMenuShouldReturnCoffeeMenu() throws Exception {
        final String expectedMenu = "{ \"menu\": \"coffee (kå·fee)\" }";

        given(menuRepositoryMock.getCoffeeMenu()).willReturn(expectedMenu);

        mockMvc.perform(get(SERVICE_PATH).accept(MediaType.APPLICATION_JSON)) //
            .andExpect(status().isOk()) //
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)) //
            .andExpect(content().json(expectedMenu));
    }
}
