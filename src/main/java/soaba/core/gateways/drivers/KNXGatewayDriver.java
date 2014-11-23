package soaba.core.gateways.drivers;

import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.activation.UnsupportedDataTypeException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import soaba.core.api.IDatapoint;
import soaba.core.api.IGatewayDriver;
import soaba.core.exception.DatapointInvalidValueTypeException;
import soaba.core.exception.DatapointReadonlyAccessTypeException;
import soaba.core.exception.DatapointWriteonlyAccessTypeException;
import soaba.core.exception.GatewayConnectionLostException;
import soaba.core.exception.GatewayDriverException;
import soaba.core.exception.GatewayMaxConnectionsReachedException;
import soaba.core.models.DatapointValue;
import tuwien.auto.calimero.CloseEvent;
import tuwien.auto.calimero.DetachEvent;
import tuwien.auto.calimero.FrameEvent;
import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.IndividualAddress;
import tuwien.auto.calimero.Priority;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.exception.KNXRemoteException;
import tuwien.auto.calimero.exception.KNXTimeoutException;
import tuwien.auto.calimero.link.KNXLinkClosedException;
import tuwien.auto.calimero.link.KNXNetworkLink;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.NetworkLinkListener;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.mgmt.ManagementProcedures;
import tuwien.auto.calimero.mgmt.ManagementProceduresImpl;
import tuwien.auto.calimero.process.ProcessCommunicationBase;
import tuwien.auto.calimero.process.ProcessCommunicator;
import tuwien.auto.calimero.process.ProcessCommunicatorImpl;
import tuwien.auto.calimero.process.ProcessEvent;
import tuwien.auto.calimero.process.ProcessListener;

