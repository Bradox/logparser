package es.tresw.logparser.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.boot.ApplicationArguments;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * This class reads the arguments introduced by the user and parses them
 * 
 * @author aalves
 *
 */
@Data
public class Parameters {
	private long threshold;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private String accessLog;
	private boolean help;
	private ApplicationArguments args;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd.HH:mm:ss");

	public Parameters(ApplicationArguments args) {
		if (args != null) {
			this.args = args;
			if (args.containsOption("help")) {
				this.help = true;
			}
			if (args.containsOption("startDate")) {
				String date = args.getOptionValues("startDate").get(0);
				this.startDate = LocalDateTime.parse(date, formatter);
			}
			if (args.containsOption("duration")) {
				Duration duration = Duration.findByLabel(args.getOptionValues("duration").get(0));
				switch (duration) {
				case HOURLY:
					this.endDate = this.startDate.plusHours(1);
					this.endDate = this.endDate.minusSeconds(1);
					break;
				case DAILY:
					this.endDate = this.startDate.plusDays(1);
					this.endDate = this.endDate.minusSeconds(1);
					break;
				}
			}
			if (args.containsOption("threshold")) {
				this.threshold = Integer.valueOf(args.getOptionValues("threshold").get(0));
			}
			if (args.containsOption("accesslog")) {
				this.accessLog = args.getOptionValues("accesslog").get(0);
			}
		} else {
			this.help = false;
		}
	}

	public boolean requiredPresent() {
		return (this.args != null && this.args.containsOption("accesslog") && this.args.containsOption("startDate")
				&& this.args.containsOption("duration") && this.args.containsOption("threshold"));
	}

}
