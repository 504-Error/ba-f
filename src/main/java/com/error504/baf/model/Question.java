package com.error504.baf.model;

import com.error504.baf.Time;
import lombok.Getter;
import lombok.Setter;

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

    private LocalDateTime createDate;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;

    @ManyToMany
    Set<SiteUser> voter;

    @ManyToOne
    private Board board;


    private String date;
    public String getDate( LocalDateTime time){
        date = Time.convertLocaldatetimeToTime(time);
        return date;
    }
}
