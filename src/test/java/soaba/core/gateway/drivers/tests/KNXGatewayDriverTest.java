package soaba.core.gateway.drivers.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.junit.Ignore;
import org.junit.Test;

import soaba.core.api.IDatapoint.DATAPOINT_ACCESSTYPE;
import soaba.core.api.IDatapoint.DATAPOINT_DATATYPE;
import soaba.core.exception.GatewayDriverException;
import soaba.core.gateways.drivers.KNXGatewayDriver;
import soaba.core.models.Datapoint;

public class KNXGatewayDriverTest {

    @SuppressWarnings("unused")
    private static final String KNX_GW_158 = "172.20.70.147";
    private static final String KNX_GW_N14 = "172.20.70.241";

    @Test
    public final void testConnect() throws UnknownHostException, GatewayDriverException {

        try {
            KNXGatewayDriver gateway = new KNXGatewayDriver(Inet4Address.getByName(KNX_GW_N14));

            // testing connection establishment
            gateway.connect();

            // assert that the channel is open
            assertTrue(gateway.getNetworkLink().isOpen());
        } catch (Exception e) {
            LogManager.getLogger(this).error(e);
            fail("Gateway is down or busy.");
        }
    }

    @Test
    public final void testDisconnect() throws GatewayDriverException, UnknownHostException {
        try {
            KNXGatewayDriver gateway = new KNXGatewayDriver(Inet4Address.getByName(KNX_GW_N14));

            // testing connection establishment
            gateway.connect();

            // assert that the channel is open
            assertTrue(gateway.getNetworkLink().isOpen());

            // attempting to disconnect the link with the gateway
            gateway.disconnect();

            // assert the connection was closed
            assertTrue(!gateway.getNetworkLink().isOpen());
        } catch (Exception e) {
            LogManager.getLogger(this).error(e);
            fail("Gateway is down or busy.");
        }
    }

    @Test
    public final void testDatapointReadings() {
        KNXGatewayDriver gateway = null;
        
        try {
            gateway = new KNXGatewayDriver(Inet4Address.getByName(KNX_GW_N14));
            
            // testing connection establishment
            gateway.connect();

            // assert that the channel is open
            assertTrue(gateway.getNetworkLink().isOpen());
        } catch (Exception e) {
            LogManager.getLogger(this).error(e);
            fail("Gateway is down or busy.");
        }
        
        try{
            Datapoint dpN1404Lights = new Datapoint("2-N14.02 - Lights",
                    DATAPOINT_ACCESSTYPE.READ_WRITE, DATAPOINT_DATATYPE.BIT, "0/0/2", "0/0/2");
            
            Datapoint dpN1402Lights = new Datapoint("2-N14 - Luminosity - Hall - South Sensor",
                    DATAPOINT_ACCESSTYPE.READ_WRITE, DATAPOINT_DATATYPE.NUMBER, "0/2/18", "0/2/18");

            boolean reading1 = (boolean)gateway.readBool(dpN1404Lights);
            boolean reading2 = (boolean)gateway.readBool(dpN1404Lights);
            assertTrue(reading1 == reading2);
            
            float reading3 = (float)gateway.read2ByteFloat(dpN1402Lights);
            float reading4 = (float)gateway.read2ByteFloat(dpN1402Lights);
            assertTrue(reading3 == reading4);
            
            // attempting to disconnect the link with the gateway
            gateway.disconnect();

            // assert the connection was closed
            assertTrue(!gateway.getNetworkLink().isOpen());
        } catch (Exception e) {
            LogManager.getLogger(this).error(e);
            fail(e.getMessage());
        }
    }

    @Ignore
    @Test
    public final void testWriteDatapoint() {
        fail("Not yet implemented");
    }
}
