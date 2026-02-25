package ural.ru.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import ural.ru.services.RoleSecurityService;

import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AuthConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final RoleSecurityService roleSecurityService;

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        return roleSecurityService.getRoles(jwt.getClaims()).stream()
                .map(roleName -> "ROLE_" + roleName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

}
