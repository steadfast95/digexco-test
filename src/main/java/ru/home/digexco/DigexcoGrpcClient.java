package ru.home.digexco;

import com.example.grpc.Digexco;
import com.example.grpc.SequenceServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class DigexcoGrpcClient {
    private static final Logger logger = LogManager.getLogger(DigexcoGrpcClient.class);

    private static final int maxIterableValue = 50;

    private final AtomicInteger atomicInteger = new AtomicInteger(0);
    private final long ONE_SECOND = 1000L;
    private final SequenceServiceGrpc.SequenceServiceStub async;
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    public DigexcoGrpcClient(SequenceServiceGrpc.SequenceServiceStub async) {
        this.async = async;
    }

    @Async
    void calling() {
        int p0 = 0;
        int p1 = 30;
        logger.info("Starting gRPC call with numbers: {} and {}", p0, p1);

        StreamObserver<Digexco.ServerResponse> responseObserver = new StreamObserver<Digexco.ServerResponse>() {
            @Override
            public void onNext(Digexco.ServerResponse response) {
                atomicInteger.set(response.getNumber());
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

        executorService.submit(()->{
            try {
                for (int i = 0; i < maxIterableValue; i++) {
                    logger.info("some current value = {}", i + atomicInteger.getAndAdd(1));
                    TimeUnit.MILLISECONDS.sleep(ONE_SECOND);
                }
            } catch (InterruptedException e) {
                logger.error(e);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                logger.error(e);
            }
        });

        async
                .withDeadlineAfter(120, TimeUnit.SECONDS) // Таймаут 120 с
                .getSeq(
                        Digexco.ClientRequest.newBuilder()
                                .setNumber(p0)
                                .setNumber2(p1)
                                .build(),
                        responseObserver
                );
    }
}
