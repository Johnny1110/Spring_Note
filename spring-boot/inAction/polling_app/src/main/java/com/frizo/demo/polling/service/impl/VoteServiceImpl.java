package com.frizo.demo.polling.service.impl;

import com.frizo.demo.polling.dao.VoteRepository;
import com.frizo.demo.polling.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VoteServiceImpl implements VoteService {
    @Autowired
    VoteRepository voteRepository;


    @Override
    public Long countByUserId(Long id) {
        return voteRepository.countByUserId(id);
    }
}
