package soaba.core.gateways.drivers;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;

import soaba.core.api.IGatewayDriver;
import soaba.core.exception.DatapointInvalidValueTypeException;
import soaba.core.exception.GatewayDriverException;
import soaba.core.models.Datapoint;
import tuwien.auto.calimero.CloseEvent;
import tuwien.auto.calimero.DetachEvent;
import tuwien.auto.calimero.FrameEvent;
import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.link.KNXNetworkLink;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.NetworkLinkListener;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.process.ProcessCommunicationBase;
import tuwien.auto.calimero.process.ProcessCommunicator;
import tuwien.auto.calimero.process.ProcessCommunicatorImpl;
import tuwien.auto.calimero.process.ProcessEvent;
import tuwien.auto.calimero.process.ProcessListener;

public class KNXGatewayDriver
        implements IGatewayDriver {

    private static final String BROADCAST_ADDRESS = "0.0.0.0";
    private KNXNetworkLink knxLink;
    private ProcessCommunicator pc;
    private boolean isConnected = false;
    private boolean isReconnecting = false;
    private InetAddress address;

    public KNXGatewayDriver(InetAddress gatewayAddress) {
        this.address = gatewayAddress;
    }

    /* (non-Javadoc)
     * @see soaba.core.gateways.drivers.IGatewayDriver2#connect()
     */
    @Override
    public void connect() throws GatewayDriverException, UnknownHostException {
        try {
            InetSocketAddress localIP = new InetSocketAddress(
                    InetAddress.getByName(BROADCAST_ADDRESS), 0);
            InetSocketAddress remoteIP = new InetSocketAddress(this.address, 3671);

            knxLink = new KNXNetworkLinkIP(KNXNetworkLinkIP.TUNNELING, localIP, remoteIP, true,
                    TPSettings.TP1);

            knxLink.addLinkListener(new NetworkLinkListener() {
                public void linkClosed(CloseEvent arg0) {
                    reconnectGateway();
                }

                public void indication(FrameEvent arg0) {
                    /* nothing to be done here */
                }

                public void confirmation(FrameEvent arg0) {
                    /* nothing to be done here */
                }
            });

            (pc = new ProcessCommunicatorImpl(knxLink)).addProcessListener(new ProcessListener() {
                public void groupWrite(ProcessEvent arg0) {
                    /* nothing to be done here */
                }

                public void detached(DetachEvent arg0) {
                    /* nothing to be done here */
                }
            });
        } catch (KNXException e) {
            isConnected = false;
            throw new GatewayDriverException(e);
        } catch (InterruptedException e) {
            isConnected = false;
            throw new GatewayDriverException(e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                try {
                    disconnect();
                } catch (GatewayDriverException e) {
                    LogManager.getLogger(this).info(e.getMessage());
                }
            }
        }));
    }

    /* (non-Javadoc)
     * @see soaba.core.gateways.drivers.IGatewayDriver2#disconnect()
     */
    @Override
    public void disconnect() throws GatewayDriverException {
        // we don't need the process communicator anymore, detach it from the
        // link
        if (pc != null)
            pc.detach();

        // close the KNX link
        if (knxLink != null)
            knxLink.close();

        this.isConnected = false;
    }

    /* (non-Javadoc)
     * @see soaba.core.gateways.drivers.IGatewayDriver2#reconnectGateway()
     */
    @Override
    public void reconnectGateway() {
        if (isReconnecting)
            return;

        new Thread(new Runnable() {
            public void run() {
                isReconnecting = true;
                int wait = 2000;
                int attempt = 0;

                try {
                    disconnect();
                } catch (GatewayDriverException e) {
                    LogManager.getLogger(this).error(e.getMessage(), e);
                }

                isConnected = false;
                while (!isConnected) {
                    try {
                        Thread.sleep(wait);
                        wait = wait + 100;
                    } catch (InterruptedException e) {
                        LogManager.getLogger(this).error(e);
                    }

                    LogManager.getLogger(this).info(
                            "Reconnecting to KNX Gateway... (attempt " + (++attempt) + ")");

                    try {
                        connect();
                    } catch (GatewayDriverException e) {
                        LogManager.getLogger(this).error(e.getMessage(), e);
                    } catch (UnknownHostException e) {
                        LogManager.getLogger(this).error(e.getMessage(), e);
                    }
                }

                isReconnecting = false;
            }
        }).start();
    }

    /* (non-Javadoc)
     * @see soaba.core.gateways.drivers.IGatewayDriver2#readBool(soaba.core.models.Datapoint)
     */
    @Override
    public boolean readBool(Datapoint datapoint) throws GatewayDriverException,
            DatapointInvalidValueTypeException {

        synchronized (pc) {
            try {
                return pc.readBool(new GroupAddress(datapoint.getReadAddress()));
            } catch (KNXException | InterruptedException e) {
                throw new GatewayDriverException(e);
            }
        }
    }

    /* (non-Javadoc)
     * @see soaba.core.gateways.drivers.IGatewayDriver2#read2ByteFloat(soaba.core.models.Datapoint)
     */
    @Override
    public float read2ByteFloat(Datapoint datapoint) throws GatewayDriverException,
            DatapointInvalidValueTypeException {

        synchronized (pc) {
            try {
                return pc.readFloat(new GroupAddress(datapoint.getReadAddress()));
            } catch (KNXException | InterruptedException e) {
                throw new GatewayDriverException(e);
            }
        }
    }

    /* (non-Javadoc)
     * @see soaba.core.gateways.drivers.IGatewayDriver2#readString(soaba.core.models.Datapoint)
     */
    @Override
    public String readString(Datapoint datapoint) throws GatewayDriverException,
            DatapointInvalidValueTypeException {

        synchronized (pc) {
            try {
                return pc.readString(new GroupAddress(datapoint.getReadAddress()));
            } catch (KNXException | InterruptedException e) {
                throw new GatewayDriverException(e);
            }
        }
    }

    /* (non-Javadoc)
     * @see soaba.core.gateways.drivers.IGatewayDriver2#readPercentage(soaba.core.models.Datapoint)
     */
    @Override
    public float readPercentage(Datapoint datapoint) throws GatewayDriverException,
            DatapointInvalidValueTypeException {

        synchronized (pc) {
            try {
                return pc.readUnsigned(new GroupAddress(datapoint.getReadAddress()),
                        ProcessCommunicationBase.SCALING);
            } catch (KNXException | InterruptedException e) {
                throw new GatewayDriverException(e);
            }
        }
    }

    /* (non-Javadoc)
     * @see soaba.core.gateways.drivers.IGatewayDriver2#writeBool(soaba.core.models.Datapoint, boolean)
     */
    @Override
    public void writeBool(Datapoint datapoint, boolean value) throws DatapointInvalidValueTypeException,
            GatewayDriverException {
        try {
            pc.write(new GroupAddress(datapoint.getReadAddress()), value);
        } catch (KNXException e) {
            throw new GatewayDriverException(e);
        }
    }

    /* (non-Javadoc)
     * @see soaba.core.gateways.drivers.IGatewayDriver2#write2ByteFloat(soaba.core.models.Datapoint, float)
     */
    @Override
    public void write2ByteFloat(Datapoint datapoint, float value) throws DatapointInvalidValueTypeException,
            GatewayDriverException {

        synchronized (pc) {
            try {
                pc.write(new GroupAddress(datapoint.getReadAddress()), value);
            } catch (KNXException e) {
                throw new GatewayDriverException(e);
            }
        }
    }

    /* (non-Javadoc)
     * @see soaba.core.gateways.drivers.IGatewayDriver2#writeString(soaba.core.models.Datapoint, java.lang.String)
     */
    @Override
    public void writeString(Datapoint datapoint, String value) throws DatapointInvalidValueTypeException,
            GatewayDriverException {

        synchronized (pc) {
            try {
                pc.write(new GroupAddress(datapoint.getReadAddress()), value);
            } catch (KNXException e) {
                throw new GatewayDriverException(e);
            }
        }
    }

    /* (non-Javadoc)
     * @see soaba.core.gateways.drivers.IGatewayDriver2#writePercentage(soaba.core.models.Datapoint, int)
     */
    @Override
    public void writePercentage(Datapoint datapoint, int value) throws DatapointInvalidValueTypeException,
            GatewayDriverException {

        synchronized (pc) {
            try {
                pc.write(new GroupAddress(datapoint.getReadAddress()), value,
                        ProcessCommunicationBase.SCALING);
            } catch (KNXException e) {
                throw new GatewayDriverException(e);
            }
        }
    }

    /* (non-Javadoc)
     * @see soaba.core.gateways.drivers.IGatewayDriver2#getAddress()
     */
    @Override
    public InetAddress getAddress() {
        return address;
    }

    /* (non-Javadoc)
     * @see soaba.core.gateways.drivers.IGatewayDriver2#setAddress(java.net.InetAddress)
     */
    @Override
    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public ProcessCommunicator getProcessCommunicator() {
        return pc;
    }

    public KNXNetworkLink getNetworkLink() {
        return knxLink;
    }
}
