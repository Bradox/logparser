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

@SpringBootApplication
public class LogParserApplication implements ApplicationRunner {

	@Autowired
	private LogService logService;

	public static void main(String... args) throws Exception {
		SpringApplication.run(LogParserApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) {
		Parameters params = null;
		try {
			params = new Parameters(args);

			if (params.isHelp()) {
				printHelp();
			} else if (params.requiredPresent()) {
				logService.parseFile(params.getAccessLog());
				List<BlockedIP> blockedIPs = logService.find(params);
				blockedIPs.forEach(ip -> System.out.println(ip.getComment()));
			} else {
				System.out.println("Wrong parameters the right usage is:");
				printHelp();
			}
		} catch (IllegalArgumentException e) {
			System.out.println("Wrong parameters the right usage is:");
			printHelp();
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Problem reading file " + params.getAccessLog());
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