package es.tresw.logparser;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import es.tresw.logparser.dto.ParametersTest;
import es.tresw.logparser.service.LogServiceTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ ParametersTest.class, LogServiceTest.class })
public class TestSuite {

}
