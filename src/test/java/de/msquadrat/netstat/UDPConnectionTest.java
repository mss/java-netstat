package de.msquadrat.netstat;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

import java.util.EnumMap;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

public class UDPConnectionTest {
    @Rule
    public ErrorCollector collector = new ErrorCollector();

    @Test
    public void testGetConnectionType() {
        UDPConnection c = mock(UDPConnection.class, CALLS_REAL_METHODS);
        
        assertThat(c.getConnectionType(), is(equalTo(ConnectionType.UDP)));
    }

    @Test
    public void testIsListening() {
        EnumMap<ConnectionState, Boolean> expected = new EnumMap<>(ConnectionState.class);
        expected.put(ConnectionState.UNKNOWN, false);
        expected.put(ConnectionState.ESTABLISHED, false);
        expected.put(ConnectionState.SYN_SENT, false);
        expected.put(ConnectionState.SYN_RECV, false);
        expected.put(ConnectionState.FIN_WAIT1, false);
        expected.put(ConnectionState.FIN_WAIT2, false);
        expected.put(ConnectionState.TIME_WAIT, false);
        expected.put(ConnectionState.CLOSE, true);
        expected.put(ConnectionState.CLOSE_WAIT, false);
        expected.put(ConnectionState.LAST_ACK, false);
        expected.put(ConnectionState.LISTEN, true);
        expected.put(ConnectionState.CLOSING, false);
        assertThat(expected.size(), is(equalTo(ConnectionState.values().length)));
        
        for (ConnectionState s : ConnectionState.values()) {
            UDPConnection c = mock(UDPConnection.class, CALLS_REAL_METHODS);
            when(c.getState()).thenReturn(s);
            collector.checkThat(s.toString(), c.isListening(), is(equalTo(expected.get(s))));
        }
    }

}
