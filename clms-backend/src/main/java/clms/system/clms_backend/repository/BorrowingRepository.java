package clms.system.clms_backend.repository;

import clms.system.clms_backend.model.Borrowing;
import clms.system.clms_backend.model.User;
import clms.system.clms_backend.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {
    List<Borrowing> findByUser(User user);

    List<Borrowing> findByBook(Book book);

    @Query("SELECT b FROM Borrowing b WHERE b.user = :user AND b.status = 'ACTIVE'")
    List<Borrowing> findActiveBorrowingsByUser(@Param("user") User user);

    @Query("SELECT b FROM Borrowing b WHERE b.book = :book AND b.status = 'ACTIVE'")
    Optional<Borrowing> findActiveBorrowingByBook(@Param("book") Book book);

    @Query("SELECT b FROM Borrowing b WHERE b.dueDate < :date AND b.status = 'ACTIVE'")
    List<Borrowing> findOverdueBorrowings(@Param("date") LocalDate date);

    @Query("SELECT b.book.id FROM Borrowing b GROUP BY b.book.id ORDER BY COUNT(b) DESC")
    List<Long> findTopBorrowedBookIds();
}
