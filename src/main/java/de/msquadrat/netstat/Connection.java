package de.msquadrat.netstat;

import java.net.InetAddress;
import java.net.ProtocolFamily;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

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
        protected Queue<String> fields;
        
        protected ProtocolFamily protocolFamily;
        protected InetAddress localAddress;
        protected int localPort;
        protected InetAddress remoteAddress;
        protected int remotePort;
        protected ConnectionState state;
        
        protected Parser(String line) {
            fields = new LinkedList<>(Arrays.asList(line.trim().split("\\s+")));
            assert fields.size() >= 4 : "Malformed line '" + line + "'";
            String field;
            Pair<InetAddress, Integer> endpoint;
            
            // Drop first field aka internal index
            field = fields.poll();
            assert field.matches("^\\p{Digit}+:$") : "Malformed field sl '" + field + "'";
            
            // Parse local_address
            field = fields.poll();
            assert field.matches("^\\p{XDigit}{8,32}:\\p{XDigit}{4}$") : "Malformed field local_address '" + field + "'";
            endpoint = parseEndpoint(field);
            this.localAddress = endpoint.getLeft();
            this.localPort = endpoint.getRight();
            
            // Parse rem_address
            field = fields.poll();
            assert field.matches("^\\p{XDigit}{8,32}:\\p{XDigit}{4}$") : "Malformed field rem_address '" + field + "'";
            endpoint = parseEndpoint(field);
            this.remoteAddress = endpoint.getLeft();
            this.remotePort = endpoint.getRight();
            
            // Parse st
            field = fields.poll();
            assert field.matches("^\\p{XDigit}{2}$") : "Malformed field st '" + field + "'";
            this.state = parseState(field);
        }
        
        protected static byte parseHexByte(String input) {
            assert input.length() == 2 : "Malformed byte input '" + input + "'";
            return (byte)Short.parseShort(input, 16);
        }
        
        protected static short parseHexByteToShort(String input) {
            assert input.length() == 2 : "Malformed byte input '" + input + "'";
            return (short)Short.parseShort(input, 16);
        }
        
        protected static int parseHexShortToInt(String input) {
            assert input.length() == 4 : "Malformed short input '" + input + "'";
            return Integer.parseInt(input, 16);
        }
        
        protected static int parseHexInt(String input) {
            assert input.length() == 8 : "Malformed int input '" + input + "'";
            return (int)Long.parseLong(input, 16);
        }
        
        private static Pair<InetAddress, Integer> parseEndpoint(String input) {
            String[] parts = input.split(":");
            assert parts.length == 2 : "Malformed endpoint '" + input + "'";
            
            int port;
            try {
                port = parseHexShortToInt(parts[1]);
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Unexpected malformed port '" + parts[1] + "'", e);
            }
            return new ImmutablePair<>(parseAddress(parts[0]), port);
        }
        
        
        
        private static InetAddress parseAddress(String input) {
            try {
                /* InetAddress.getByAddress() expects data in network byte
                 * order (Big Endian).  The proc interface either uses local
                 * byte order or Little Endian.  IPv6 addresses are represented
                 * as 4 32 Bit words in the same order as IPv4 addresses.
                 * TODO: Check which byte order the proc interface uses
                 */
                assert ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN : "Tested on Little Endian only ('" + input + "')";
                ByteBuffer addr = ByteBuffer.allocate(input.length() / 2).order(ByteOrder.nativeOrder());
                
                for (int offset = 0; offset < input.length(); offset += 4 * 2) {
                    addr.putInt(parseHexInt(input.substring(offset, offset + 4 * 2)));
                }
                
                return InetAddress.getByAddress(addr.array());
            }
            catch (UnknownHostException | NumberFormatException e) {
                throw new IllegalArgumentException("Unexpected malformed address '" + input + "'", e);
            }
        }
        
        private static ConnectionState parseState(String input) {
            try {
                return ConnectionState.values()[parseHexByteToShort(input)];
            }
            catch (IndexOutOfBoundsException | NumberFormatException e) {
                throw new IllegalArgumentException("Unexpected malformed state '" + input + "'", e);
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
    
    public boolean isListening() {
        return getState() == ConnectionState.LISTEN;
    }
    
    @Override
    public String toString() {
        Formatter f = new Formatter(Locale.US);
        f.format("[%s]:%d [%s]:%d (%s)",
                getLocalAddress().getHostAddress(),
                getLocalPort(),
                getRemoteAddress().getHostAddress(),
                getRemotePort(),
                getState());
        String s = f.out().toString();
        f.close();
        return s;
    }
}
