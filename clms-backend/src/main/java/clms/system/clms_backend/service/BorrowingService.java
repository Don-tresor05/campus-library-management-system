package clms.system.clms_backend.service;

import clms.system.clms_backend.dto.BorrowingDTO;
import clms.system.clms_backend.model.Book;
import clms.system.clms_backend.model.Borrowing;
import clms.system.clms_backend.model.User;
import clms.system.clms_backend.repository.BookRepository;
import clms.system.clms_backend.repository.BorrowingRepository;
import clms.system.clms_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BorrowingService {
    @Autowired
    private BorrowingRepository borrowingRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FineService fineService;

    private static final int BORROWING_PERIOD_DAYS = 14; // 2 weeks

    public BorrowingDTO borrowBook(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException("Book is not available");
        }

        if (borrowingRepository.findActiveBorrowingByBook(book).isPresent()) {
            throw new RuntimeException("Book is already borrowed");
        }

        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(BORROWING_PERIOD_DAYS);

        Borrowing borrowing = new Borrowing();
        borrowing.setUser(user);
        borrowing.setBook(book);
        borrowing.setBorrowDate(borrowDate);
        borrowing.setDueDate(dueDate);
        borrowing.setStatus(Borrowing.BorrowingStatus.ACTIVE);

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        if (book.getAvailableCopies() == 0) {
            book.setStatus(Book.BookStatus.BORROWED);
        }

        bookRepository.save(book);
        Borrowing savedBorrowing = borrowingRepository.save(borrowing);

        return convertToDTO(savedBorrowing);
    }

    public BorrowingDTO returnBook(Long borrowingId) {
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new RuntimeException("Borrowing not found"));

        if (borrowing.getStatus() != Borrowing.BorrowingStatus.ACTIVE) {
            throw new RuntimeException("Borrowing is not active");
        }

        borrowing.setReturnDate(LocalDate.now());
        borrowing.setStatus(Borrowing.BorrowingStatus.RETURNED);

        Book book = borrowing.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        if (book.getStatus() == Book.BookStatus.BORROWED) {
            book.setStatus(Book.BookStatus.AVAILABLE);
        }
        bookRepository.save(book);

        // Check for overdue and calculate fine
        if (borrowing.getReturnDate().isAfter(borrowing.getDueDate())) {
            fineService.calculateOverdueFine(borrowing);
        }

        Borrowing savedBorrowing = borrowingRepository.save(borrowing);
        return convertToDTO(savedBorrowing);
    }

    public List<BorrowingDTO> getUserBorrowings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return borrowingRepository.findByUser(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BorrowingDTO> getActiveBorrowings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return borrowingRepository.findActiveBorrowingsByUser(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BorrowingDTO> getAllBorrowings() {
        return borrowingRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BorrowingDTO> getOverdueBorrowings() {
        return borrowingRepository.findOverdueBorrowings(LocalDate.now()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private BorrowingDTO convertToDTO(Borrowing borrowing) {
        BorrowingDTO dto = new BorrowingDTO();
        dto.setId(borrowing.getId());
        dto.setUserId(borrowing.getUser().getId());
        dto.setUsername(borrowing.getUser().getUsername());
        dto.setBookId(borrowing.getBook().getId());
        dto.setBookTitle(borrowing.getBook().getTitle());
        dto.setBookIsbn(borrowing.getBook().getIsbn());
        dto.setBorrowDate(borrowing.getBorrowDate());
        dto.setDueDate(borrowing.getDueDate());
        dto.setReturnDate(borrowing.getReturnDate());
        dto.setStatus(borrowing.getStatus());
        dto.setCreatedAt(borrowing.getCreatedAt());
        return dto;
    }
}







