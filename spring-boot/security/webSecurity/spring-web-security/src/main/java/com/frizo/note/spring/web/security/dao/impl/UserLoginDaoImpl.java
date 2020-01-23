package com.frizo.note.spring.web.security.dao.impl;

import com.frizo.note.spring.web.security.dao.UserLoginDao;
import com.frizo.note.spring.web.security.entity.SysRole;
import com.frizo.note.spring.web.security.entity.SysUser;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserLoginDaoImpl implements UserLoginDao {
    private static final Map<String, SysUser> USER_TABLE;
    static {
        // 模擬 Table 資料
        USER_TABLE = new HashMap<>();

        // 建立一般 user 資料
        SysUser user = new SysUser();
        user.setId(1L);
        user.setUsername("user");
        user.setPassword("{SHA-256}04f8996da763b7a969b1028ee3007569eaf3a635486ddab211d512c85b9df8fb");
        user.setEnabled(true);
        user.setExpired(false);
        user.setLocked(false);
        List<SysRole> userRole = new ArrayList<>();
        userRole.add(new SysRole(1L, "USER"));
        user.setRoles(userRole);

        // 建立管理者 admin 資料
        SysUser admin = new SysUser();
        admin.setId(2L);
        admin.setUsername("admin");
        admin.setPassword("{SHA-256}8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918");
        admin.setEnabled(true);
        admin.setExpired(false);
        admin.setLocked(false);
        List<SysRole> adminRoles = new ArrayList<>();
        adminRoles.add(new SysRole(1L, "USER"));
        adminRoles.add(new SysRole(2L, "ADMIN"));
        admin.setRoles(adminRoles);

        USER_TABLE.put(user.getUsername(), user);
        USER_TABLE.put(admin.getUsername(), admin);
    }

    @Override
    public SysUser getByUsername(String username) {
        return Optional.ofNullable(USER_TABLE.get(username)).orElse(null);
    }
}
