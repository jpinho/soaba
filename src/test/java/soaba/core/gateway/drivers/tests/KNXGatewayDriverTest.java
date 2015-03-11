package soaba.core.gateway.drivers.tests;

import static org.junit.Assert.fail;

import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import soaba.core.api.IDatapoint.ACCESSTYPE;
import soaba.core.api.IDatapoint.DATATYPE;
import soaba.core.exception.GatewayDriverException;
import soaba.core.gateways.drivers.KNXGatewayDriver;
import soaba.core.models.Datapoint;
import soaba.core.models.DatapointValue;
import tuwien.auto.calimero.exception.KNXTimeoutException;

/**
 * SOABA Unit Tests for KNX Gateway Driver featuring Calimero Framework.
 * 
 * @author João Pinho (jpe.pinho@gmail.com)
 * @since 0.5
 */
public class KNXGatewayDriverTest {

    /**
     * Datapoints Meta
     */
    private static final String DATAPOINT_2N14_LUMIHALLSOUTH_SENSOR = "2-N14 - Luminosity - Hall - South Sensor";
    private static final String DATAPOINT_2N14_LUMIHALLSOUTH_SENSOR_READADDR = "0/2/18";
    private static final String DATAPOINT_2N14_LUMIHALLSOUTH_SENSOR_WRITEADDR = "0/2/18";

    private static final String DATAPOINT_2N1402_LIGHTS = "2-N14.02 - Lights";
    private static final String DATAPOINT_2N1402_LIGHTS_READADDR = "0/0/2";
    private static final String DATAPOINT_2N1402_LIGHTS_WRITEADDR = "0/0/2";


    /**
     * Known Gateways at Taguspark
     */

    /* INFO: this gateway can be used for test, as the lab purpose is specifically for testing */
    @SuppressWarnings("unused")
    private static final String KNX_GW_158 = "172.20.70.242";

    /* WARNING: use this gateway only for datapoint readings, there is people working there */
    private static final String KNX_GW_N14 = "172.20.70.241";


    /**
     * Datapoints collection
     */
    private static Datapoint dpN1404Lights;
    private static Datapoint dpN1402Lights;
    private static Logger logger;


    @BeforeClass
    public static void setup() throws UnknownHostException {
        dpN1404Lights = new Datapoint(KNX_GW_N14, DATAPOINT_2N1402_LIGHTS, ACCESSTYPE.READ_WRITE, DATATYPE.BIT,
                DATAPOINT_2N1402_LIGHTS_READADDR, DATAPOINT_2N1402_LIGHTS_WRITEADDR);

        dpN1402Lights = new Datapoint(KNX_GW_N14, DATAPOINT_2N14_LUMIHALLSOUTH_SENSOR, ACCESSTYPE.READ_WRITE,
                DATATYPE.NUMBER, DATAPOINT_2N14_LUMIHALLSOUTH_SENSOR_READADDR,
                DATAPOINT_2N14_LUMIHALLSOUTH_SENSOR_WRITEADDR);

        logger = Logger.getLogger(KNXGatewayDriverTest.class);
    }

    @AfterClass
    public static void teardown() {
        KNXGatewayDriver.dispose();
    }

    /**
     * Test a connection to a KNX gateway through the KNX Gateway Driver.
     * 
     * @throws UnknownHostException if the host cannot be resolved
     * @throws GatewayDriverException if the driver as thrown an error while connecting
     */
    @Test
    public final void testConnect() throws UnknownHostException, GatewayDriverException {
        try {
            KNXGatewayDriver gateway = new KNXGatewayDriver(KNX_GW_N14);

            // testing connection establishment
            gateway.connect();

            // assert that the channel is open
            Assert.assertTrue(gateway.getNetworkLink().isOpen());
        } catch (Exception e) {
            if(e.getCause() instanceof KNXTimeoutException){
                logger.error("Skipping unit test, KNX Gateway connection timeout.");
                return;
            }
            
            logger.error(e);
            fail("Gateway is down or busy.");
        }
    }

    /**
     * Test a connection to a KNX gateway through the KNX Gateway Driver.
     * 
     * @throws UnknownHostException if the host cannot be resolved
     * @throws GatewayDriverException if the driver as thrown an error while disconnecting
     */
    @Test
    public final void testDisconnect() throws GatewayDriverException, UnknownHostException {
        try {
            KNXGatewayDriver gateway = new KNXGatewayDriver(KNX_GW_N14);

            // testing connection establishment
            gateway.connect();

            // assert that the channel is open
            Assert.assertTrue(gateway.getNetworkLink().isOpen());

            // attempting to disconnect the link with the gateway
            gateway.disconnect();

            // assert the connection was closed
            Assert.assertTrue(!gateway.isConnected());
        } catch (Exception e) {
            if(e.getCause() instanceof KNXTimeoutException){
                logger.error("Skipping unit test, KNX Gateway connection timeout.");
                return;
            }
            
            logger.error(e);
            fail("Gateway is down or busy.");
        }
    }

