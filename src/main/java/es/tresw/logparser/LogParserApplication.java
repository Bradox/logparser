package es.tresw.logparser;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import es.tresw.logparser.dto.Parameters;
import es.tresw.logparser.model.BlockedIP;
import es.tresw.logparser.service.LogService;

/**
 * Application entry point
 * @author aalves
 *
 */
@SpringBootApplication
public class LogParserApplication implements ApplicationRunner {

	@Autowired
	private LogService logService;

	public static void main(String... args) throws Exception {
		SpringApplication.run(LogParserApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) {
		try {
			//User requested help
			if (logService.showHelp()) {
				printHelp();
			} else if (logService.requiredPresent()) {
				//All required parameters are present
				logService.parseFile();
				List<BlockedIP> blockedIPs = logService.find();
				blockedIPs.forEach(ip -> System.out.println(ip.getComment()));
			} else {
				//Wrong parameters
				System.out.println("Wrong parameters the right usage is:");
				printHelp();
			}
		} catch (IllegalArgumentException e) {
			System.out.println("Wrong parameters the right usage is:");
			printHelp();
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Problem reading file log");
			e.printStackTrace();
		}
	}

	private void printHelp() {
		System.out.println("******************");
		System.out.println("Usage: java -jar target/log_parser-0.0.1-SNAPSHOT.jar --accesslog=access.log"
				+ "  --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=200");
		System.out.println("******************");
	}
}