package ru.gaidamaka.dns;

import lombok.Data;
import ru.gaidamaka.ClientHandler;

@Data
public class DNSResolveRequest {
    private final String hostToResolve;
    private final ClientHandler clientHandler;
}
