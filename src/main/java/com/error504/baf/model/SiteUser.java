package com.error504.baf.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Entity
public class SiteUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    @Column(unique = true)
    private String email;

    private int gender;

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
