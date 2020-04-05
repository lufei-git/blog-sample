package jit.wxs.grpc.example1;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import jit.wxs.grpc.common.Constant;
import jit.wxs.grpc.common.UserRpcServiceImpl;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Grpc 服务端
 * @author jitwxs
 * @date 2019年12月20日 1:03
 */
public class Example1Server {
    private static final Logger logger = Logger.getLogger(Example1Server.class.getName());
    private Server server;

    public static void main(String[] args) throws IOException, InterruptedException {
        final Example1Server server = new Example1Server();
        server.start();
        server.blockUntilShutdown();
    }

    private void start() throws IOException {
        server = ServerBuilder.forPort(Constant.RUNNING_PORT)
                .addService(new UserRpcServiceImpl())
                .build()
                .start();
        logger.info("Server started...");

        // 程序停止钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Use stderr here since the logger may have been reset by its JVM shutdown hook.
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            try {
                Example1Server.this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("*** server shut down");
        }));
    }

    /**
     * 停止服务
     */
    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}
