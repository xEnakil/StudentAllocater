package com.university.StudentDropper.repository;

import com.university.StudentDropper.model.Destination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, Integer> {

    Optional<Destination> findById(Long destinationId);
}
