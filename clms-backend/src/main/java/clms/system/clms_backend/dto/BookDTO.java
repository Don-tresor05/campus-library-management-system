package clms.system.clms_backend.dto;

import clms.system.clms_backend.model.Book;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookDTO {
    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private Integer publicationYear;
    private String category;
    private String description;
    private Integer totalCopies;
    private Integer availableCopies;
    private Book.BookStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}







