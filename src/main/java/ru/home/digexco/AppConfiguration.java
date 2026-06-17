package ru.home.digexco;

import com.example.grpc.SequenceServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

    @Bean
    public Channel myServiceChannel() {
        return ManagedChannelBuilder
                .forAddress("localhost", 9090)
                .usePlaintext()
                .build();
    }

    @Bean
    public SequenceServiceGrpc.SequenceServiceStub myServiceStub(Channel myServiceChannel) {
        return SequenceServiceGrpc.newStub(myServiceChannel);
    }
}
