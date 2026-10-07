package com.poetence.inbox_tracker.mail;

import com.microsoft.aad.msal4j.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

@Component
public class GraphTokenProvider {

    private static final Set<String> SCOPES = Set.of("Mail.Read");
    private static final String NOT_CONFIGURED = "not-configured";

    private final PublicClientApplication app;
    private final boolean configured;

    public GraphTokenProvider(GraphProperties props) throws MalformedURLException {
        this.configured = !NOT_CONFIGURED.equals(props.clientId());
        Path cacheFile = Path.of(props.tokenCacheFile());

        ITokenCacheAccessAspect cache = new ITokenCacheAccessAspect() {
            @Override
            public void beforeCacheAccess(ITokenCacheAccessContext ctx) {
                try {
                    if (Files.exists(cacheFile)) {
                        ctx.tokenCache().deserialize(Files.readString(cacheFile));
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @Override
            public void afterCacheAccess(ITokenCacheAccessContext ctx) {
                if (ctx.hasCacheChanged()) {
                    try {
                        Files.writeString(cacheFile, ctx.tokenCache().serialize());
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
            }
        };

        this.app = PublicClientApplication.builder(props.clientId())
                .authority(props.authority())
                .setTokenCacheAccessAspect(cache)
                .build();
    }

    /** One-time interactive sign-in. Prints a code to enter at a Microsoft URL. */
    public void loginWithDeviceCode() {
        requireConfigured();
        DeviceCodeFlowParameters params = DeviceCodeFlowParameters
                .builder(SCOPES, deviceCode -> System.out.println("\n" + deviceCode.message() + "\n"))
                .build();
        IAuthenticationResult result = app.acquireToken(params).join();
        System.out.println("Signed in as " + result.account().username());
    }

    /** Silent token fetch for scheduled jobs. Refreshes automatically when needed. */
    public String getAccessToken() {
        requireConfigured();
        var accounts = app.getAccounts().join();
        if (accounts.isEmpty()) {
            throw new IllegalStateException("Not signed in. Start the app with --login first.");
        }
        IAccount account = accounts.iterator().next();
        SilentParameters params = SilentParameters.builder(SCOPES, account).build();
        try {
            return app.acquireTokenSilently(params).join().accessToken();
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Invalid authority URL in app.graph.authority", e);
        }
    }

    private void requireConfigured() {
        if (!configured) {
            throw new IllegalStateException(
                    "app.graph.client-id is not set. Run with the 'local' profile or set GRAPH_CLIENT_ID.");
        }
    }
}
