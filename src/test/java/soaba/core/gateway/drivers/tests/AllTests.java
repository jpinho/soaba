package soaba.core.gateway.drivers.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * SOABA test suite for all Gateway Drivers.
 * 
 * @author João Pinho (jpe.pinho@gmail.com)
 */
@RunWith(Suite.class)
@SuiteClasses({ KNXGatewayDriverTest.class })
public class AllTests {
    
    @BeforeClass
    public static void suiteInit(){
    }
    
    @AfterClass
    public static void suiteTearDown(){
    }
}
