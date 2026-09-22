package com.app.oslotoilet.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leaderboard")
public class LeaderboardController {

    private final UserService userService;

    public LeaderboardController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<LeaderboardEntryDto>> getLeaderboard(){
        return ResponseEntity.ok(userService.getLeaderboard());
    }
}
