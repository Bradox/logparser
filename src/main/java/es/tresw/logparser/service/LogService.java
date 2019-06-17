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
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import es.tresw.logparser.dto.Parameters;
import es.tresw.logparser.model.BlockedIP;
import es.tresw.logparser.model.LogEntry;
import es.tresw.logparser.repository.BlockedIPRepository;
import es.tresw.logparser.repository.LogEntryRepository;

/**
 * Class that orchestrates all the operations to read the file, parse the file
 * into Objects, stores them
 * 
 * @author aalves
 *
 */
@Service
public class LogService {

	@Autowired
	private LogEntryRepository logEntryRepository;

	@Autowired
	private BlockedIPRepository blockedIPRepository;
	
	private Parameters params;

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

	private static final char PIPE = '|';

	public LogService() {
		params = new Parameters(new DefaultApplicationArguments(new String[0]));
	}
	
	public LogService(ApplicationArguments applicationArguments) {
		params = new Parameters(applicationArguments);
	}

	public void parseFile() throws FileNotFoundException, IOException {
		List<LogEntry> log = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(params.getAccessLog()), "UTF-8"));
				CSVParser csvFileParser = new CSVParser(br, CSVFormat.DEFAULT.withDelimiter(PIPE))) {

			// Get a list of CSV file records
			List<CSVRecord> records = csvFileParser.getRecords();

			// Read the CSV file records starting from the first record since there's no
			// header
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
				log.add(logEntry);
			}
			logEntryRepository.saveAll(log);

		}
	}

	public List<String> findIPs() {
		return logEntryRepository.findIPsByAccountAndCreatedBefore(params.getStartDate(), params.getEndDate(),
				params.getThreshold());
	}

	public List<BlockedIP> getAndSaveBlockedIps(List<String> ips) {
		List<BlockedIP> blockedIPs = new ArrayList<>();
		ips.forEach(ip -> {
			BlockedIP bIp = new BlockedIP();
			bIp.setIp(ip);
			bIp.setComment("IP: " + ip + " has reached the threshold (" + params.getThreshold() + ") between "
					+ params.getStartDate().toString() + " and " + params.getEndDate().toString());
			blockedIPs.add(bIp);
		});
		blockedIPRepository.saveAll(blockedIPs);
		return blockedIPs;
	}

	public boolean showHelp() {
		return params.isHelp();
	}

	public boolean requiredPresent() {
		return params.requiredPresent();
	}

	public void deleteDatabase() {
		blockedIPRepository.deleteAll();
		logEntryRepository.deleteAll();
	}

	/*
	 * for testing purposes
	 */
	protected void setParameters(ApplicationArguments args) {
		this.params = new Parameters(args);
	}
}
