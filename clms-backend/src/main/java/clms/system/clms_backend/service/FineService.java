package clms.system.clms_backend.service;

import clms.system.clms_backend.dto.FineDTO;
import clms.system.clms_backend.model.Borrowing;
import clms.system.clms_backend.model.Fine;
import clms.system.clms_backend.model.User;
import clms.system.clms_backend.repository.BorrowingRepository;
import clms.system.clms_backend.repository.FineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FineService {
    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private BorrowingRepository borrowingRepository;

    private static final BigDecimal DAILY_FINE_RATE = new BigDecimal("5.00"); // $5 per day

    public void calculateOverdueFine(Borrowing borrowing) {
        if (borrowing.getReturnDate() == null || borrowing.getDueDate() == null) {
            return;
        }

        if (borrowing.getReturnDate().isAfter(borrowing.getDueDate())) {
            long daysOverdue = ChronoUnit.DAYS.between(borrowing.getDueDate(), borrowing.getReturnDate());
            BigDecimal fineAmount = DAILY_FINE_RATE.multiply(new BigDecimal(daysOverdue));

            Fine fine = new Fine();
            fine.setUser(borrowing.getUser());
            fine.setBorrowing(borrowing);
            fine.setAmount(fineAmount);
            fine.setStatus(Fine.FineStatus.PENDING);
            fine.setReason("Overdue return: " + daysOverdue + " days");

            fineRepository.save(fine);
        }
    }

    public FineDTO payFine(Long fineId) {
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new RuntimeException("Fine not found"));

        if (fine.getStatus() != Fine.FineStatus.PENDING) {
            throw new RuntimeException("Fine is not pending");
        }

        fine.setStatus(Fine.FineStatus.PAID);
        fine.setPaidDate(java.time.LocalDateTime.now());
        Fine savedFine = fineRepository.save(fine);

        return convertToDTO(savedFine);
    }

    public FineDTO waiveFine(Long fineId, String reason) {
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new RuntimeException("Fine not found"));

        fine.setStatus(Fine.FineStatus.WAIVED);
        if (reason != null) {
            fine.setReason(fine.getReason() + " - Waived: " + reason);
        }
        Fine savedFine = fineRepository.save(fine);

        return convertToDTO(savedFine);
    }

    public List<FineDTO> getUserFines(Long userId) {
        User user = new User();
        user.setId(userId);
        return fineRepository.findByUser(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FineDTO> getPendingFines(Long userId) {
        User user = new User();
        user.setId(userId);
        return fineRepository.findPendingFinesByUser(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FineDTO> getAllFines() {
        return fineRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BigDecimal getTotalPendingFines(Long userId) {
        User user = new User();
        user.setId(userId);
        Double total = fineRepository.getTotalPendingFinesByUser(user);
        return total != null ? BigDecimal.valueOf(total) : BigDecimal.ZERO;
    }

    private FineDTO convertToDTO(Fine fine) {
        FineDTO dto = new FineDTO();
        dto.setId(fine.getId());
        dto.setUserId(fine.getUser().getId());
        dto.setUsername(fine.getUser().getUsername());
        dto.setBorrowingId(fine.getBorrowing().getId());
        dto.setAmount(fine.getAmount());
        dto.setStatus(fine.getStatus());
        dto.setReason(fine.getReason());
        dto.setPaidDate(fine.getPaidDate());
        dto.setCreatedAt(fine.getCreatedAt());
        return dto;
    }
}







