package com.neworange.netty4;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/4/15 10:31
 * @description 处理服务端 channel.
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(io.netty.channel.ChannelHandlerContext ctx, Object msg) throws Exception {
        // 默默地丢弃收到的数据
//        ByteBuf in = (ByteBuf) msg;
        try {
//            while (in.isReadable()) { // (1)
//                System.out.print((char) in.readByte());
//                System.out.flush();
//            }
            ctx.write(msg); // (1)
            ctx.flush();
//            ctx.write(msg);
            // Do something with msg
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
//    @Override
//    public void channelReadComplete(io.netty.channel.ChannelHandlerContext ctx) throws Exception {
//        // 将消息写入到缓冲并刷新
//        // 注意，这里不是关闭 Channel
//        ctx.writeAndFlush(ctx.alloc().buffer().writeBytes("hello".getBytes()));
//    }
    @Override
    public void exceptionCaught(io.netty.channel.ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}
