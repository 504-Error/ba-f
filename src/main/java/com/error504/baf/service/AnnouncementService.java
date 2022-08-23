package com.error504.baf.service;

import com.error504.baf.exception.DataNotFoundException;
import com.error504.baf.model.Announcement;
import com.error504.baf.repository.AnnouncementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;

    public AnnouncementService(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    public Announcement getAnnouncement(Long id) {
        Optional<Announcement> announcement = this.announcementRepository.findById(id);
        if (announcement.isPresent()) {
            return announcement.get();
        } else {
            throw new DataNotFoundException("Announcement not found");
        }
    }

    public Page<Announcement> getList(int page, String keyword) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page, 4, Sort.by(sorts));
        Specification<Announcement> spec = searchAnnouncement(keyword);
        return this.announcementRepository.findAll(spec, pageable);
    }


    public Long create(String subject, String content) {
        Announcement announcement = new Announcement();
        announcement.setSubject(subject);
        announcement.setContent(content);
        announcement.setCreateDate(LocalDateTime.now());
        this.announcementRepository.save(announcement);
        this.announcementRepository.flush();
        long id = announcement.getId();

        return id;
    }

    public void delete(Announcement announcement) {
        this.announcementRepository.delete(announcement);
    }

    private Specification searchAnnouncement(String keyword) {
        return (announcement, query, criteriaBuilder) -> {
            query.distinct(true);

            return criteriaBuilder.or(
                    criteriaBuilder.like(announcement.get("subject"), "%" + keyword + "%"),
                    criteriaBuilder.like(announcement.get("content"),  "%" + keyword + "%")
            );
        };
    }
}
