package com.manywho.sharepoint.flow.addin.hosted;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.manywho.sdk.client.run.RunClient;
import com.manywho.sharepoint.flow.addin.hosted.client.RunClientProvider;

public class ApplicationBinder extends AbstractModule {
    @Override
    protected void configure() {
        bind(RunClient.class).toProvider(RunClientProvider.class).in(Singleton.class);

    }
}
