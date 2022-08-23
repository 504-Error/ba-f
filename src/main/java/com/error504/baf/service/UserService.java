package com.error504.baf.service;

import com.error504.baf.exception.DataNotFoundException;
import com.error504.baf.model.SiteUser;
import com.error504.baf.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public SiteUser getUser(String username){
        Optional<SiteUser> siteUser = userRepository.findByUsername(username);
        if(siteUser.isPresent()){
            return siteUser.get();
        }else{
            throw new DataNotFoundException("siteuser not found");
        }
    }

    public SiteUser getUser(Long id){
        Optional<SiteUser> siteUser = userRepository.findById(id);
        if(siteUser.isPresent()){
            return siteUser.get();
        }else{
            throw new DataNotFoundException("siteuser not found");
        }
    }

    public Page<SiteUser> getList(int page, String keyword, int getAuth) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page, 4, Sort.by(sorts));
        Specification<SiteUser> spec = searchUser(keyword, getAuth);
        return this.userRepository.findAll(spec, pageable);
    }

    public SiteUser create(String username, String name, int gender, Date birthday, String email, String password, int type, int getWheel){
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setName(name);
        user.setGender(gender);
        user.setBirthday(birthday);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setType(type);
        user.setGetWheel(getWheel);
        userRepository.save(user);
        return user;
    }

    public void uploadCertifyFile(SiteUser siteUser, String path) {
        siteUser.setCertifyFilePath(path);
        this.userRepository.save(siteUser);
    }

    public void updatePassword(SiteUser siteUser, String newPassword) {
        siteUser.updatePassword(passwordEncoder.encode(newPassword));
        userRepository.save(siteUser);
    }
    public void updateEmail(SiteUser siteUser, String newEmail) {
        siteUser.updateEmail(newEmail);
        userRepository.save(siteUser);
    }

    public void updateMemberAuth(SiteUser siteUser, int updateAuth) {
        siteUser.upadteAuth(updateAuth);
        userRepository.save(siteUser);
    }

    public void deleteMember(SiteUser siteUser) {
        userRepository.delete(siteUser);
    }

    private Specification searchUser(String keyword, int getAuth){
        return (user, query, criteriaBuilder) -> {
            query.distinct(true);

            return criteriaBuilder.and(
                    criteriaBuilder.like(user.get("username"), "%" + keyword + "%"),
                    criteriaBuilder.equal(user.get("getAuth"), getAuth)
            );
        };
    }
}

