package study.microcoffee.creditrating;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;

import springfox.documentation.spring.web.plugins.WebFluxRequestHandlerProvider;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;

/**
 * Configuration class to hack Springfox WebMvcRequestHandlerProvider to filter out Actuator controllers which don't respect
 * spring.mvc.pathmatch.matching-strategy. The code is copied from
 * <a href="https://github.com/springfox/springfox/issues/3462">Springfox issue 3462</a>.
 * <p>
 * This configuration class complements reverting the matching strategy spring.mvc.pathmatch.matching-strategy to ant-path-matcher
 * in <code>application.properties</code> as follows.
 *
 * <pre>
 * spring.mvc.pathmatch.matching-strategy=ant-path-matcher
 * </pre>
 *
 * This configuration is needed until a final fix is applied, either and preferably 1) Springfox fixes the issue, or 2) Migrate away
 * from Springfox to Springdoc.
 *
 * @see <a href=
 *      "https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.6-Release-Notes#pathpattern-based-path-matching-strategy-for-spring-mvc">Spring-Boot-2.6-Release-Notes
 *      pathpattern-based-path-matching-strategy-for-spring-mvc</a>
 */
@Configuration
public class Springfox3SpringBoot26Config {

    @Bean
    public BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
        return new BeanPostProcessor() {

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof WebMvcRequestHandlerProvider || bean instanceof WebFluxRequestHandlerProvider) {
                    customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
                }
                return bean;
            }

            private <T extends RequestMappingInfoHandlerMapping> void customizeSpringfoxHandlerMappings(List<T> mappings) {
                List<T> copy = mappings.stream().filter(mapping -> mapping.getPatternParser() == null).collect(Collectors.toList());
                mappings.clear();
                mappings.addAll(copy);
            }

            @SuppressWarnings("unchecked")
            private List<RequestMappingInfoHandlerMapping> getHandlerMappings(Object bean) {
                try {
                    Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
                    field.setAccessible(true); // NOSONAR Allow in workaround
                    return (List<RequestMappingInfoHandlerMapping>) field.get(bean);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }
        };
    }
}
