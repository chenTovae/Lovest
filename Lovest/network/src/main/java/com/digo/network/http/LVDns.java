package com.digo.network.http;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public interface LVDns {
    /**
     * Returns the IP addresses of {@code hostname}, in the order they will be attempted by
     * OkHttp. If
     * a connection to an address fails, OkHttp will retry the connection with the next address
     * until
     * either a connection is made, the set of IP addresses is exhausted, or a limit is exceeded.
     */
    List<InetAddress> lookup(String hostname) throws UnknownHostException;
}
