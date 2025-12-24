package clms.system.clms_backend.repository;

import clms.system.clms_backend.model.EResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EResourceRepository extends JpaRepository<EResource, Long> {
    List<EResource> findByTitleContainingIgnoreCase(String keyword);

    List<EResource> findByFormat(EResource.ResourceFormat format);
}
