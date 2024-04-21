// backend/src/main/java/repository/SupplierRepository.java

package repository;

import model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Integer> {
  // Don't need anything here. Can define custom methods
}
