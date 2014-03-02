package de.msquadrat.netstat;

public class UDPConnection extends Connection {
    
    static class Parser extends Connection.Parser {
        public Parser(String line) {
            super(line);
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
        switch (state) {
        case LISTEN:
        case CLOSE:
            return true;
        default:
            return false;
        }
    }

}
