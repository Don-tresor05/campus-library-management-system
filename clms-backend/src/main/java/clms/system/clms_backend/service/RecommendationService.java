package clms.system.clms_backend.service;

import clms.system.clms_backend.model.Book;
import clms.system.clms_backend.model.Borrowing;
import clms.system.clms_backend.model.User;
import clms.system.clms_backend.repository.BookRepository;
import clms.system.clms_backend.repository.BorrowingRepository;
import clms.system.clms_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final BookRepository bookRepository;
    private final BorrowingRepository borrowingRepository;
    private final UserRepository userRepository;

    public List<Book> getTrendingBooks() {
        List<Long> topBookIds = borrowingRepository.findTopBorrowedBookIds();
        if (topBookIds.isEmpty()) {
            return Collections.emptyList();
        }
        // Limit to top 5
        List<Long> top5 = topBookIds.stream().limit(5).collect(Collectors.toList());
        return bookRepository.findAllById(top5);
    }

    public List<Book> getNewArrivals() {
        return bookRepository.findAll(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"))).getContent();
    }

    public List<Book> getRecommendedForUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Collections.emptyList();
        }

        // 1. Get user's borrowing history
        List<Borrowing> history = borrowingRepository.findByUser(user);

        if (history.isEmpty()) {
            // If no history, return top rated/trending
            return getTrendingBooks();
        }

        // 2. Extract categories from history
        Set<String> categories = history.stream()
                .map(b -> b.getBook().getCategory())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (categories.isEmpty()) {
            return getTrendingBooks();
        }

        // 3. Find other books in these categories
        List<Book> recommendations = new ArrayList<>();
        Set<Long> ownedBookIds = history.stream()
                .map(b -> b.getBook().getId())
                .collect(Collectors.toSet());

        for (String category : categories) {
            List<Book> catBooks = bookRepository.findByCategory(category);
            for (Book book : catBooks) {
                if (!ownedBookIds.contains(book.getId()) && book.getAvailableCopies() > 0) {
                    recommendations.add(book);
                }
            }
        }

        // Shuffle and limit
        Collections.shuffle(recommendations);
        return recommendations.stream().limit(5).collect(Collectors.toList());
    }
}
