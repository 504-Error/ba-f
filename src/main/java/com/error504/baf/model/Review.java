package com.error504.baf.model;

import com.error504.baf.SetTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private SiteUser author;

    @Column(columnDefinition = "TEXT")
    private String genre;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT" )
    private String date;

    @Column(columnDefinition = "TEXT")
    private String place;

    @Column(columnDefinition = "TEXT")
    private String placeAddress;

    @Column(columnDefinition = "INT")
    private Integer grade;

    @Column(columnDefinition = "TEXT")
    private String amenities;

    @Column(columnDefinition = "TEXT")
    private String placeReview;

    @Column(columnDefinition = "TEXT")
    private String additionalReview;

    @Column(columnDefinition = "BOOLEAN")
    private Boolean isAnonymous;

    private LocalDateTime createDate;

    @OneToMany(mappedBy = "review", cascade = CascadeType.REMOVE)
    private List<ReviewComment> reviewCommentsList;

    @OneToMany(mappedBy = "review", cascade = CascadeType.REMOVE)
    private List<ReviewImage> reviewImages;

    @ManyToMany
    private Set<SiteUser> voter;

    @Formula("(select count(*) from review_voter where review_voter.review_id=id)")
    private int voterCount;

    @ManyToMany
    private Set<SiteUser> accuser;

    @Formula("(select count(*) from review_accuser where review_accuser.review_id=id)")
    private int accuserCount;

    public String getDate( LocalDateTime time){
        date = SetTime.convertLocaldatetimeToTime(time);
        return date;
    }
}