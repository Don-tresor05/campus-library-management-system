package clms.system.clms_backend.dto;

import clms.system.clms_backend.model.Borrowing;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BorrowingDTO {
    private Long id;
    private Long userId;
    private String username;
    private Long bookId;
    private String bookTitle;
    private String bookIsbn;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private Borrowing.BorrowingStatus status;
    private LocalDateTime createdAt;
}







