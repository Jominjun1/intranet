package com.example.tag_dev.USER.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="refresh_token")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable= false)
    private Long userId;

    @Column(nullable = false , unique = true)
    private String refreshToken;

    @Column(nullable = false)
    private Date expiresAt;

    @Column(nullable = false)
    private Date createdAt;

}
