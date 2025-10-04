package client.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;

public class NonBlockingClient {
    String ip;
    int port;
    NonBlockingClient(){

    }
    public NonBlockingClient(String ip,int port)throws IOException{
        start(ip, port);
    }
    public void start(String ip,int port) throws IOException{
        SocketChannel ssc=SocketChannel.open();
        ssc.configureBlocking(false);
        while(!ssc.connect(new InetSocketAddress(ip, port))){

        }
        Selector selector =Selector.open();
        ssc.register(selector,SelectionKey.OP_READ,ByteBuffer.allocate(1024));

        new Thread(
            new Runnable() {
                @Override
                public void run(){
                    while (true) {
                    try{
                        if(selector.select(1000)==0){
                            continue;
                        }
                        Iterator<SelectionKey> iterator=selector.selectedKeys().iterator();
                        while(iterator.hasNext()){
                            SelectionKey key=iterator.next();
                            SocketChannel channel = (SocketChannel)key.channel();
                            ByteBuffer buffer=(ByteBuffer)key.attachment();
                            StringBuffer response=new StringBuffer();
                            int read=1;
                            while (read>0) {
                                buffer.clear();
                                read=channel.read(buffer);
                                response.append(new String(buffer.array(),0,read));
                            }
                            System.out.println("收到服务端的响应："+response.toString());
                            iterator.remove();
                        }
                        }catch(IOException e){
                            e.printStackTrace();
                        }
                    }

                }
            }
        ).start();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            int methodNum=scanner.nextInt();
            String massage=scanner.next();
            String msg=new String(methodNum+"#"+massage);
            ssc.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
            System.out.println("消息发送！");
        }
    }

}
