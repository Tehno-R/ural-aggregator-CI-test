package ural.ru.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import ural.ru.services.RoleService;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuthConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final RoleService roleService;

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        return roleService.getRoles(jwt.getClaims()).stream()
                .map(roleName -> "ROLE_" + roleName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

}
