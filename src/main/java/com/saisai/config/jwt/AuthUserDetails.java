package com.saisai.config.jwt;

import com.saisai.domain.user.entity.User;
import com.saisai.domain.user.entity.UserRole;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record AuthUserDetails(
    Long userId,
    String email,
    UserRole userRole
) implements UserDetails
{
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userRole.name()));
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    public static AuthUserDetails from(Long userId, String email, UserRole userRole) {
        return new AuthUserDetails(
            userId,
            email,
            userRole
        );
    }

    public static AuthUserDetails from(User user) {
        return new AuthUserDetails(
            user.getId(),
            user.getEmail(),
            user.getRole()
        );
    }
}