public class KNXGatewayDriver
        implements IGatewayDriver, Serializable {

    private static final long serialVersionUID = -6111450316766903633L;

    /**
     * KNX Process Communicator request timeout in seconds.
     */
    private static final int PROCESS_COMM_TIMEOUT = 60;
    private static final int BROADCAST_PORT = 0;
    private static final int DEFAULT_GATEWAY_PORT = 3671;
    private static final String BROADCAST_ADDRESS = "0.0.0.0";
    private static final Map<String, KNXNetworkLink> connectionPool = new TreeMap<String, KNXNetworkLink>();

    private KNXNetworkLink knxLink;
    private ProcessCommunicator pc;
    private ProcessListener processListener;
    private boolean isConnected = false;
    private boolean isReconnecting = false;
    private String description;
    private String address;
    private final static Logger logger = LogManager.getLogger(KNXGatewayDriver.class);

    public KNXGatewayDriver() {
    }

    public KNXGatewayDriver(String gatewayAddress) {
        this.address = gatewayAddress;
    }

    public KNXGatewayDriver(String description, String gatewayAddress) {
        this.description = description;
        this.address = gatewayAddress;
    }

    @Override
    public void connect() throws GatewayDriverException, UnknownHostException {
        try {
            KNXNetworkLink link = null;

            if (connectionPool.containsKey(this.address) && connectionPool.get(this.address).isOpen()) {
                link = connectionPool.get(this.address);
                logger.info("KNXGatewayDriver connection to underlying gateway fetched from pool.");
            } else {
                logger.info("KNXGatewayDriver establishing new connection to the underlying gateway.");
                InetSocketAddress localIP = new InetSocketAddress(InetAddress.getByName(BROADCAST_ADDRESS),
                        BROADCAST_PORT);
                InetSocketAddress remoteIP = new InetSocketAddress(Inet4Address.getByName(this.address),
                        DEFAULT_GATEWAY_PORT);

                link = new KNXNetworkLinkIP(KNXNetworkLinkIP.TUNNELING, localIP, remoteIP, true, TPSettings.TP1);
                logger.info("KNXGatewayDriver new connection established and added to pool.");
                connectionPool.put(this.address, link);
                attachGatewayReconnectToLink(link);
            }

            pc = new ProcessCommunicatorImpl(link);
            pc.setResponseTimeout(PROCESS_COMM_TIMEOUT);
            pc.setPriority(Priority.SYSTEM);
            attachProcessListener(pc);

            knxLink = link;
            isConnected = link.isOpen();
        } catch (KNXRemoteException e) {
            isConnected = false;
            if (e.getMessage().toLowerCase().contains("could not accept new connection (maximum reached)"))
                throw new GatewayMaxConnectionsReachedException(e);
            else
                throw new GatewayDriverException(e);
        } catch (KNXException e) {
            isConnected = false;
            throw new GatewayDriverException(e);
        } catch (InterruptedException e) {
            isConnected = false;
            throw new GatewayDriverException(e);
        }
    }

    private void attachProcessListener(ProcessCommunicator pcomm) {
        processListener = new ProcessListener() {
            public void groupWrite(ProcessEvent evt) {
                String eventType = "UnknownEvent";

                switch (evt.getServiceCode()) {
                    case 0:
                        eventType = "GroupRead";
                        break;
                    case 64:
                        eventType = "GroupResponse";
                        break;
                    case 128:
                        eventType = "GroupWrite";
                        break;
                }

                logger.info(String.format("Event: '%s' from '%s' >> to >> '%s'.", eventType,
                        evt.getSource().toString(), evt.getDestination().toString()));
            }

            public void detached(DetachEvent evt) {
                logger.info(String.format("Event: DetachedListener from '%s'.", evt.getSource().toString()));
            }
        };
        pcomm.addProcessListener(processListener);
    }

    private void attachGatewayReconnectToLink(KNXNetworkLink link) {
        link.addLinkListener(new NetworkLinkListener() {
            public void linkClosed(CloseEvent evt) {
                reconnectGateway();
            }

            public void indication(FrameEvent evt) {
                logger.info(String.format("Event: MessageReceivedIndication from '%s'.", evt.getSource().toString()));
            }

            public void confirmation(FrameEvent evt) {
                logger.info(String.format("Event: RequestConfirmation from '%s'.", evt.getSource().toString()));
            }
        });
    }

    @Override
    public void disconnect() throws GatewayDriverException {
        try {
            // closes KNX link process communicator
            if (pc != null) {
                pc.removeProcessListener(this.processListener);
                pc.detach();
                logger.info("KNXGatewayDriver process comunication detached.");
                pc = null;
            }
        } finally {
            this.isConnected = false;
        }
    }

    @Override
    public void reconnectGateway() {
        if (this.isReconnecting)
            return;

        new Thread(new Runnable() {
            public void run() {
                isReconnecting = true;
                int wait = 2000;
                int attempt = 0;

                try {
                    disconnect();
                } catch (GatewayDriverException e) {
                    logger.warn(e.getMessage(), e);
                }

                isConnected = false;

                while (!isConnected) {
                    try {
                        Thread.sleep(wait);

                        // back-off wait period
                        wait = wait + 100;
                    } catch (InterruptedException e) {
                        logger.warn(e);
                    }

                    logger.info("KNXGatewayDriver, reconnecting to gateway... (attempt " + (++attempt) + ")");

                    try {
                        connect();
                    } catch (GatewayDriverException e) {
                        logger.warn(e.getMessage(), e);
                    } catch (UnknownHostException e) {
                        logger.warn(e.getMessage(), e);
                    }
                }

                isReconnecting = false;
            }
        }).start();
    }

    @Override
    public DatapointValue<?> read(IDatapoint datapoint) throws GatewayDriverException,
            DatapointInvalidValueTypeException,
            DatapointWriteonlyAccessTypeException,
            DatapointReadonlyAccessTypeException,
            UnsupportedDataTypeException {

        if (isReconnecting)
            throw new GatewayConnectionLostException("Gateway Address: " + this.address);

        DatapointValue<?> value = DatapointValue.build(datapoint);
        logger.info(String.format("KNXGatewayDriver#read('%s')", value.getDatapoint().getReadAddress()));

        synchronized (pc) {
            try {
                switch (datapoint.getDataType()) {
                    case BIT:
                        value.setValue(pc.readBool(new GroupAddress(datapoint.getReadAddress())));
                        break;
                    case TINY_NUMBER:
                        value.setValue(pc.readFloat(new GroupAddress(datapoint.getReadAddress()), false));
                        break;
                    case NUMBER:
                        value.setValue(pc.readFloat(new GroupAddress(datapoint.getReadAddress()), true));
                        break;
                    case PERCENTAGE:
                        value.setValue(pc.readUnsigned(new GroupAddress(datapoint.getReadAddress()),
                                ProcessCommunicationBase.SCALING));
                        break;
                    case TEXT:
                        value.setValue(pc.readString(new GroupAddress(datapoint.getReadAddress())));
                        break;
                    default:
                        throw new GatewayDriverException(
                                "Datapoint data type unknown, please specify a valid data type.");
                }

                return value;
            } catch (KNXException | InterruptedException e) {
                throw new GatewayDriverException(e);
            }
        }
    }

    @Override
    public void write(DatapointValue<?> value) throws GatewayDriverException,
            DatapointInvalidValueTypeException,
            DatapointWriteonlyAccessTypeException,
            DatapointReadonlyAccessTypeException,
            UnsupportedDataTypeException {
        logger.info(String.format("KNXGatewayDriver#write('%s', '%s')", value.getDatapoint().getWriteAddress(), value
                .getValue().toString()));

        if (isReconnecting)
            throw new GatewayConnectionLostException("Gateway Address: " + this.address);

        synchronized (pc) {
            try {
                switch (value.getDatapoint().getDataType()) {
                    case BIT:
                        pc.write(new GroupAddress(value.getDatapoint().getWriteAddress()),
                                (Boolean) (value.getValue() == null ? false : value.getValue()));
                        break;
                    case TINY_NUMBER:
                        pc.write(new GroupAddress(value.getDatapoint().getWriteAddress()),
                                (Float) (value.getValue() == null ? 0 : value.getValue()), false);
                        break;
                    case NUMBER:
                        pc.write(new GroupAddress(value.getDatapoint().getWriteAddress()),
                                (Float) (value.getValue() == null ? 0 : value.getValue()), false);
                        break;
                    case PERCENTAGE:
                        pc.write(new GroupAddress(value.getDatapoint().getWriteAddress()),
                                (Integer) (value.getValue() == null ? 0 : value.getValue()),
                                ProcessCommunicationBase.SCALING);
                        break;
                    case TEXT:
                        pc.write(new GroupAddress(value.getDatapoint().getWriteAddress()),
                                (String) (value.getValue() == null ? "" : value.getValue()));
                        break;
                    default:
                        throw new GatewayDriverException(
                                "Datapoint data type unknown, please specify a valid data type.");
                }
            } catch (KNXException e) {
                throw new GatewayDriverException(e);
            }
        }
    }

    @Override
    public List<String> scanNetworkRouters() throws GatewayDriverException,
            KNXLinkClosedException,
            KNXTimeoutException,
            InterruptedException {
        if (isReconnecting)
            throw new GatewayConnectionLostException("Gateway Address: " + this.address);

        ManagementProcedures man = new ManagementProceduresImpl(this.knxLink);
        IndividualAddress[] networkRouters = man.scanNetworkRouters();
        List<String> result = new ArrayList<String>();

        for (IndividualAddress addr : networkRouters)
            result.add(new String(addr.toByteArray()));

        return result;
    }

    @Override
    public List<String> scanNetworkDevices(int area, int line) throws KNXLinkClosedException,
            KNXTimeoutException,
            InterruptedException,
            GatewayConnectionLostException {
        if (isReconnecting)
            throw new GatewayConnectionLostException("Gateway Address: " + this.address);

        ManagementProcedures man = new ManagementProceduresImpl(this.knxLink);
        IndividualAddress[] networkRouters = man.scanNetworkDevices(area, line);
        List<String> result = new ArrayList<String>();

        for (IndividualAddress addr : networkRouters)
            result.add(new String(addr.toByteArray()));

        return result;
    }

    public boolean isAddressOccupied(String addr) throws InterruptedException,
            KNXFormatException,
            KNXException,
            GatewayConnectionLostException {
        if (isReconnecting)
            throw new GatewayConnectionLostException("Gateway Address: " + this.address);

        ManagementProcedures man = new ManagementProceduresImpl(this.knxLink);
        return man.isAddressOccupied(new IndividualAddress(addr));
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public void setAddress(String address) {
        this.address = address;
    }

    public ProcessCommunicator getProcessCommunicator() {
        return pc;
    }

    public KNXNetworkLink getNetworkLink() {
        return knxLink;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public static long getSerialversionUID() {
        return serialVersionUID;
    }

    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public boolean isReconnecting() {
        return isReconnecting;
    }

    public void setReconnecting(boolean isReconnecting) {
        this.isReconnecting = isReconnecting;
    }

    public ProcessListener getProcessListener() {
        return processListener;
    }

    /**
     * Disposes KNX Gateway Driver allocated resources, such as pooled network links.
     */
    public static void dispose() {
        try {
            for (KNXNetworkLink link : connectionPool.values()) {
                link.close();
            }
        } catch (Exception e) {
            logger.error("Exception occurred while disposing KNX network link.", e);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
