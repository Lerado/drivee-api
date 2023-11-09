package com.lerado.drivee.entities;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    @Column(unique = true, nullable = false)
    private String password;

    @Column(nullable = false)
    private String roles = "USER";

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "refresh_token_id", unique = true)
    @JsonIgnore
    private RefreshToken userRefreshToken;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Date createdAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getRoles()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @JsonIgnore
    public String getRefreshToken() {
        return this.userRefreshToken != null ? this.userRefreshToken.getToken() : null;
    }

    @JsonIgnore
    public boolean isTokenExpired() {
        return this.userRefreshToken.isExpired();
    }

    public void renewToken() {
        this.userRefreshToken.renewToken();
    }

    public Collection<String> getRoles() {
        return Arrays.asList(this.roles.split(","));
    }

    public void addRole(String role) {
        if (this.hasRole(role)) return;
        this.setRoles(
            String.format("%s,%s", this.roles, role));
    }

    public boolean hasRole(String roleName) {
        return this.getRoles().contains(roleName);
    }

    @Override
    public String getUsername() {
        return this.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
