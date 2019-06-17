package es.tresw.logparser.dto;

import java.util.Arrays;

/**
 * Enum holding the possible duration options HOURLY and DAILY
 * 
 * @author aalves
 *
 */
public enum Duration {

	HOURLY("HOURLY"), DAILY("DAILY");

	private String label;

	Duration(String label) {
		this.label = label;
	}

	Duration() {
	}

	public static Duration findByLabel(String byLabel) {

		return Arrays.stream(Duration.values()).filter(d -> d.label.equalsIgnoreCase(byLabel.trim())).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Argument " + byLabel + " must be an hourly or daily"));

	}

}