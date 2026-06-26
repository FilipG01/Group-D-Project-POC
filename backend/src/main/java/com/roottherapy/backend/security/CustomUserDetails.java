package com.roottherapy.backend.security;

import com.roottherapy.backend.users.AccountStatus;
import org.springframework.security.core.GrantedAuthority;
import com.roottherapy.backend.users.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;


public class CustomUserDetails implements UserDetails, Serializable {

    private final User user;
    @Serial
    private static final long serialVersionUID = 1L;

    public CustomUserDetails(User user){
        this.user = user;
    }

    public User getUser(){
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return List.of(new SimpleGrantedAuthority("ROLE_"+ user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername(){
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonLocked(){
        return user.getAccountStatus() != AccountStatus.SUSPENDED;
    }

    @Override
    public boolean isEnabled(){
        return user.getAccountStatus() == AccountStatus.ACTIVE;
    }

}
