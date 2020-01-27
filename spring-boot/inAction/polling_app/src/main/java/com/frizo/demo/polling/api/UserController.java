package com.frizo.demo.polling.api;

import com.frizo.demo.polling.entity.User;
import com.frizo.demo.polling.payload.*;
import com.frizo.demo.polling.security.CurrentUser;
import com.frizo.demo.polling.security.UserPrincipal;
import com.frizo.demo.polling.service.PollService;
import com.frizo.demo.polling.service.UserService;
import com.frizo.demo.polling.service.VoteService;
import com.frizo.demo.polling.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    PollService pollService;

    @Autowired
    VoteService voteService;


    @GetMapping("/user/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DBA')")
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        return new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getName());
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DBA')")
    @PostMapping("/user/reset/password")
    public ResponseEntity<ApiResponse> resetPassword(@CurrentUser UserPrincipal currentUser,
                                                     @Valid @RequestBody ResetPasswordRequest req){
        req.setUsernameOrEmail(currentUser.getUsername());
        req.setUserId(currentUser.getId());
        ApiResponse response = userService.resetUserPassword(req);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/user/checkUsernameAvailability")
    public UserIdentityAvailability checkUsernameAvailability(@RequestParam(value = "username") String username) {
        Boolean isAvailable = !userService.existsByUsername(username);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping("/user/checkEmailAvailability")
    public UserIdentityAvailability checkEmailAvailability(@RequestParam(value = "email") String email) {
        Boolean isAvailable = !userService.existsByEmail(email);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping("/users/{username}")
    public UserProfile getUserProfile(@PathVariable(value = "username") String username) {
        User user = userService.findByUsername(username);
        long pollCount = pollService.countByCreatedBy(user.getId());
        long voteCount = voteService.countByUserId(user.getId());
        return new UserProfile(user.getId(),
                               user.getUsername(),
                               user.getName(),
                               user.getCreatedAt(),
                               pollCount, voteCount);
    }

    @GetMapping("/users/{username}/polls")
    public PagedResponse<PollResponse> getPollsCreatedBy(@PathVariable(value = "username") String username,
                                                         @CurrentUser UserPrincipal currentUser,
                                                         @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                         @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return pollService.getPollsCreatedBy(username, currentUser, page, size);
    }

    @GetMapping("/users/{username}/votes")
    public PagedResponse<PollResponse> getPollsVotedBy(@PathVariable(value = "username") String username,
                                                       @CurrentUser UserPrincipal currentUser,
                                                       @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                       @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return pollService.getPollsVotedBy(username, currentUser, page, size);
    }
}
