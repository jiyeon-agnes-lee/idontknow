package com.idk.api.user.domain.entity;

import com.idk.api.common.entity.BaseEntity;
import com.idk.api.districtcode.domain.entity.DistrictCode;
import com.idk.api.user.domain.Role;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "user")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String email;

    @Column(name = "nickname")
    private String name;

    private String password;

    private String gender;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id")

    private DistrictCode districtCode;

    private int age;

    @Column(name = "refresh_token")
    private String refreshToken;

    public void updatePassword(String password){
        this.password = password;
    }
    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }
    public void deleteInfo(){
        this.name = "알 수 없음";
        this.email = "deleted@idontknow.com";
        this.setDeletedAt(LocalDateTime.now());
    }

    public void updateUserInfo(DistrictCode districtCode, String gender, int age){
        if(districtCode != null)    this.districtCode = districtCode;
        if(gender != null)  this.gender = gender;
        if(age != 0)    this.age = age;

    }
}
