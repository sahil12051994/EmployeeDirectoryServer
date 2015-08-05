package com.gemini.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemini.utils.CSVParser;

public class RequestHandler extends ChannelInboundHandlerAdapter{

	private static Logger l = LoggerFactory.getLogger(RequestHandler.class);
	
	@Override
	public void channelRead(final ChannelHandlerContext ctx, Object msg){
		try{
			l.debug("Request received from client: {}" );
			CSVParser csvs = new CSVParser();
			JSONObject obj = new JSONObject(csvs.run());
			if (msg instanceof HttpRequest) {

				FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(obj.toString().getBytes()));
				response.headers().set("content-type", "application/json");
				response.headers().set("content-length", response.content().readableBytes());

				ctx.write(response).addListener(ChannelFutureListener.CLOSE);
				ctx.write(response);
			}
		} finally {
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
