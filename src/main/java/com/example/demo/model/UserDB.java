package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "users")
public class UserDB {
    @Id
    @Column(name = "user_id")
    Long userId;
    String username;
    String password;
    String role;

    @Column(name = "morion_login")
    String morionLogin;

    @Column(name = "morion_key")
    String morionKey;

    @Column(name = "morion_corp_id")
    String morionCorpId;
}
