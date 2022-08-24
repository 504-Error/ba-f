package com.error504.baf.model;

import com.error504.baf.Time;
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
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;

    @ManyToOne
    private SiteUser author;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<QuestionImage> questionImages;

    private LocalDateTime createDate;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;

    @ManyToMany
    Set<SiteUser> voter;

//    @ManyToMany
//    @JoinTable(name = "question_voter", joinColumns = @JoinColumn(name = "voter_id"), inverseJoinColumns = @JoinColumn(name = "question_id"))
//    Set<Question> questionVoter;

    @Formula("(select count(*) from question_voter where question_voter.question_id=id)")
    private int voterCount;

    @ManyToMany
    Set<SiteUser> accuser;

    @Formula("(select count(*) from question_accuser where question_accuser.question_id=id)")
    private int accuserCount;

    @ManyToOne
    private Board board;

    @Column(columnDefinition = "BOOLEAN")
    private Boolean isAnonymous;
    private String date;

    public String getDate( LocalDateTime time){
        date = Time.convertLocaldatetimeToTime(time);
        return date;
    }
}
