package com.frizo.note.spring.web.security.dao;

import com.frizo.note.spring.web.security.entity.SysUser;

public interface UserLoginDao {
    public SysUser getByUsername(String username);
}
