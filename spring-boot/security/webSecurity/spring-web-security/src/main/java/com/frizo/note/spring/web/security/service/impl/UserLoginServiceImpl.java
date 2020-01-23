package com.frizo.note.spring.web.security.service.impl;

import com.frizo.note.spring.web.security.dao.UserLoginDao;
import com.frizo.note.spring.web.security.service.UserLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// 實作 UserLoginService (UserDetailsService)，實現自訂義找尋 USER 資訊
@Service
public class UserLoginServiceImpl implements UserLoginService {
    @Autowired
    UserLoginDao userLoginDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userLoginDao.getByUsername(username);
    }
}
