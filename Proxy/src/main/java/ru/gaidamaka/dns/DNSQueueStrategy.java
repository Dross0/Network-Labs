package ru.gaidamaka.dns;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.BlockingQueue;

public interface DNSQueueStrategy {
    void appendResolveRequest(@NotNull BlockingQueue<DNSResolveRequest> queue, @NotNull DNSResolveRequest dnsResolveRequest);

    @NotNull
    DNSResolveRequest takeResolveRequest(@NotNull BlockingQueue<DNSResolveRequest> queue);
}
