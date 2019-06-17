package es.tresw.logparser;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import es.tresw.logparser.model.BlockedIP;
import es.tresw.logparser.model.LogEntry;
import es.tresw.logparser.service.LogService;

/**
 * Application entry point
 * 
 * @author aalves
 *
 */
@SpringBootApplication
public class LogParserApplication implements ApplicationRunner {

	@Autowired
	private LogService logService;

	// private static ApplicationArguments applicationArguments;

	public static void main(String... args) throws Exception {
		// applicationArguments = new DefaultApplicationArguments(args);
		SpringApplication.run(LogParserApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) {
		try {
			// User requested help
			if (logService.showHelp()) {
				printHelp();
			} else if (logService.requiredPresent()) {
				// All required parameters are present
				List<LogEntry> logEntries = logService.parseFile();
				System.out.println(StringUtils.join("Found ", logEntries.size(), " log entires"));
				// write log entries to db
				System.out.println(StringUtils.join("About to bulk save ", logEntries.size()," log entries, this might take a while"));
				logService.saveLogs(logEntries);
				List<String> ips = logService.findIPs();
				System.out.println(StringUtils.join("Found ", ips.size()," blocked ips"));
				List<BlockedIP> blockedIPs = logService.getBlockedIps(ips);
				System.out.println(StringUtils.join("About to bulk save ", blockedIPs.size()," blocked ips, this might take a while"));
				logService.saveBlockedIps(blockedIPs);
				System.out.println("******************");
				System.out.println("The blocked ips are:");
				blockedIPs.forEach(ip -> System.out.println(ip.getComment()));
				System.out.println("******************");
			} else {
				// Wrong parameters
				System.out.println("******************");
				System.out.println("Wrong parameters the right usage is:");
				printHelp();
				System.out.println("******************");
			}
		} catch (IOException e) {
			System.out.println("******************");
			System.out.println("Problem reading file log, make sure that the file exists and it is accessible, right usage:");
			printHelp();
			System.out.println("******************");
		} catch (IllegalArgumentException e) {
			System.out.println("******************");
			System.out.println("Wrong parameters the right usage is:");
			printHelp();
			System.out.println("******************");
		} finally {
			/**
			 * TODO: It is not clear on the task definition how to handle errors, if that
			 * should be treated incrementally. For now I am assuming that the data is not
			 * incremental and we only care about data in one file, and wether the app
			 * finishes correctly or encounters an error the database is deleted.
			 */
			logService.deleteDatabase();
		}
	}

	private void printHelp() {
		System.out.println("Usage: java -jar parser-1.0.jar --accesslog=[PATH_TO_LOG]\\access.log"
				+ "  --startDate=2017-01-01.13:00:00 --duration={hourly|daily} --threshold=200");
	}
}