/**
 * 
 */
package com.mosun.saveredis;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.ReadOptions;
import org.iq80.leveldb.util.DbIterator;

import com.mosun.saveredis.leveldb.Database;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
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
		//logger.debug("--------------------");
		// TODO Auto-generated method stub
		if (msg==null || msg.isEmpty()){
			ctx.channel().writeAndFlush("-ERROR KEY IS NULL OR EMPTY\r\n");
			return;
		}
		String[] args = msg.split(" ");
		String cmd = args[0];
		String response = "";
		switch(cmd.toUpperCase()){
		case "SHUTDOWN":
			KeyQueue.Put(KeyQueue.MAGIC_WORD);
			Thread.sleep(3000);
			System.exit(0);
			break;
		case "SAVE":
			response="begin to backup...";
			KeyQueue.Put(KeyQueue.HOTCOPY);
			break;
		case "KEYS":
			if (args.length!=2){
				response = "usage: keys abc\r\n";
				break;
			}
			String param = args[1];
			if (param==null || param.isEmpty()){
				response = "usage: keys abc\r\n";
				break;
			}
			DBIterator it = MainProc.DATABASE.iterator(new ReadOptions());
			StringBuilder sb = new StringBuilder();
			while(it.hasNext()){
				String key = new String(it.next().getKey());
				key = key.substring(1);
				if (key.equals(param)) {
					sb.append(key);
					sb.append("\r\n");
				}
			}
			response = sb.toString();
			if (response==null || response.isEmpty()){
				response = "No such key\r\n";
			}
			sb.setLength(0);
			break;
		case "GET":
			if (args.length==2){
				String key = args[1];
				String value = MainProc.DATABASE.read(key);
				if (value==null || value.isEmpty()){
					response = "-EMPTY KEY OR LIST\r\n";
				}else{
					String valueType= value.substring(0, 1);
					response = "type="+ valueType + "\r\n"+ value.substring(1)  + "\r\n";
				}
				
			}else{
				response = "-PLEASE PROVIDE THE KEY\r\n";
			}
			
			break;
		case "EXIT":
			ChannelFuture cf = ctx.channel().writeAndFlush("Exit...\r\n");
			cf.addListener(ChannelFutureListener.CLOSE);
			return;
		default:
			response = "-ERROR COMMAND ERROR\r\n";
			break;
		}
		ctx.channel().writeAndFlush(response);
	}

}
