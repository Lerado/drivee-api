package com.lerado.drivee.entities;

import java.util.Date;
import java.util.UUID;

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
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationDate;

    @OneToOne(mappedBy = "userRefreshToken")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public RefreshToken(String token, Date expirationDate) {
        this.setToken(token);
        this.setExpirationDate(expirationDate);
    }

    public void renewToken() {
        this.setToken(UUID.randomUUID().toString());
        this.setExpirationDate(new Date(System.currentTimeMillis() + 1000 * 60 * 60));
    }

    public boolean isExpired() {
        return this.expirationDate.before(new Date());
    }
}
