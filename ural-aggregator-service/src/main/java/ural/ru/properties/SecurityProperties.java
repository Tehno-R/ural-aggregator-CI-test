package ural.ru.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Getter
@ConfigurationProperties(prefix = "spring.security")
public class SecurityProperties {

    private Map<String, String> mappingRoles;

}
