package com.example.demo.model.user;

import com.example.demo.model.UserDB;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User implements UserDetails {
    String username;
    String password;
    Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role.name()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public User(UserDB userDb) {
        this.username = userDb.getUsername();
        this.password = userDb.getPassword();
        switch (userDb.getRole()) {
            case "SHOP" -> this.role = Role.SHOP;
            case "ADMIN" -> this.role = Role.ADMIN;
        }
    }
}
