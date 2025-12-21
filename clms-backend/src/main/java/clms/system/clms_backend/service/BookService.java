package clms.system.clms_backend.service;

import clms.system.clms_backend.dto.BookDTO;
import clms.system.clms_backend.model.Book;
import clms.system.clms_backend.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    public BookDTO createBook(BookDTO bookDTO) {
        if (bookRepository.findByIsbn(bookDTO.getIsbn()).isPresent()) {
            throw new RuntimeException("Book with ISBN " + bookDTO.getIsbn() + " already exists");
        }

        Book book = new Book();
        book.setIsbn(bookDTO.getIsbn());
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setPublisher(bookDTO.getPublisher());
        book.setPublicationYear(bookDTO.getPublicationYear());
        book.setCategory(bookDTO.getCategory());
        book.setDescription(bookDTO.getDescription());
        book.setTotalCopies(bookDTO.getTotalCopies());
        book.setAvailableCopies(bookDTO.getAvailableCopies());
        book.setStatus(bookDTO.getStatus() != null ? bookDTO.getStatus() : Book.BookStatus.AVAILABLE);

        Book savedBook = bookRepository.save(book);
        return convertToDTO(savedBook);
    }

    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<BookDTO> getBookById(Long id) {
        return bookRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<BookDTO> searchBooks(String keyword) {
        return bookRepository.searchBooks(keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BookDTO> getBooksByCategory(String category) {
        return bookRepository.findByCategory(category).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (bookDTO.getTitle() != null) book.setTitle(bookDTO.getTitle());
        if (bookDTO.getAuthor() != null) book.setAuthor(bookDTO.getAuthor());
        if (bookDTO.getPublisher() != null) book.setPublisher(bookDTO.getPublisher());
        if (bookDTO.getPublicationYear() != null) book.setPublicationYear(bookDTO.getPublicationYear());
        if (bookDTO.getCategory() != null) book.setCategory(bookDTO.getCategory());
        if (bookDTO.getDescription() != null) book.setDescription(bookDTO.getDescription());
        if (bookDTO.getTotalCopies() != null) {
            int diff = bookDTO.getTotalCopies() - book.getTotalCopies();
            book.setTotalCopies(bookDTO.getTotalCopies());
            book.setAvailableCopies(book.getAvailableCopies() + diff);
        }
        if (bookDTO.getStatus() != null) book.setStatus(bookDTO.getStatus());

        Book updatedBook = bookRepository.save(book);
        return convertToDTO(updatedBook);
    }

    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        bookRepository.delete(book);
    }

    private BookDTO convertToDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setIsbn(book.getIsbn());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setPublisher(book.getPublisher());
        dto.setPublicationYear(book.getPublicationYear());
        dto.setCategory(book.getCategory());
        dto.setDescription(book.getDescription());
        dto.setTotalCopies(book.getTotalCopies());
        dto.setAvailableCopies(book.getAvailableCopies());
        dto.setStatus(book.getStatus());
        dto.setCreatedAt(book.getCreatedAt());
        dto.setUpdatedAt(book.getUpdatedAt());
        return dto;
    }
}







