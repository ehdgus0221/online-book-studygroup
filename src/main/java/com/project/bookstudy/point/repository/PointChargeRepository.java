package com.project.bookstudy.point.repository;

import com.project.bookstudy.point.domain.PointCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PointChargeRepository extends JpaRepository<PointCharge, Long> {
    Optional<PointCharge> findByTransactionId(String transactionId);

    @Query("select p From PointCharge p join fetch p.member where p.id = :id")
    Optional<PointCharge> findByIdFetchMember(Long id);

    Optional<PointCharge> findByTempKey(String tempKey);
}
