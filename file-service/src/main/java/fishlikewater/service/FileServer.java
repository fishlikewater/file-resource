package fishlikewater.service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileServer {

    private static boolean SSL = false;
    private static int PORT = 8080;

    private void start() throws Exception {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    //通过NoDelay禁用Nagle,使消息立即发出去，不用等待到一定的数据量才发出去
                    .option(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ServerHandlerInitializer());
            // Start the server.
            ChannelFuture f = bootstrap.bind(PORT).sync();
            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down all event loops to terminate all threads.
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    /**
     * 启动服务器
     *
     * @param args
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            PORT = Integer.parseInt(args[0]);
        }
        if (args.length > 1) {
            SSL = Boolean.valueOf(args[1]);
        }
        try {
            new FileServer().start();
        } catch (Exception e) {
            log.error("don't start", e);
        }

    }
}
