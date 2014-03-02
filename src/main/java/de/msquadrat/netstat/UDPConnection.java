package de.msquadrat.netstat;

public class UDPConnection extends Connection {
    
    class Builder extends Connection.Builder {
        public Builder() {
            this.state = ConnectionState.ESTABLISHED;
        }
        
        @Override
        public Connection build() {
            return new UDPConnection(this);
        }
    }
    
    static UDPConnection fromLine(String line) {
        throw new UnsupportedOperationException();
    }
    
    private UDPConnection(Builder builder) {
        super(builder);
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.UDP;
    }

}
