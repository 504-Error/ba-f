package com.error504.baf.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;

    @ManyToOne
    private SiteUser author;

    @Column(columnDefinition = "TEXT")
    private String genre;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "DATE")
    private Date date;

    @Column(columnDefinition = "TEXT")
    private String place;

    @Column(columnDefinition = "INT")
    private Integer grade;

    @Column(columnDefinition = "TEXT")
    private String amenities;

//    @Column(columnDefinition = "BOOLEAN")
//    private Boolean amenities_elevator;
//    @Column(columnDefinition = "BOOLEAN")
//    private Boolean amenities_incline;
//    @Column(columnDefinition = "BOOLEAN")
//    private Boolean amenities_parking;
//    @Column(columnDefinition = "BOOLEAN")
//    private Boolean amenities_table;
//    @Column(columnDefinition = "BOOLEAN")
//    private Boolean amenities_restRoom;

    @Column(columnDefinition = "TEXT")
    private String placeReview;

    @Column(columnDefinition = "TEXT")
    private String additionalReview;

    @Column(columnDefinition = "BOOLEAN")
    private Boolean isAnonymous;

    private LocalDateTime createDate;

    @OneToMany(mappedBy = "review", cascade = CascadeType.REMOVE)
    private List<ReviewComment> reviewCommentsList;

    @ManyToMany
    Set<SiteUser> voter;
}
