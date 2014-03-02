package de.msquadrat.netstat;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.ProtocolFamily;
import java.net.StandardProtocolFamily;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Connections implements Iterable<Connection> {
    private static final int INITIAL_CAPACITY = 16;
    private static final String PROC_NET_PATH = "/proc/net";
    
    private final Set<Connection> connections;
    
    public Connections() {
        this(ConnectionType.TCP);
    }
    
    public Connections(ConnectionType type) {
        this(null, type);
    }
    
    public Connections(ProtocolFamily protocolFamily) {
        this(protocolFamily, ConnectionType.TCP);
    }
    
    public Connections(ProtocolFamily protocolFamily, ConnectionType type) {
        connections = Collections.unmodifiableSet(load(protocolFamily, type));
    }
    
    private Set<Connection> load(ProtocolFamily protocolFamily, ConnectionType type) {
        Set<Connection> result = new HashSet<Connection>(INITIAL_CAPACITY);
        
        if (protocolFamily == null) {
            result.addAll(load(StandardProtocolFamily.INET, type));
            result.addAll(load(StandardProtocolFamily.INET6, type));
            return result;
        }
        else if (protocolFamily != StandardProtocolFamily.INET && protocolFamily != StandardProtocolFamily.INET6) {
            throw new IllegalArgumentException("protocolFamily must be INET or INET6, not " + protocolFamily.toString());
        }
        
        if (type == null) {
            type = ConnectionType.ANY;
        }
        if (type == ConnectionType.ANY) {
            result.addAll(load(protocolFamily, ConnectionType.TCP));
            result.addAll(load(protocolFamily, ConnectionType.UDP));
            return result;
        }
        
        Path path = Paths.get(PROC_NET_PATH,
                (type == ConnectionType.TCP ? "tcp" : "udp") + (protocolFamily == StandardProtocolFamily.INET6 ? "6" : ""));
        try (BufferedReader in = Files.newBufferedReader(path, StandardCharsets.US_ASCII)) {
            // Throw away first line (column headings)
            String line = in.readLine();
            if (line == null) {
                throw new IOException("Empty file");
            }
            assert line.contains(" sl ") : "Malformed header line '" + line + "'";
            
            // Parse each line
            for (line = in.readLine(); line != null; line = in.readLine()) {
                Connection conn;
                switch (type) {
                case TCP:
                    conn = new TCPConnection.Parser(line).finish();
                    break;
                case UDP:
                    conn = new UDPConnection.Parser(line).finish();
                    break;
                default:
                    throw new IllegalArgumentException(type + " is no a valid source type");
                }
                boolean added = result.add(conn);
                assert added : "Connection " + conn + "found twice";
            }
        }
        catch (Exception | AssertionError e) {
            // Can't happen
            throw new IllegalStateException("Failed to read " + path.toString() + ": " + e.getMessage(), e);
        }

        return result;
    }


    @Override
    public Iterator<Connection> iterator() {
        return connections.iterator();
    }
    
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer(connections.size() * 80);
        for (Connection c : connections) {
            buf.append(c.getConnectionType());
            buf.append(' ');
            buf.append(c.toString());
            buf.append('\n');
        }
        return buf.toString();
    }

}
