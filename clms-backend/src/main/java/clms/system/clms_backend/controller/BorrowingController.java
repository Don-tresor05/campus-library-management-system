package clms.system.clms_backend.controller;

import clms.system.clms_backend.dto.BorrowingDTO;
import clms.system.clms_backend.service.BorrowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrowings")
@CrossOrigin(origins = "http://localhost:5173")
public class BorrowingController {
    @Autowired
    private BorrowingService borrowingService;

    @PostMapping("/borrow")
    public ResponseEntity<BorrowingDTO> borrowBook(@RequestParam Long userId, @RequestParam Long bookId) {
        try {
            BorrowingDTO borrowing = borrowingService.borrowBook(userId, bookId);
            return ResponseEntity.status(HttpStatus.CREATED).body(borrowing);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/return/{id}")
    public ResponseEntity<BorrowingDTO> returnBook(@PathVariable Long id) {
        try {
            BorrowingDTO borrowing = borrowingService.returnBook(id);
            return ResponseEntity.ok(borrowing);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BorrowingDTO>> getUserBorrowings(@PathVariable Long userId) {
        return ResponseEntity.ok(borrowingService.getUserBorrowings(userId));
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<BorrowingDTO>> getActiveBorrowings(@PathVariable Long userId) {
        return ResponseEntity.ok(borrowingService.getActiveBorrowings(userId));
    }

    @GetMapping("/all")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<List<BorrowingDTO>> getAllBorrowings() {
        return ResponseEntity.ok(borrowingService.getAllBorrowings());
    }

    @GetMapping("/overdue")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<List<BorrowingDTO>> getOverdueBorrowings() {
        return ResponseEntity.ok(borrowingService.getOverdueBorrowings());
    }
}
