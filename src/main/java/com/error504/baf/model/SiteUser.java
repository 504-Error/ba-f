package com.error504.baf.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
public class SiteUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String username;

    private String password;

    private String birthday;

    private int gender;

    @Column(unique = true)
    private String email;

    private int type;

    private int getWheel;

    private String certifyFilePath;

    private int getAuth;

    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(name = "review_voter", joinColumns = @JoinColumn(name = "voter_id"), inverseJoinColumns = @JoinColumn(name = "review_id"))
    Set<Review> voter;

    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(name = "review_accuser", joinColumns = @JoinColumn(name = "accuser_id"), inverseJoinColumns = @JoinColumn(name = "review_id"))
    Set<Review> accuser;

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
    public void updateEmail(String newEmail) {
        this.email = newEmail;
    }
    public void upadteAuth(int newAuth){
        this.getAuth = newAuth;
    }


}
