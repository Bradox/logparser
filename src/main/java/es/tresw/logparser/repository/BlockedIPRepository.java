package es.tresw.logparser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.tresw.logparser.model.BlockedIP;
/**
 * Blocked ip repository, just exposes the default JpaRepository methods
 * @author aalves
 *
 */
@Repository
public interface BlockedIPRepository extends JpaRepository<BlockedIP, Long> {
}