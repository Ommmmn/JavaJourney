package com.petymate.repository;

import com.petymate.entity.TrainingPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TrainingPackageRepository extends JpaRepository<TrainingPackage, Long> {
    List<TrainingPackage> findByTrainerIdAndIsActiveTrue(Long trainerId);
}
