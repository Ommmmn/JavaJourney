package com.petymate.repository;

import com.petymate.entity.PetPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PetPhotoRepository extends JpaRepository<PetPhoto, Long> {
    List<PetPhoto> findByPetIdOrderByOrderIndex(Long petId);
    void deleteByPetIdAndId(Long petId, Long photoId);
}
