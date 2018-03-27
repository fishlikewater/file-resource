package fishlikewater.service;

import fishlikewater.handler.FileServerHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public class ServerHandlerInitializer extends ChannelInitializer{
    @Override
    protected void initChannel(Channel ch) throws Exception {


        //它负责把字节解码成Http请求。
        ch.pipeline().addLast("decoder", new HttpRequestDecoder());
        //当Server处理完消息后，需要向Client发送响应。那么需要把响应编码成字节，再发送出去。故添加HttpResponseEncoder处理器。
        ch.pipeline().addLast("encoder", new HttpResponseEncoder());
        //它负责把多个HttpMessage组装成一个完整的Http请求或者响应。到底是组装成请求还是响应，则取决于它所处理的内容是请求的内容，还是响应的内容。
        //这其实可以通过Inbound和Outbound来判断，对于Server端而言，在Inbound 端接收请求，在Outbound端返回响应。
        //如果Server向Client返回的数据指定的传输编码是 chunked。则，Server不需要知道发送给Client的数据总长度是多少，它是通过分块发送的，参考分块传输编码
        //pipeline.addLast("http-aggregator", new HttpObjectAggregator(65536));
        ch.pipeline().addLast("deflater", new HttpContentCompressor());
        //该通道处理器主要是为了处理大文件传输的情形。大文件传输时，需要复杂的状态管理，而ChunkedWriteHandler实现这个功能。
        ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
        //自定义的通道处理器，其目的是实现文件服务器的业务逻辑。
        ch.pipeline().addLast("handler", new FileServerHandler());

       /* ch.pipeline().addLast(
                new StringEncoder(CharsetUtil.UTF_8),
                new LineBasedFrameDecoder(8192),
                new StringDecoder(CharsetUtil.UTF_8),
                new ChunkedWriteHandler(),
                new FileServerHandler());*/
    }
}
