package de.msquadrat.netstat;

public class TCPConnection extends Connection {
    class Builder extends Connection.Builder {
        
        public Builder state(ConnectionState state) {
            this.state = state;
            return this;
        }

        @Override
        public Connection build() {
            return new TCPConnection(this);
        }
    }
    
    static TCPConnection fromLine(String line) {
        throw new UnsupportedOperationException();
    }
    
    private TCPConnection(Builder builder) {
        super(builder);
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.TCP;
    }
    
    
}
