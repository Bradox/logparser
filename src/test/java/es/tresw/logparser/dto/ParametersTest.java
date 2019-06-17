package es.tresw.logparser.dto;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class ParametersTest {

	@MockBean
	private ApplicationArguments args;

	@Test
	public void requiredPresentOK() {

		when(args.containsOption("help")).thenReturn(true);
		when(args.containsOption("startDate")).thenReturn(true);
		when(args.getOptionValues("startDate")).thenReturn(Arrays.asList("2017-01-01.13:00:00"));
		when(args.containsOption("duration")).thenReturn(true);
		when(args.getOptionValues("duration")).thenReturn(Arrays.asList("hourly"));
		when(args.containsOption("threshold")).thenReturn(true);
		when(args.getOptionValues("threshold")).thenReturn(Arrays.asList("100"));
		when(args.containsOption("accesslog")).thenReturn(true);
		when(args.getOptionValues("accesslog")).thenReturn(Arrays.asList("path"));

		Parameters params = new Parameters(args);
		assertTrue(params.requiredPresent());
	}

	@Test
	public void requiredPresentKO() {

		when(args.containsOption("startDate")).thenReturn(false);
		when(args.getOptionValues("startDate")).thenReturn(Arrays.asList("2017-01-01.13:00:00"));
		when(args.getOptionValues("threshold")).thenReturn(Arrays.asList("100"));
		when(args.containsOption("accesslog")).thenReturn(true);
		when(args.getOptionValues("accesslog")).thenReturn(Arrays.asList("path"));

		Parameters params = new Parameters(args);
		assertFalse(params.requiredPresent());
	}

	@Test(expected = IllegalArgumentException.class)
	public void durationWronEnumValue() {

		when(args.containsOption("help")).thenReturn(true);
		when(args.containsOption("startDate")).thenReturn(true);
		when(args.getOptionValues("startDate")).thenReturn(Arrays.asList("2017-01-01.13:00:00"));
		when(args.containsOption("duration")).thenReturn(true);
		when(args.getOptionValues("duration")).thenReturn(Arrays.asList("hourly1"));
		when(args.containsOption("threshold")).thenReturn(true);
		when(args.getOptionValues("threshold")).thenReturn(Arrays.asList("100"));
		when(args.containsOption("accesslog")).thenReturn(true);
		when(args.getOptionValues("accesslog")).thenReturn(Arrays.asList("path"));
		new Parameters(args);
	}

}
