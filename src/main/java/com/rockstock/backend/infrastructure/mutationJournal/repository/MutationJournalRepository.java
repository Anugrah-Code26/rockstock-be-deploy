package com.rockstock.backend.infrastructure.mutationJournal.repository;

import com.rockstock.backend.entity.stock.MutationJournal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MutationJournalRepository extends JpaRepository<MutationJournal, Long>, JpaSpecificationExecutor<MutationJournal> {
}