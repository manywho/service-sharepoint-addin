package com.manywho.sharepoint.flow.addin.hosted.client;

import com.google.inject.Provider;
import com.manywho.sdk.api.jackson.ObjectMapperFactory;
import com.manywho.sdk.client.run.RunClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RunClientProvider implements Provider<RunClient> {
    @Override
    public RunClient get() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create(ObjectMapperFactory.create()))
                .baseUrl("https://flow.manywho.com")
                .build();

        return retrofit.create(RunClient.class);
    }
}
