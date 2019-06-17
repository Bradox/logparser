package es.tresw.logparser.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.tresw.logparser.model.LogEntry;

/**
 * log entry repository, just exposes the default JpaRepository methods
 * 
 * @author aalves
 *
 */
@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {

	@Query("SELECT l.ip FROM LogEntry l WHERE l.startDate BETWEEN :startDate AND :endDate GROUP BY l.ip HAVING count(l.ip) >= :threshold")
	public List<String> findIPsByAccountAndCreatedBefore(@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate, @Param("threshold") long threshold);
}