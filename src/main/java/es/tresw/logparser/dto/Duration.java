package es.tresw.logparser.dto;

import java.util.Arrays;

/**
 * Enum holding the possible duration options HOURLY, DAILY and INVALID
 * 
 * @author aalves
 *
 */
public enum Duration {

	/**
	 * TODO: The invalid value was added due to the fact that ApplicationArguments could not be autowired
	 * something that has to bee investigated and in conjuction with the fact that throwing an exception
	 * if argument did not match HOURLY and DAILY a BeanInitialization exception was thrown, forcing us 
	 * to override the bean definition. To keep the code clean and error prone free an invalid option was 
	 * introduce while the issue is fixed.
	 */
	HOURLY("HOURLY"), DAILY("DAILY"), INVALID("INVALID");

	private String label;

	Duration(String label) {
		this.label = label;
	}

	Duration() {
	}

	public static Duration findByLabel(String byLabel) {
		/**
		 * TODO: This is not the fastes way to look for the enum, since it is called only once and
		 * there are only three options for now it is ok. If this changes we might need to reconsider
		 * use a static map indexed by display name
		 */
		return Arrays.stream(Duration.values()).filter(d -> d.label.equalsIgnoreCase(byLabel.trim())).findFirst()
				.orElse(INVALID);

	}

}