package es.tresw.logparser.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import es.tresw.logparser.dto.Parameters;
import es.tresw.logparser.model.LogEntry;
import es.tresw.logparser.repository.LogEntryRepository;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "classpath:test.properties")
public class LogServiceTest {

	@Autowired
	private LogService logService;

	@Autowired
	private LogEntryRepository logEntryRepository;

	@MockBean
	ApplicationArguments applicationArguments;

	@Before
	public void before() {
		when(applicationArguments.containsOption("startDate")).thenReturn(true);
		when(applicationArguments.containsOption("duration")).thenReturn(true);
		when(applicationArguments.getOptionValues("startDate")).thenReturn(Arrays.asList("2017-01-01.00:00:00"));
		when(applicationArguments.containsOption("startDate")).thenReturn(true);
		when(applicationArguments.getOptionValues("startDate")).thenReturn(Arrays.asList("2017-01-01.00:00:00"));
		when(applicationArguments.containsOption("threshold")).thenReturn(true);
		when(applicationArguments.containsOption("accesslog")).thenReturn(true);
		when(applicationArguments.getOptionValues("accesslog")).thenReturn(Arrays.asList("path"));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
		LogEntry l = new LogEntry(1, "192.168.234.82", "GET / HTTP/1.1", 200,
				"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0",
				LocalDateTime.parse("2017-01-01 00:00:11.763", formatter));
		LogEntry l1 = new LogEntry(1, "192.168.234.82", "GET / HTTP/1.1", 200,
				"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0",
				LocalDateTime.parse("2017-01-01 00:30:11.763", formatter));
		LogEntry l2 = new LogEntry(1, "192.168.234.82", "GET / HTTP/1.1", 200,
				"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0",
				LocalDateTime.parse("2017-01-01 01:00:11.763", formatter));
		LogEntry l3 = new LogEntry(1, "192.168.234.84", "GET / HTTP/1.1", 200,
				"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0",
				LocalDateTime.parse("2017-01-01 00:00:11.763", formatter));
		List<LogEntry> entries = new ArrayList<>();
		entries.add(l);
		entries.add(l1);
		entries.add(l2);
		entries.add(l3);
		logEntryRepository.saveAll(entries);
	}

	@After
	public void after() {
		logEntryRepository.deleteAll();
	}

	@Test
	public void findDaily() {
		when(applicationArguments.getOptionValues("duration")).thenReturn(Arrays.asList("daily"));
		when(applicationArguments.getOptionValues("threshold")).thenReturn(Arrays.asList("1"));
		assertTrue(logService.find(new Parameters(applicationArguments)).size() == 2);
	}

	@Test
	public void findHourly() {
		when(applicationArguments.getOptionValues("duration")).thenReturn(Arrays.asList("hourly"));
		when(applicationArguments.getOptionValues("threshold")).thenReturn(Arrays.asList("2"));
		assertTrue(logService.find(new Parameters(applicationArguments)).size() == 1);
	}
	
	@Test
	public void parseFile() throws FileNotFoundException, IOException {
		when(applicationArguments.getOptionValues("duration")).thenReturn(Arrays.asList("daily"));
		when(applicationArguments.getOptionValues("threshold")).thenReturn(Arrays.asList("5"));
		Path resourceDirectory = Paths.get("src","test","resources");
		String absolutePath = resourceDirectory.toFile().getAbsolutePath();
		logService.parseFile(absolutePath+"/access.log");
		assertTrue(logService.find(new Parameters(applicationArguments)).size() == 2);
	}
}