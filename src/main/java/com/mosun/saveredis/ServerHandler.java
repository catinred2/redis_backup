/**
 * 
 */
package com.mosun.saveredis;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Description: TODO
 * @author ming
 * @date 2014年8月13日 下午3:58:30
 */
public class ServerHandler extends SimpleChannelInboundHandler<String>{

	/* (non-Javadoc)
	 * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		// TODO Auto-generated method stub
		if (msg==null || msg.isEmpty()){
			return;
		}
		
		ctx.channel().writeAndFlush("1\n");
	}

}
