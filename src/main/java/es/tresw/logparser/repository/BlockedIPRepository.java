package es.tresw.logparser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.tresw.logparser.model.BlockedIP;

@Repository
public interface BlockedIPRepository extends JpaRepository<BlockedIP, Long> {
}