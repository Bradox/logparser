package es.tresw.logparser.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import es.tresw.logparser.dto.Parameters;
import es.tresw.logparser.model.BlockedIP;
import es.tresw.logparser.model.LogEntry;
import es.tresw.logparser.repository.BlockedIPRepository;
import es.tresw.logparser.repository.LogEntryRepository;

/**
 * Class that orchestrates all the operations to read the file, parse the file 
 * into Objects, stores them and finding the blocked ip's.
 * 
 * @author aalves
 *
 */
@Service
@DependsOn({ "springApplicationArguments" })
public class LogService {

	@Autowired
	private LogEntryRepository logEntryRepository;

	@Autowired
	private BlockedIPRepository blockedIPRepository;

	private Parameters params;

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

	private static final char PIPE = '|';

	@Autowired
	public LogService(ApplicationArguments springApplicationArguments) {
		params = new Parameters(springApplicationArguments);
	}

	/**
	 * This method parses the log file getting all the log entries
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public List<LogEntry> parseFile() throws FileNotFoundException, IOException {
		List<LogEntry> logs = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(params.getAccessLog()), "UTF-8"));
				CSVParser csvFileParser = new CSVParser(br, CSVFormat.DEFAULT.withDelimiter(PIPE))) {

			// Get a list of CSV file records
			List<CSVRecord> records = csvFileParser.getRecords();

			// Read all the records since there's no header don't need to skip any
			for (CSVRecord record : records) {
				LogEntry logEntry = new LogEntry();
				String dateString = record.get(0);
				dateString = dateString.replace("\uFEFF", ""); // remove UTF BOM
				LocalDateTime date = LocalDateTime.parse(dateString, formatter);
				logEntry.setStartDate(date);
				logEntry.setIp(record.get(1));
				logEntry.setRequest(record.get(2));
				logEntry.setStatus(201);
				logEntry.setUserAgent(record.get(4));
				logs.add(logEntry);
			}

		}
		return logs;
	}
	
	/**
	 * Makes a bulk of all the log entries
	 * @param entries entries to persist
	 */
	public void saveLogs(List<LogEntry> entries) {
		logEntryRepository.saveAll(entries);
	}

	/**
	 * This method queries gets a list of ips that exceed the threshold between the start date and
	 * the end date. The two first are passed by the user as arguments, the third one is calculated using
	 * the duration argument
	 * @return list of ips
	 */
	public List<String> findIPs() {
		return logEntryRepository.findIPsByAccountAndCreatedBefore(params.getStartDate(), params.getEndDate(),
				params.getThreshold());
	}

	/**
	 * This method takes a list of ips and generating blockip objects.
	 * @param ips list of ips
	 * @return list of blockedip objects
	 */
	public List<BlockedIP> getBlockedIps(List<String> ips) {
		List<BlockedIP> blockedIPs = new ArrayList<>();
		ips.forEach(ip -> {
			BlockedIP bIp = new BlockedIP();
			bIp.setIp(ip);
			bIp.setComment("IP: " + ip + " has reached the threshold (" + params.getThreshold() + ") between "
					+ params.getStartDate().toString() + " and " + params.getEndDate().toString());
			blockedIPs.add(bIp);
		});
		return blockedIPs;
	}

	/**
	 * Makes a bulk of all the blocked ips
	 * @param blockedIPs entries to persist
	 */
	public void saveBlockedIps(List<BlockedIP> blockedIPs) {
		blockedIPRepository.saveAll(blockedIPs);
	}
	
	/**
	 * This method returns whether or not the user has set the --help argument
	 * @return true if the user asked for help, false otherwise
	 */
	public boolean showHelp() {
		return params.isHelp();
	}

	/**
	 * This method returns whether or not the user has passed all the require arguments to the application
	 * @return true if the user passed all the arguments, false otherwise
	 */
	public boolean requiredPresent() {
		return params.checkRequiredArguments();
	}

	/**
	 * This method deletes all the database records
	 */
	/**
	 * TODO: It is not clear on the task definition how to handle errors, if that
	 * should be treated incrementally. For now I am assuming that the data is not
	 * incremental and we only care about data in one file, and wether the app
	 * finishes correctly or encounters an error the database is deleted.
	 */
	public void deleteDatabase() {
		blockedIPRepository.deleteAll();
		logEntryRepository.deleteAll();
	}

	/**
	 * This method is for testing purposes, mocking the ApplicationArguments and updating
	 * them allows us to test all the different scenarios 
	 * @param args applicaciont arguments entered by the user
	 */
	protected void setParameters(ApplicationArguments args) {
		this.params = new Parameters(args);
	}
}
