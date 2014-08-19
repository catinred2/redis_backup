/**
 * 
 */
package com.mosun.saveredis;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.ReadOptions;
import org.iq80.leveldb.util.DbIterator;

import com.mosun.saveredis.leveldb.Database;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Description: TODO
 * @author ming
 * @date 2014年8月13日 下午3:58:30
 */
public class ServerHandler extends SimpleChannelInboundHandler<String>{
	private static final Logger logger = Logger.getLogger(ServerHandler.class);
	
	/* (non-Javadoc)
	 * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		logger.debug("--------------------");
		// TODO Auto-generated method stub
		if (msg==null || msg.isEmpty()){
			ctx.channel().writeAndFlush("-ERROR KEY IS NULL OR EMPTY\r\n");
			return;
		}
		String[] args = msg.split(" ");
		String cmd = args[0];
		String response = "";
		switch(cmd.toUpperCase()){
		case "KEYS":
			DBIterator it = Database.getInstance().iterator(new ReadOptions());
			StringBuilder sb = new StringBuilder();
			while(it.hasNext()){
				sb.append(new String(it.next().getKey()));
				sb.append("\r\n");
			}
			response = sb.toString();
			sb.setLength(0);
			break;
		case "GET":
			if (args.length==2){
				String key = args[1];
				String value = Database.getInstance().read(key);
				if (value==null || value.isEmpty()){
					response = "EMPTY KEY OR LIST\r\n";
				}else{
					String valueType= value.substring(0, 1);
					response = "type="+ valueType + " "+ value.substring(1)  + "\r\n";
				}
				
			}else{
				response = "PLEASE PROVIDE THE KEY\r\n";
			}
			
			break;
		case "EXIT":
			ctx.channel().close();
			return;
		default:
			response = "-ERROR COMMAND ERROR\r\n";
			break;
		}
		ctx.channel().writeAndFlush(response);
	}

}
