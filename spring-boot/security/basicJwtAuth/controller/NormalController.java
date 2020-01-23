package basicJwtAuth.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;


@RestController
public class NormalController {
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping("/sayHello")
    public String forwardToIndex(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println(username);
        authentication.getAuthorities().forEach(System.out::println);
        return username;
    }
}
