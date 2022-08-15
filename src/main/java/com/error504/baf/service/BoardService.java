package com.error504.baf.service;

import com.error504.baf.model.Board;
import com.error504.baf.model.Question;
import com.error504.baf.model.SiteUser;
import com.error504.baf.repository.BoardRepository;
import com.error504.baf.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BoardService {
    private BoardRepository boardRepository;

    @Autowired
    public BoardService(BoardRepository boardRepository){
        this.boardRepository = boardRepository;
    }

    public Page<Board> getList(int page){
        List<Sort.Order> sorts = new ArrayList<>();
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return boardRepository.findAll(pageable);
    }

    public List<Board> findAll() {
        return boardRepository.findAll();
    }

    public void create(String boardName, String boardIntro, SiteUser user)
    { Board q = new Board();
        q.setBoardName(boardName);
        q.setBoardIntro(boardIntro);
        boardRepository.save(q);
    }
    public Board getBoard(long id) {
        return boardRepository.findBoardById(id);
    }

}
