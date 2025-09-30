package com.example.api_test.security_config;


import com.example.api_test.entity.User_info;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class Dao_auth implements UserDetails {

    private final User_info user;

    public Dao_auth(User_info user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // DB থেকে role ধরে authority বানানো হচ্ছে
        return List.of(new SimpleGrantedAuthority(user.getRoles()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }
    public User_info getUserModel() { return user; }
    @Override
    public boolean isAccountNonExpired() {
        return true; // সবসময় active
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // সবসময় unlocked
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // সবসময় valid
    }

    @Override
    public boolean isEnabled() {
        return true; // সবসময় enabled
    }
}
