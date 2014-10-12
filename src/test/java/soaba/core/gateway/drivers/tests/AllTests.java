package soaba.core.gateway.drivers.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ KNXGatewayDriverTest.class })
public class AllTests {
    
    @BeforeClass
    public static void suiteInit(){
        // TODO        
    }
    
    @AfterClass
    public static void suiteTearDown(){
        // TODO
    }
}
