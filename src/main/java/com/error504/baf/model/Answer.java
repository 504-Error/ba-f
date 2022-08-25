package com.error504.baf.model;

import com.error504.baf.SetTime;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    @ManyToOne
    private Question question;

    @ManyToOne
    private SiteUser author;

    @ManyToMany
    private Set<SiteUser> voter;

    @Column(columnDefinition = "BOOLEAN")
    private Boolean isAnonymous;
    private String date;
    public String getDate( LocalDateTime time){
        date = SetTime.convertLocaldatetimeToTime(time);
        return date;
    }
}
