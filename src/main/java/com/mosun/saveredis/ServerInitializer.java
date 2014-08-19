package com.mosun.saveredis;

import java.nio.charset.StandardCharsets;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {
	private static final Logger logger = Logger.getLogger(ServerInitializer.class);
	private static final StringDecoder STRING_DECODER = new StringDecoder(StandardCharsets.UTF_8);
	private static final StringEncoder STRING_ENCODER = new StringEncoder(StandardCharsets.UTF_8);
	@Override
	protected void initChannel(SocketChannel ch)  {
		logger.debug("#############");
		try {
			//MyLengthFieldBasedFrameDecoder lfbFrameDecoder = new MyLengthFieldBasedFrameDecoder(10240, 0, 4);
			ChannelPipeline pipeline = ch.pipeline();
			pipeline.addLast("frameDecoder",new LineBasedFrameDecoder(256, true, true));
			pipeline.addLast("stringDecoder",STRING_DECODER);
			pipeline.addLast("stringEncoder",STRING_ENCODER);
			pipeline.addLast("handler",new ServerHandler());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
