package com.error504.baf.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String boardName;
    private String boardIntro;



    @OneToMany (mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<Question> questionList;
}
