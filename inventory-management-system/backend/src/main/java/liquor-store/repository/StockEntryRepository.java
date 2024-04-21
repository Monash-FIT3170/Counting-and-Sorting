// backend/src/main/java/repository/StockEntryRepository.java

package repository;

import model.StockEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockEntryRepository extends JpaRepository<StockEntry, Integer> {
  // Don't need anything here. Can define custom methods
}
