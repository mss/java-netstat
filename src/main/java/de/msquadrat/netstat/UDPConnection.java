package de.msquadrat.netstat;

import java.net.ProtocolFamily;

public class UDPConnection extends Connection {
    
    static class Parser extends Connection.Parser {
        
        public Parser(ProtocolFamily protocolFamily, String line) {
            super(protocolFamily, line);
        }
        
        @Override
        public Connection finish() {
            return new UDPConnection(this);
        }
    }
    
    private UDPConnection(Parser parser) {
        super(parser);
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.UDP;
    }
    
    @Override
    public boolean isListening() {
        /* The kernel (currently?) encodes listening UDP sockets as 
         * CLOSEd.
         */
        switch (getState()) {
        case LISTEN:
        case CLOSE:
            return true;
        default:
            return false;
        }
    }

}
