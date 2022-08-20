package com.error504.baf.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class QuestionImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "INT")
    private Integer imageNUm;

    @Column(columnDefinition = "TEXT")
    private String image;

    @ManyToOne
private Question question;
}