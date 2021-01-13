package pl.isbrandt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.isbrandt.model.Asset;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
}
