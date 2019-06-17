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
import org.springframework.stereotype.Service;

import es.tresw.logparser.dto.Parameters;
import es.tresw.logparser.model.BlockedIP;
import es.tresw.logparser.model.LogEntry;
import es.tresw.logparser.repository.BlockedIPRepository;
import es.tresw.logparser.repository.LogEntryRepository;

@Service
public class LogService {

	@Autowired
	private LogEntryRepository logEntryRepository;

	@Autowired
	private BlockedIPRepository blockedIPRepository;

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

	private static final char PIPE = '|';

	public void parseFile(String file) throws FileNotFoundException, IOException {
		List<LogEntry> log = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
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

	public List<BlockedIP> find(Parameters params) {

		List<LogEntry> entries = logEntryRepository.findByAccountAndCreatedBefore(params.getStartDate(),
				params.getEndDate(), params.getThreshold());
		return saveBlockedIps(params, entries);
	}

	public List<BlockedIP> saveBlockedIps(Parameters params, List<LogEntry> logEntries) {
		List<BlockedIP> blockedIPs = new ArrayList<>();
		logEntries.forEach(l -> {
			BlockedIP ip = new BlockedIP();
			ip.setIp(l.getIp());
			ip.setComment("The IP: " + l.getIp() + " has reached the maximun number of request ("
					+ params.getThreshold() + ") between " + params.getStartDate().toString() + " and "
					+ params.getEndDate().toString());
			blockedIPs.add(ip);
		});
		blockedIPRepository.saveAll(blockedIPs);
		return blockedIPs;
	}

}
