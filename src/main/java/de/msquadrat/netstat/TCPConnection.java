package de.msquadrat.netstat;

import java.net.ProtocolFamily;

public class TCPConnection extends Connection {
    
    static class Parser extends Connection.Parser {
        
        public Parser(ProtocolFamily protocolFamily, String line) {
            super(protocolFamily, line);
        }

        @Override
        public Connection finish() {
            return new TCPConnection(this);
        }
    }
    
    private TCPConnection(Parser parser) {
        super(parser);
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.TCP;
    }
    
    
}
