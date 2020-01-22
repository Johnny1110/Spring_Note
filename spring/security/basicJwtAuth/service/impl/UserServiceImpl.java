package basicJwtAuth.service.impl;

import basicJwtAuth.entity.SysRole;
import basicJwtAuth.entity.SysUser;
import basicJwtAuth.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements UserService {
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        if(s.equals("Johnny")){
            SysRole role = new SysRole();
            role.setId(1L);
            role.setName("USER");
            SysUser user = new SysUser();
            user.setId(1L);
            user.setEnabled(true);
            user.setExpired(false);
            user.setLocked(false);
            user.setUsername("Johnny");
            user.setPassword("Jarvan1110");
            List<SysRole> roles = new ArrayList<>();
            roles.add(role);
            user.setRoles(roles);
            return user;
        }else {
            return null;
        }
    }
}
