package clms.system.clms_backend.controller;

import clms.system.clms_backend.dto.FineDTO;
import clms.system.clms_backend.service.FineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fines")
@CrossOrigin(origins = "http://localhost:5173")
public class FineController {
    @Autowired
    private FineService fineService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FineDTO>> getUserFines(@PathVariable Long userId) {
        return ResponseEntity.ok(fineService.getUserFines(userId));
    }

    @GetMapping("/user/{userId}/pending")
    public ResponseEntity<List<FineDTO>> getPendingFines(@PathVariable Long userId) {
        return ResponseEntity.ok(fineService.getPendingFines(userId));
    }

    @GetMapping("/user/{userId}/total")
    public ResponseEntity<Map<String, BigDecimal>> getTotalPendingFines(@PathVariable Long userId) {
        BigDecimal total = fineService.getTotalPendingFines(userId);
        return ResponseEntity.ok(Map.of("total", total));
    }

    @GetMapping("/all")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<List<FineDTO>> getAllFines() {
        return ResponseEntity.ok(fineService.getAllFines());
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<FineDTO> payFine(@PathVariable Long id) {
        try {
            FineDTO fine = fineService.payFine(id);
            return ResponseEntity.ok(fine);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/waive")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<FineDTO> waiveFine(@PathVariable Long id, @RequestParam(required = false) String reason) {
        try {
            FineDTO fine = fineService.waiveFine(id, reason);
            return ResponseEntity.ok(fine);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
