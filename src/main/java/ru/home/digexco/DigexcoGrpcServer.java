package ru.home.digexco;

import com.example.grpc.Digexco;
import com.example.grpc.SequenceServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.grpc.server.service.GrpcService;

import java.util.concurrent.TimeUnit;

@GrpcService
public class DigexcoGrpcServer extends SequenceServiceGrpc.SequenceServiceImplBase {
    private static final Logger logger = LogManager.getLogger(DigexcoGrpcServer.class);

    private final int maxSequence = 50;
    private final long ms = 2000L;

    @Override
    public void getSeq(Digexco.ClientRequest request, StreamObserver<Digexco.ServerResponse> responseObserver) {
        int number = request.getNumber();
        logger.info("get number by grpc {}", number);
        try {
            for (int i = 1; i < maxSequence; i++) {
                int newValue = number + i;
                Digexco.ServerResponse response = Digexco.ServerResponse.newBuilder()
                        .setNumber(newValue)
                        .build();

                responseObserver.onNext(response);
                logger.info("send to client new number {}", newValue);
                TimeUnit.MILLISECONDS.sleep(ms);
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

}
