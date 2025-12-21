package clms.system.clms_backend.controller;

import clms.system.clms_backend.model.Book;
import clms.system.clms_backend.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final clms.system.clms_backend.service.UserService userService;

    @GetMapping("/trending")
    public ResponseEntity<List<Book>> getTrending() {
        return ResponseEntity.ok(recommendationService.getTrendingBooks());
    }

    @GetMapping("/new")
    public ResponseEntity<List<Book>> getNewArrivals() {
        return ResponseEntity.ok(recommendationService.getNewArrivals());
    }

    @GetMapping("/user")
    public ResponseEntity<List<Book>> getUserRecommendations(@RequestParam String username) {
        Long userId = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
        return ResponseEntity.ok(recommendationService.getRecommendedForUser(userId));
    }
}
