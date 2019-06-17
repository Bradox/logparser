package es.tresw.logparser;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import es.tresw.logparser.model.BlockedIP;
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
	
    private static ApplicationArguments applicationArguments;

	public static void main(String... args) throws Exception {
		applicationArguments = new DefaultApplicationArguments(args);
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
				logService.parseFile();
				List<String> ips = logService.findIPs();
				List<BlockedIP> blockedIPs = logService.getAndSaveBlockedIps(ips);
				blockedIPs.forEach(ip -> System.out.println(ip.getComment()));
			} else {
				// Wrong parameters
				System.out.println("Wrong parameters the right usage is:");
				printHelp();
			}
		} catch (IllegalArgumentException e) {
			System.out.println("Wrong parameters the right usage is:");
			printHelp();
		} catch (IOException e) {
			System.out.println("Problem reading file log");
			printHelp();
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
		System.out.println("******************");
		System.out.println("Usage: java -jar parser-1.0.jar --accesslog=[PATH_TO_LOG]\\access.log"
				+ "  --startDate=2017-01-01.13:00:00 --duration={hourly|daily} --threshold=200");
		System.out.println("******************");
	}
	
	 @Bean
	  public LogService logService() {
	    try {
	      return new LogService(applicationArguments);
	    } catch (IllegalArgumentException e) {
	    	//will be managed later
	    }
	    return new LogService();
	  }
}