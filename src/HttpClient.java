/**
 * Created by AKSHAY on 5/4/2017.
 */
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import org.codehaus.jackson.map.ObjectMapper;

import java.net.URI;
import java.util.Scanner;

/**
 * A simple HTTP client that prints out the content of the HTTP response to
 * {@link System#out} to test {@link HttpServer}.
 */
@SuppressWarnings("deprecation")
public final class HttpClient {

    static final String URL = System.getProperty("url", "http://127.0.0.1:8080/");

    public static void main(String[] args) throws Exception {
        Person person = new Person();
        init(person);
        URI uri = new URI(URL);

        String scheme = uri.getScheme() == null? "http" : uri.getScheme();
        String host = uri.getHost() == null? "127.0.0.1" : uri.getHost();
        int port = uri.getPort();
        if (port == -1) {
            if ("http".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("https".equalsIgnoreCase(scheme)) {
                port = 443;
            }
        }

        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            System.err.println("Only HTTP(S) is supported.");
            return;
        }

        // Configure SSL context if necessary.
        final boolean ssl = "https".equalsIgnoreCase(scheme);


        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new HttpClientInitializer());

            // Make the connection attempt.
            Channel ch = b.connect(host, port).sync().channel();

            // Prepare the HTTP request.
            HttpRequest  request = new DefaultFullHttpRequest(
                    HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getRawPath()) {
            };
            request.headers().set(HttpHeaders.Names.HOST, host);
            request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
            request.headers().set(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP);
            ObjectMapper mapper = new ObjectMapper();
            String jsonInString = mapper.writeValueAsString(person);

            request.headers().set("person",jsonInString);


            // Set some example cookies.
            request.headers().set(
                    HttpHeaders.Names.COOKIE,
                    ClientCookieEncoder.encode(
                            new DefaultCookie("my-cookie", "foo"),
                            new DefaultCookie("another-cookie", "bar")));



            // Send the HTTP request.
            ch.writeAndFlush(request);

            // Wait for the server to close the connection.
            ch.closeFuture().sync();
        } finally {
            // Shut down executor threads to exit.
            group.shutdownGracefully();
        }
    }


    private static void init(Person person){

            Scanner in = new Scanner(System.in);

            System.out.println("\nEnter details about the following\n");
            System.out.println("Enter Name\t : ");
            person.setName(in.nextLine());
            System.out.println("Enter Address\t:");
            person.setAddress(in.nextLine());
            System.out.println("Enter Email\t:");
            person.setEmail(in.nextLine());
            System.out.println("Enter Mobile number\t");
            person.setPhone_number(in.nextLong());
             System.out.println("Enter Age\t:");
             person.setAge(in.nextLine());
             System.out.println("Added Successfully");



        }
    }


