
package udp;

import java.util.*;
import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatagramServer implements Runnable{
   
    private static ArrayList<Client> threads;
    private static Client newClient = null;
    private static boolean listen = true;
    private static DatagramSocket socket;
    
    public DatagramServer(){       
       threads = new ArrayList<Client>();
        try {
            socket = new DatagramSocket(4445);
            System.out.println("Server is running.");
        } catch (SocketException ex) {
            Logger.getLogger(DatagramServer.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("sini");
        }
    }
    
    public static void Broadcast(InetAddress address,int port,byte[] buf){
        //System.out.println(port + " " + findPerson(port,address) );
        for(Client h : threads){
            //System.out.println(h.port + " "+h.nama);
            // System.out.println(threads.size());
            if(h.port!= port ){
                //&& !address.equals(h.address)
                DatagramPacket packet = new DatagramPacket(buf,buf.length,h.address,h.port);
                try {
                    socket.send(packet);
                    //System.out.println("yang di broadcast " +h.port + " "+h.nama);
                    //System.out.println(new String(packet.getData(),0,packet.getLength()));
                } catch (IOException ex) {
                    Logger.getLogger(DatagramServer.class.getName()).log(Level.SEVERE, null, ex);
                }  
            }
        }
    }
    
    public static void Whisper(Client newClient,byte[] buf){
        String input = new String(buf,0,buf.length);
        input = input.substring(1);
        byte[] sendbuff = new byte[256];
        String[] parts = null;
        String text = null;
        String whisperTo = null ;
        
        try{
            parts = input.split(" : ");
            whisperTo= parts[0]; // 004
            //System.out.println(newClient.nama+" "+whisperTo);
             text = parts[1]; 
        } catch(ArrayIndexOutOfBoundsException e){
            String notif = "wrong prefix";
            sendbuff = notif.getBytes();
            DatagramPacket packet = new DatagramPacket(sendbuff, sendbuff.length,newClient.address, newClient.port);
        }
        String prefix = newClient.nama+" : "+text;
        sendbuff =  prefix.getBytes();
        
        for(Client h : threads){
            if(h.nama.equals(whisperTo)){
               DatagramPacket packet = new DatagramPacket(sendbuff,sendbuff.length,h.address,h.port);
                try {
                    socket.send(packet);
                } catch (IOException ex) {
                    Logger.getLogger(DatagramServer.class.getName()).log(Level.SEVERE, null, ex);
                    
                }
            }
        }
    }
    
    public static String findPerson(int port,InetAddress address){
        String name=null;
        for(Client h: threads){
            if(port==h.port && address.equals(h.address)){
                name = h.nama;
            }
        }
        return name;
    }
    
    public static void main(String[] args) throws java.io.IOException {  
        new Thread(new DatagramServer()).start();
        
    }

    public void run(){
         while(listen){
             try {
                byte[] buf = new byte[256];
                byte[] buf2 = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf,buf.length); 
                InetAddress address; 
                socket.receive(packet);
                String in =new String(packet.getData(),0,packet.getLength()); 
                address = packet.getAddress();
                int port = packet.getPort();
                //newClient = findPort(port, address, newClient);
                String name = findPerson(port, address);
                // System.out.println(name);
                if(in.startsWith("1")){
                     String nama = new String(packet.getData(),0,packet.getLength());
                     nama = nama.substring(1);
                     newClient = new Client(packet.getAddress(),packet.getPort(),nama);
                     threads.add(newClient);
                     String wel = "\nWelcome " + newClient.nama + "\n " + "to quit type \"bye\" and to whisper use prefix @name : "; 
                     buf = (wel).getBytes();
                     packet = new DatagramPacket(buf,buf.length,packet.getAddress(), packet.getPort());
                     socket.send(packet);
                     wel = " enters the room";
                     buf = (newClient.nama+wel).getBytes(); 
                     Broadcast(address,port, buf);
                }
                else if(in.startsWith("@")){
                    //findPort(port, address, newClient);
                    Whisper(newClient,packet.getData());
                }
                else if(in.equals("bye")){
                    
                    String bye = name+" is leaving";
                    buf2 = bye.getBytes();
                    //packet = new DatagramPacket(buf,buf.length,newClient.address, newClient.port);
                    Broadcast(address,port,buf2);
                    
                }
                else{
                    byte[] buf3 = new byte[256];
                    String text = name+"<broadcast> : "+new String(packet.getData(),0,packet.getLength());
                    buf3 = text.getBytes();
                    Broadcast(packet.getAddress(),packet.getPort(),buf3);
                }
             } catch (IOException ex) {
                 Logger.getLogger(DatagramServer.class.getName()).log(Level.SEVERE, null, ex);
             }
        }
        socket.close();
    }
    
    public static class Client {
        protected BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        protected int port;
        protected InetAddress address;
        public String nama;
        
        public Client(InetAddress address,int port,String nama) throws IOException {
            this.port = port;
            this.nama = nama;
            this.address = address;
        }
    }
}
