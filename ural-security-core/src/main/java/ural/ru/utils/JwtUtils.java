package ural.ru.utils;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import ural.ru.enums.UserRole;
import ural.ru.models.UserPrincipals;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JwtUtils {

    private static final String USER_UUID_KEY = "user_uuid";

    private static final String ROLES_KEY = "roles";

    private static final String EMAIL_KEY = "email";

    private JwtUtils(){
    }

    @NonNull
    public static UserPrincipals getUser(@Nullable Authentication authentication) {
        Jwt jwt = getToken(authentication);
        return UserPrincipals.builder()
                .uuid(getClaim(jwt, USER_UUID_KEY, String.class))
                .roles(getRoles(jwt))
                .email(getClaim(jwt, EMAIL_KEY, String.class))
                .build();
    }

    @NonNull
    public static Jwt getToken(@Nullable Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken) {
            return ((JwtAuthenticationToken) authentication).getToken();
        }
        throw new IllegalArgumentException("Authentification not cast to JwtAuthenticationToken");
    }

    @NonNull
    public static Map<String, Object> getClaims(@Nullable Authentication authentication) {
        Jwt jwt = getToken(authentication);
        return jwt.getClaims();
    }

    @NonNull
    private static List<UserRole> getRoles(@Nullable Jwt jwt) {
        List<Object> roles = getClaim(jwt, ROLES_KEY, List.class);
        return roles.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(UserRole::valueOf)
                .toList();
    }

    private static <T> T getClaim(Jwt jwt, String key, Class<T> clazz) {
        Object object = jwt.getClaims().getOrDefault(key, null);
        return Optional.ofNullable(object)
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .orElse(null);
    }

}
