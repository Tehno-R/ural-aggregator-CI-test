package ural.ru.services;

import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
public class RoleSecurityService {

    private static final String KEY_ROLES = "roles";

    public Set<String> getRoles(Map<String, Object> claims) {
        return new HashSet<>(Optional.ofNullable(claims)
                .map(cl -> cl.get(KEY_ROLES))
                .map(ra -> (List<String>) ra)
                .orElse(Collections.emptyList())
        );
    }

}
