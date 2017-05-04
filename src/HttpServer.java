/**
 * Created by AKSHAY on 5/4/2017.
 */


import com.couchbase.client.deps.io.netty.handler.ssl.SslContext;
import com.couchbase.client.deps.io.netty.handler.ssl.SslContextBuilder;
import com.couchbase.client.deps.io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;


    /**
     * An HTTP server that sends back the content of the received HTTP request
     * in a pretty plaintext form.
     */
    public final class HttpServer {

       private static final boolean SSL = System.getProperty("ssl") != null;
       private static final int PORT = Integer.parseInt(System.getProperty("port", SSL? "8443" : "8080"));

        public static void main(String[] args) throws Exception {
            // Configure SSL.
            final SslContext sslCtx;
            if (SSL) {
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            } else {
                sslCtx = null;
            }

            // Configure the server.
            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .handler(new LoggingHandler(LogLevel.INFO))
                        .childHandler(new HttpServerInitializer());

                Channel ch = b.bind(PORT).sync().channel();

                System.err.println("Open your web browser and navigate to " +
                        (SSL? "https" : "http") + "://127.0.0.1:" + PORT + '/');

                ch.closeFuture().sync();
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        }
    }

