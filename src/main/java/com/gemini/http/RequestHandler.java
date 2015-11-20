package com.gemini.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gemini.dal.DbHelper;

public class RequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	private static Logger l = LoggerFactory.getLogger(RequestHandler.class);
	private static ObjectMapper om = new ObjectMapper();
	private @Autowired DbHelper dbHelper;

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		l.error("Error occured: {}", new Object[] { cause.getStackTrace() });
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req)
			throws Exception {
		List<String> paths = new LinkedList<String>(Arrays.asList(req.getUri()
				.split("/")));
		if (paths.size() > 0 && paths.get(0).isEmpty())
			paths.remove(0);

		String cpath = "";

		if (paths.size() > 0) {
			cpath = paths.get(0);
			paths.remove(0);
		}

		switch (cpath) {
		case "user":
			handleUser(paths, ctx, req);
			break;
		default:
			handleInvalid(paths, ctx, req);
			System.out.println("Invalid path");
			break;

		}
	}

	private void handleInvalid(List<String> paths, ChannelHandlerContext ctx,
			HttpRequest req) {
		ctx.writeAndFlush(
				new DefaultFullHttpResponse(req.getProtocolVersion(),
						HttpResponseStatus.BAD_REQUEST, Unpooled.EMPTY_BUFFER))
				.addListener(ChannelFutureListener.CLOSE);
	}

	public void handleUser(List<String> paths, ChannelHandlerContext ctx,
			FullHttpRequest req) {
		try {
			String jsonString = req.content().toString(Charset.defaultCharset());
			l.debug("Building HTTP response: ", jsonString);
			Map<String,Object> reqBody = om.readValue(jsonString, new TypeReference<HashMap<String, Object>>(){});
			l.debug("Building HTTP response: ", reqBody);
			FullHttpResponse response = new DefaultFullHttpResponse(
					HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
					Unpooled.EMPTY_BUFFER);
			response.headers().set("content-type", "application/json");
			response.headers().set("content-length",
					response.content().readableBytes());

			ctx.write(response);

		} catch (Exception e) {
			l.error("Exception occured: {}", new Object[] { e.getStackTrace() });
		} finally {
			l.debug("Releasing request buffer object");
			ctx.writeAndFlush(
					new DefaultFullHttpResponse(req.getProtocolVersion(),
							HttpResponseStatus.OK, Unpooled.EMPTY_BUFFER))
					.addListener(ChannelFutureListener.CLOSE);
		}

	}

}
