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
    
    
    static abstract class Parser {
        protected ProtocolFamily protocolFamily;
        protected InetAddress localAddress;
        protected int localPort;
        protected InetAddress remoteAddress;
        protected int remotePort;
        protected ConnectionState state;
        
        protected Parser(String line) {
            throw new UnsupportedOperationException("Not implemented");
        }
        
        static Connection fromLine(ConnectionType type, String line) {
            switch (type) {
            case TCP:
                return new TCPConnection.Parser(line).finish();
            case UDP:
                return new UDPConnection.Parser(line).finish();
            default:
                throw new IllegalArgumentException(type + " is no a valid source type");
            }
        }
        
        public abstract Connection finish();
    }

    
    protected Connection(Parser parser) {
        this.protocolFamily = parser.protocolFamily;
        this.localAddress = parser.localAddress;
        this.localPort = parser.localPort;
        this.remoteAddress = parser.remoteAddress;
        this.remotePort = parser.remotePort;
        this.state = parser.state;
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
