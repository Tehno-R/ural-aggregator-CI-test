package ural.ru.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import ural.ru.properties.SecurityProperties;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private static final String KEY_ROLES = "roles";

    private final SecurityProperties securityProps;

    public Set<String> getRoles(Map<String, Object> claims) {
        return new HashSet<>(Optional.ofNullable(claims)
                .map(cl -> cl.get(KEY_ROLES))
                .map(ra -> (List<String>) ra)
                .orElse(Collections.emptyList())
        );
    }

}
