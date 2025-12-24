package clms.system.clms_backend.repository;

import clms.system.clms_backend.model.Fine;
import clms.system.clms_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FineRepository extends JpaRepository<Fine, Long> {
    List<Fine> findByUser(User user);
    
    @Query("SELECT f FROM Fine f WHERE f.user = :user AND f.status = 'PENDING'")
    List<Fine> findPendingFinesByUser(@Param("user") User user);
    
    @Query("SELECT SUM(f.amount) FROM Fine f WHERE f.user = :user AND f.status = 'PENDING'")
    Double getTotalPendingFinesByUser(@Param("user") User user);
}







