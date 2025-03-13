package com.rockstock.backend.infrastructure.mutationJournal.repository;

import com.rockstock.backend.entity.stock.MutationJournal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MutationJournalRepository extends JpaRepository<MutationJournal, Long>, JpaSpecificationExecutor<MutationJournal> {
    @Query("SELECT mj FROM MutationJournal mj WHERE mj.relatedJournal.id = :journalId")
    Optional<MutationJournal> findOriginJournalByDestinationId(@Param("journalId") Long journalId);
}