package com.fr.swift.executor.task.netty.server;


import com.fr.swift.executor.task.netty.protocol.FilePacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Hoky
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
@ChannelHandler.Sharable
public class FilePacketServerHandler extends SimpleChannelInboundHandler<FilePacket> {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FilePacket packet) throws Exception {
//		File file = packet.getFile();
//		SwiftLoggers.getLogger().info("receive file from client: " + file.getName());
////		FileReceiveServerHandler.clearReadLength();
////		FileReceiveServerHandler.setFileLength(file.length());
////		FileReceiveServerHandler.setOutputStream(new FileOutputStream(packet.getTargetPath()));
//		packet.setACK(packet.getACK() + 1);
//		ctx.writeAndFlush(packet);
	}
}
