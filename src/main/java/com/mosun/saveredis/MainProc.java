/**
 * 
 */
package com.mosun.saveredis;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.mosun.saveredis.util.*;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @Description: TODO
 * @author ming
 * @date 2014年8月13日 下午3:10:24
 */
public class MainProc {
	private static final Logger logger = Logger.getLogger(MainProc.class);
	private static EventLoopGroup bossGroup=null;
	private static EventLoopGroup workerGroup=null;
	private static ServerBootstrap bootstrap=null;
	private static int port = 9001;
	private static Socket socket=null;
	private static RedisInputStream ris;
	private static RedisOutputStream ros;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println( "Hello World!" );
		Runtime.getRuntime().addShutdownHook(new Thread(){
    		public void run(){
    			
    			try {
					socket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
    			
    			
    			try {
					KeyQueue.Put(KeyQueue.MAGIC_WORD);
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	});
		//networkInit();
		try {
			socketInit();
			new Thread(new RedisToDb()).start();
			//new Thread(new Monitor(ris)).start();
			new Monitor(ris).run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Connect to Redis Error");
		}
		
	}
	public static void socketInit() throws UnknownHostException, IOException{
		socket = new Socket(RedisConfig.getHost(),RedisConfig.getPort());
		ris = new RedisInputStream(socket.getInputStream());
		ros = new RedisOutputStream(socket.getOutputStream());
		ros.writeAsciiCrLf("config set notify-keyspace-events EA");
		ros.flush();
		String ok = ris.readLine();
		System.out.println(ok);
		ros.writeAsciiCrLf("psubscribe __keyevent@0__:*");
		ros.flush();
		
		//socket.close();
	}
	public static void networkInit(){
		bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        try {
            bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .option(ChannelOption.SO_BACKLOG, 100)
             .option(ChannelOption.TCP_NODELAY, true)//Nagle算法，不开启
             .option(ChannelOption.SO_KEEPALIVE, true)
             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new ServerInitializer());

            // Start the server.
            ChannelFuture f = bootstrap.bind(port).sync();
            System.out.println("starting the server on port:" + port);
            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        }catch (Exception e) {
			e.printStackTrace();
		}finally {
            // Shut down all event loops to terminate all threads.
			logger.debug("Ready to shutdown gracefully");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
	}
}
