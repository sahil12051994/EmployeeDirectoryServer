package com.gemini.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeResponder extends ChannelHandlerAdapter{
	
	private static Logger l = LoggerFactory.getLogger(TimeResponder.class);
	
	public void channelActive(final ChannelHandlerContext ctx){
		final ByteBuf time = ctx.alloc().buffer(4);
		time.writeInt((int) (System.currentTimeMillis()/1000L + 2208988800L));

		l.debug("Writing time: {} on output stream", time.toString());
		final ChannelFuture f = ctx.writeAndFlush(time);
		f.addListener(new ChannelFutureListener() {
			
			public void operationComplete(ChannelFuture future) throws Exception {
				l.debug("Writing completed");
				assert f == future;
				ctx.close();
			}
		});
		
	}

	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
