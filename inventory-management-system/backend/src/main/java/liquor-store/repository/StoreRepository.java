// backend/src/main/java/repository/StoreRepository.java

package repository;

import model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Integer> {
    // Don't need anything here. Can define custom methods
}
