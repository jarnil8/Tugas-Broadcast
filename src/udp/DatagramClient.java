
package udp;

import java.util.*;
import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatagramClient implements Runnable{
    
     private static DatagramSocket socket;
     private static  InetAddress address;
     private static DatagramPacket packet;
     private static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
     private static  boolean listen = true;
     
     public static void main(String[] args) throws IOException {

        //MulticastSocket socket = new MulticastSocket(4445);
	//socket.joinGroup(address);
        //InetAddress address = InetAddress.getByName("localhost");
        socket = new DatagramSocket();
        address = InetAddress.getByName("localhost");
	DatagramClient oke = new DatagramClient();
        
        String input;
        byte[] buf = new byte[1024];
        System.out.print("Nama : ");
        input= in.readLine();
        
        String first = "1";
        input = first+input;
        buf = input.getBytes();
        
        packet = new DatagramPacket(buf, buf.length,address,4445);
        socket.send(packet);
        //System.out.println("oke");
        
        byte[] buf1 = new byte[256];
        packet = new DatagramPacket(buf1,buf1.length);
          
        new Thread(new DatagramClient()).start();
        
        boolean write = true;
        while(write){
            try {
                input= in.readLine();
                buf = input.getBytes();
                packet = new DatagramPacket(buf, buf.length,address,4445);
                socket.send(packet);
                if(input.equals("bye")){
                    write = false;
                    listen = false;
                }
                //System.out.println("hai");
            } catch (IOException ex) {
                Logger.getLogger(DatagramClient.class.getName()).log(Level.SEVERE, null, ex);
                write = false;
            }
        }
        socket.close();
    }
     
    public void run(){
       listen=true;
       while(listen){
                byte[] buf1 = new byte[256];
                DatagramPacket packet2 = new DatagramPacket(buf1,buf1.length);
                try {
                    socket.receive(packet2);
                } catch (IOException ex) {
                    Logger.getLogger(DatagramClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println(new String(packet2.getData(), 0, packet2.getLength()));         
        } 
     }
}
