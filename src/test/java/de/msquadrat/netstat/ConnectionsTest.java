package de.msquadrat.netstat;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

import java.net.StandardProtocolFamily;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import de.msquadrat.netstat.test.Invoker;

public class ConnectionsTest {
    @Rule
    public ErrorCollector collector = new ErrorCollector();

    private String getProcNetFile(Connections c, StandardProtocolFamily f, ConnectionType t) {
        return (String) Invoker.invokeMethodOn(c, "getProcNetFile", new Class<?>[]{f.getClass(), t.getClass()}, f, t);
    }
    
    @Test
    public void testGetProcNetFile() {
        final Map<Pair<StandardProtocolFamily, ConnectionType>, String> expected = new HashMap<>();
        expected.put(Pair.of(StandardProtocolFamily.INET, ConnectionType.TCP), "tcp");
        expected.put(Pair.of(StandardProtocolFamily.INET6, ConnectionType.TCP), "tcp6");
        expected.put(Pair.of(StandardProtocolFamily.INET, ConnectionType.UDP), "udp");
        expected.put(Pair.of(StandardProtocolFamily.INET6, ConnectionType.UDP), "udp6");
        
        for (Pair<StandardProtocolFamily, ConnectionType> args : expected.keySet()) {
            StandardProtocolFamily f = args.getLeft();
            ConnectionType t = args.getRight();
            Connections c = new Connections(f, t);
            String result = getProcNetFile(c, f, t);
            collector.checkThat(t.toString() + "/" + f.toString(), result, is(equalTo(expected.get(args))));
        }
    }

}
