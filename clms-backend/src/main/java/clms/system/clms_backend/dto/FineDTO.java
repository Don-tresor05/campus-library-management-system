package clms.system.clms_backend.dto;

import clms.system.clms_backend.model.Fine;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FineDTO {
    private Long id;
    private Long userId;
    private String username;
    private Long borrowingId;
    private BigDecimal amount;
    private Fine.FineStatus status;
    private String reason;
    private LocalDateTime paidDate;
    private LocalDateTime createdAt;
}







