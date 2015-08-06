package com.gemini.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.ReferenceCountUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemini.utils.CSVParser;

public class RequestHandler extends ChannelInboundHandlerAdapter{

	private static Logger l = LoggerFactory.getLogger(RequestHandler.class);
	
	@Override
	public void channelRead(final ChannelHandlerContext ctx, Object msg){
		try{
			l.debug("Request received from client: \n{}", msg.toString() );
			
			if (msg instanceof HttpRequest) {

				l.debug("Building HTTP response");
				FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(CSVParser.getData().toString().getBytes()));
				response.headers().set("content-type", "application/json");
				response.headers().set("content-length", response.content().readableBytes());

				ctx.write(response);
				
			} else if (msg instanceof LastHttpContent)
				l.debug("HTTP Request completed");
				
		} catch(Exception e) {
			l.error("Exception occured: {}", new Object[]{e.getStackTrace()});
		} finally {
			l.debug("Releasing request buffer object");
			ReferenceCountUtil.release(msg);
		}
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx){
		ctx.flush();
	}
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		l.error("Error occured: {}", new Object[] {cause.getStackTrace()});
		cause.printStackTrace();
        ctx.close();
    }
}
