package client.bootstrap.nio;

import client.nio.NonBlockingClient;
public class NIONonBlockingStart {
    public static void main(String[] args){
      
      try {
          NonBlockingClient client=new NonBlockingClient("127.0.0.1",6666);
      } catch (Exception e) {
        e.printStackTrace();
      }
        
        
    }
    
}
