package es.tresw.logparser.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.tresw.logparser.model.LogEntry;

/**
 * Log entry repository, exposes the default JpaRepository methods and a custom query for 
 * this application
 * @author aalves
 *
 */
@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {

	/**
	 * Returns a list of ips that exceed the threshold between two dates
	 * @param startDate Date from which the search starts
	 * @param endDate Date from which the search ends
	 * @param threshold integer with the maximum threeshold
	 * @return a list of ips that meet the criteria
	 */
	@Query("SELECT l.ip FROM LogEntry l WHERE l.startDate BETWEEN :startDate AND :endDate GROUP BY l.ip HAVING count(l.ip) >= :threshold")
	public List<String> findIPsByAccountAndCreatedBefore(@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate, @Param("threshold") long threshold);
}