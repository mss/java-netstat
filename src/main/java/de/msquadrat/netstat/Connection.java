package de.msquadrat.netstat;

import java.net.InetAddress;
import java.net.ProtocolFamily;

/**
 * @see <a href="https://www.kernel.org/doc/Documentation/networking/proc_net_tcp.txt">proc_net_tcp.txt</a>
 *
 */
public abstract class Connection {
    protected final ProtocolFamily protocolFamily;
    protected final InetAddress localAddress;
    protected final int localPort;
    protected final InetAddress remoteAddress;
    protected final int remotePort;
    protected final ConnectionState state;
    
    
    abstract class Builder {
        protected ProtocolFamily protocolFamily;
        protected InetAddress localAddress;
        protected int localPort;
        protected InetAddress remoteAddress;
        protected int remotePort;
        protected ConnectionState state;
        
        public Builder protocolFamily(ProtocolFamily protocolFamily) {
            this.protocolFamily = protocolFamily;
            return this;
        }
        
        public Builder localAddress(InetAddress localAddress) {
            this.localAddress = localAddress;
            return this;
        }
        
        public Builder localPort(int localPort) {
            this.localPort = localPort;
            return this;
        }
        
        public Builder remoteAddress(InetAddress remoteAddress) {
            this.remoteAddress = remoteAddress;
            return this;
        }
        
        public Builder remotePort(int remotePort) {
            this.remotePort = remotePort;
            return this;
        }
        
        public abstract Connection build();
    }
    
    static Connection fromLine(ConnectionType type, String line) {
        switch (type) {
        case TCP:
            return TCPConnection.fromLine(line);
        case UDP:
            return UDPConnection.fromLine(line);
        default:
            throw new IllegalArgumentException(type + " is no a valid source type");
        }
    }

    
    protected Connection(Builder builder) {
        this.protocolFamily = builder.protocolFamily;
        this.localAddress = builder.localAddress;
        this.localPort = builder.localPort;
        this.remoteAddress = builder.remoteAddress;
        this.remotePort = builder.remotePort;
        this.state = builder.state;
    }
    
   
    
    public ProtocolFamily getProtocolFamily() {
        return protocolFamily;
    }
    
    public abstract ConnectionType getConnectionType(); 
    
    public InetAddress getLocalAddress() {
        return localAddress;
    }
    
    public int getLocalPort() {
        return localPort;
    }
    
    public InetAddress getRemoteAddress() {
        return remoteAddress;
    }
    
    public int getRemotePort() {
        return remotePort;
    }
    
    public ConnectionState getState() {
        return state;
    }
}
