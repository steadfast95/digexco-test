package ru.home.digexco;

import ch.qos.logback.core.testUtil.RandomUtil;
import com.example.grpc.Digexco;
import com.example.grpc.SequenceServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

@Service
public class DigexcoGrpcClient {
    private static final Logger logger = LogManager.getLogger(DigexcoGrpcClient.class);

    private final SequenceServiceGrpc.SequenceServiceStub async;

    public DigexcoGrpcClient(SequenceServiceGrpc.SequenceServiceStub async) {
        this.async = async;
    }

    void calling() {
        int positiveInt = RandomUtil.getPositiveInt();
        logger.info("Starting gRPC call with number: {}", positiveInt);

        StreamObserver<Digexco.ServerResponse> responseObserver = new StreamObserver<Digexco.ServerResponse>() {
            @Override
            public void onNext(Digexco.ServerResponse response) {
                logger.info("Received number from server: {}", response.getNumber());
            }

            @Override
            public void onError(Throwable t) {
                logger.error("gRPC stream error: ", t);
            }

            @Override
            public void onCompleted() {
                logger.info("gRPC stream completed successfully");
            }
        };

        async
                .withDeadlineAfter(120, TimeUnit.SECONDS) // Таймаут 120 с
                .getSeq(
                        Digexco.ClientRequest.newBuilder()
                                .setNumber(positiveInt)
                                .build(),
                        responseObserver
                );
    }
}
