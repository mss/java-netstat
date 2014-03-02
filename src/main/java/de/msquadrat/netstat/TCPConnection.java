package de.msquadrat.netstat;

public class TCPConnection extends Connection {
    
    static class Parser extends Connection.Parser {
        
        public Parser(String line) {
            super(line);
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
