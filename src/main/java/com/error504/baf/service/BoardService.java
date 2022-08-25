package com.error504.baf.service;

import com.error504.baf.model.*;
import com.error504.baf.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BoardService {
    private BoardRepository boardRepository;

    @Autowired
    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public Page<Board> getList(int page, String keyword) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.asc("id"));
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));
        Specification<Board> spec = searchBoard(keyword);
        return boardRepository.findAll(spec, pageable);
    }

    public Page<Board> getList(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));
        return boardRepository.findAll(pageable);
    }

    public List<Board> findAll() {
        return boardRepository.findAll();
    }

    public void create(String boardName, String boardIntro, SiteUser user) {
        Board q = new Board();
        q.setBoardName(boardName);
        q.setBoardIntro(boardIntro);
        boardRepository.save(q);
    }

    public Board getBoard(long id) {
        return boardRepository.findBoardById(id);
    }

    public void delete(Board board) {
        this.boardRepository.delete(board);
    }

    public Specification searchBoard(String keyword) {
        return (board, query, criteriaBuilder) -> {
            query.distinct(true);

            return criteriaBuilder.like(board.get("boardName"), "%" + keyword + "%");
        };
    }

}