    /**
     * Test datapoint readings to a KNX gateway through the KNX Gateway Driver.
     */
    @Test
    public final void testDatapointReadings() {
        KNXGatewayDriver gateway = null;

        try {
            gateway = new KNXGatewayDriver(KNX_GW_N14);

            // testing connection establishment
            gateway.connect();

            // assert that the channel is open
            Assert.assertTrue(gateway.getNetworkLink().isOpen());
        } catch (Exception e) {
            if(e.getCause() instanceof KNXTimeoutException){
                logger.error("Skipping unit test, KNX Gateway connection timeout.");
                return;
            }
            
            logger.error(e);
            fail("Gateway is down or busy.");
        }

        try {
            logger.info(String.format("testDatapointReadings reading datapoint '%s' at '%s', from GW '%s'.",
                    dpN1402Lights.getName(), dpN1402Lights.getReadAddress(), dpN1402Lights.getGatewayAddress()));

            DatapointValue<?> value1 = DatapointValue.build(dpN1402Lights);
            value1 = gateway.read(dpN1404Lights);

            logger.info(String.format("testDatapointReadings reading datapoint '%s' at '%s', from GW '%s'.",
                    dpN1404Lights.getName(), dpN1404Lights.getReadAddress(), dpN1404Lights.getGatewayAddress()));

            DatapointValue<?> value2 = DatapointValue.build(dpN1404Lights);
            value2 = gateway.read(dpN1404Lights);

            Assert.assertNotNull(value1.getValue());
            Assert.assertNotNull(value2.getValue());

            // attempting to disconnect the link with the gateway
            gateway.disconnect();

            // assert the connection was closed
            Assert.assertTrue(gateway.isConnected() == false);

        } catch (Exception e) {
            if(e.getCause() instanceof KNXTimeoutException){
                logger.error("Skipping unit test, KNX Gateway connection timeout.");
                return;
            }
            
            logger.error(e);
            fail(e.getMessage());
        }
    }

    /**
     * Test datapoint writtings to a KNX gateway through the KNX Gateway Driver.
     */
    @Test
    public final void testWriteDatapoint() {
        KNXGatewayDriver gateway = null;

        try {
            gateway = new KNXGatewayDriver(KNX_GW_N14);

            // testing connection establishment
            gateway.connect();

            // assert that the channel is open
            Assert.assertTrue(gateway.getNetworkLink().isOpen());
        } catch (Exception e) {
            if(e.getCause() instanceof KNXTimeoutException){
                logger.error("Skipping unit test, KNX Gateway connection timeout.");
                return;
            }
            
            logger.error(e);
            fail("Gateway is down or busy.");
        }

        try {
            /**
             * Reading Datapoint Value
             */
            logger.info(String.format("testDatapointReadings reading datapoint '%s' at '%s', from GW '%s'.",
                    dpN1404Lights.getName(), dpN1404Lights.getReadAddress(), dpN1404Lights.getGatewayAddress()));

            DatapointValue<?> readValue = DatapointValue.build(dpN1402Lights);
            readValue = gateway.read(dpN1404Lights);

            logger.info(String.format("testDatapointReadings value read from '%s' is '%s'.", dpN1404Lights.getName(),
                    readValue.getValue()));

            /**
             * Writting Datapoint Value
             */
            logger.info(String.format(
                    "testDatapointReadings writting 'false' to datapoint '%s' at '%s', from GW '%s'.",
                    dpN1404Lights.getName(), dpN1404Lights.getReadAddress(), dpN1404Lights.getGatewayAddress()));

            DatapointValue<?> writeValue = DatapointValue.build(dpN1404Lights);
            writeValue.setValue(false);
            gateway.write(writeValue);

            /**
             * Reading Written Value
             */
            logger.info(String.format("testDatapointReadings reading datapoint '%s' at '%s', from GW '%s'.",
                    dpN1404Lights.getName(), dpN1404Lights.getReadAddress(), dpN1404Lights.getGatewayAddress()));

            DatapointValue<?> readValueWritten = DatapointValue.build(dpN1402Lights);
            readValueWritten = gateway.read(dpN1404Lights);

            logger.info(String.format("testDatapointReadings value read from '%s' is '%s'.", dpN1404Lights.getName(),
                    readValueWritten.getValue()));

            /**
             * Asserting
             */
            Assert.assertNotNull(readValue.getValue());
            Assert.assertTrue(writeValue.getValue().equals(readValueWritten.getValue()));

            /**
             * Restoring Value
             */
            logger.info(String.format(
                    "testDatapointReadings writting restore value to datapoint '%s' at '%s', from GW '%s'.",
                    dpN1404Lights.getName(), dpN1404Lights.getReadAddress(), dpN1404Lights.getGatewayAddress()));

            DatapointValue<?> restoreValue = DatapointValue.build(dpN1404Lights);
            restoreValue.setValue(readValue.getValue());
            gateway.write(restoreValue);

            /**
             * Reading Restored Datapoint Value
             */
            logger.info(String.format(
                    "testDatapointReadings reading (restored value) datapoint '%s' at '%s', from GW '%s'.",
                    dpN1404Lights.getName(), dpN1404Lights.getReadAddress(), dpN1404Lights.getGatewayAddress()));

            DatapointValue<?> readRestoredValue = DatapointValue.build(dpN1402Lights);
            readRestoredValue = gateway.read(dpN1404Lights);

            logger.info(String.format("testDatapointReadings restored value read from '%s' is '%s'.",
                    dpN1404Lights.getName(), readRestoredValue.getValue()));

            /**
             * Asserting
             */
            Assert.assertNotNull(readRestoredValue.getValue());
            Assert.assertTrue(
                    "Ups!!! The value seems not to have been properly restored, or a concurrent access has changed it!",
                    readValue.getValue().equals(readRestoredValue.getValue()));

            // attempting to disconnect the link with the gateway
            gateway.disconnect();

            // assert the connection was closed
            Assert.assertTrue(gateway.isConnected() == false);

        } catch (Exception e) {
            if(e.getCause() instanceof KNXTimeoutException){
                logger.error("Skipping unit test, KNX Gateway connection timeout.");
                return;
            }
            
            logger.error(e);
            fail(e.getMessage());
        }
    }
}