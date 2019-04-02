/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uts;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;


/**
 *
 * @author ayya
 */
 
public class MultiThreadChatServer16190 {

  // The server socket.
  private static ServerSocket serverSocket = null;
  // The client socket.
  public static Socket clientSocket = null;

  // This chat server can accept up to maxClientsCount clients' connections.
  private static final int maxClientsCount = 10;

  //Array untuk menyimpang sekumpulan running thread

  private static final clientThread[] ArrayRunningSocket = new clientThread[maxClientsCount];
  
  public static void main(String args[]) {

    // The default port number.
    int portNumber = 2222;
    if (args.length < 1) {
      System.out.println("Usage: java MultiThreadChatServer <portNumber>\n" + "Now using port number=" + portNumber);
    } else {
      portNumber = Integer.valueOf(args[0]).intValue();
    }

    /*
     * Open a server socket on the portNumber (default 2222). Note that we can
     * not choose a port less than 1023 if we are not privileged users (root).
     */
    try {
      serverSocket = new ServerSocket(portNumber);
    } catch (IOException e) {
      System.out.println(e);
    }

    /*
     * Create a client socket for each connection and pass it to a new client
     * thread.
     */
    while (true) {
      try {
            // blocking menunggu ada client konek
             clientSocket = serverSocket.accept();
            int i = 0;
            for (i = 0; i < maxClientsCount; i++) {
                if (ArrayRunningSocket[i] == null) {
                   ArrayRunningSocket[i] = new clientThread(clientSocket, ArrayRunningSocket);
                   ArrayRunningSocket[i].start();
                   break;
                 //  (ArrayRunningSocket[i] = new clientThread(clientSocket, ArrayRunningSocket)).start();
                 }
             }

        // jika array sudah penuh kirim pesan bahwa jumlah client sudah maksimal
        if (i == maxClientsCount) {
          PrintStream os = new PrintStream(clientSocket.getOutputStream());
          os.println("Server too busy. Try later.");
          os.close();
          clientSocket.close();
        }
      } catch (IOException e) {
        System.out.println(e);
      }
    }
  }
}
/*
 * The chat client thread. This client thread opens the input and the output
 * streams for a particular client, ask the client's name, informs all the
 * clients connected to the server about the fact that a new client has joined
 * the chat room, and as long as it receive data, echos that data back to all
 * other clients. When a client leaves the chat room this thread informs also
 * all the clients about that and terminates.
 */
class clientThread extends Thread {

  private DataInputStream is = null;
  private PrintStream os = null;
  private Socket clientSocket = null;
  private final clientThread[] threads;
  private int maxClientsCount;
  private String nama;

  public clientThread(Socket clientSocket, clientThread[] threads) {
    this.clientSocket = clientSocket;
    this.threads = threads;
    maxClientsCount = threads.length;
  }


  /*
  public void setNama(String nama){
      this.nama=nama;
  }
  public boolean getNama(String nama){
      boolean cek = false;
      for (int i = 0; i < maxClientsCount; i++) {
        if (ArrayRunningSocket[i] != null ) {
          if (this.nama.equalsIgnoreCase(nama)){
              return true;
          }
        }
      }
      return cek;
  }
   *
   *
   */
  public void run() {
    int maxClientsCount = this.maxClientsCount;
    clientThread[] threads = this.threads;

   try {
      /*
       * Create input and output streams for this client.
       */
      is = new DataInputStream(clientSocket.getInputStream());
      os = new PrintStream(clientSocket.getOutputStream());
      
      os.println("Masukkan namamu .");
      String name = is.readLine().trim();
      this.nama= name;
      
      for(int i = 0; i < maxClientsCount; i++) {
        if(threads[i] != null && threads[i].getName().equals(name)) {
            os.println("----nama sudah dipakai , tekan enter untuk melanjutkan!!---");
            clientSocket.close();
            //threads[i]=null;
            
            
        }
      }
      
      this.setName(name);
      
      os.println("Hello " + name + " to our chat room.\nTo leave enter /quit in a new line\n"
              + "To private chat  enter ?username?message\n"
              + "To kick enter #username \n"
              + "To see user online enter $\n"
              + "To broadcast enter *username*message\n");
      
      for(int i = 0;  i < maxClientsCount; i++) {
        if(threads[i] != null && threads[i] != this) {
          threads[i].os.println("*** A new user " + name   + " entered the chat room !!! ***");
        }
      }
      while (true) {
        String line = is.readLine();
        String replace = null ;
        String kicked = null;
        String ganti=null;
        
        if (line.startsWith("/quit")) {
          break;
        }
        else if (line.startsWith("?")){
                String receiver = line.split("(?<=[?])")[1];
                replace = receiver.replace("?", "");
                System.out.println("RECEIVER : " +replace);
                //System.out.println("tes="+receiver);
               
                if (replace !=null){
                  for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null && threads[i].getName().equals(replace)) {
                      threads[i].os.println("<pc<" + name + "--> " + line);
                      os.println("<pc<"+name+"-->"+line);
                    }
                    else if (threads[i].getName().equals("")){
                        os.println("user tidak ditemukan");
                    }
                  
                } 
                }  
                else{
                  for (int i = 0; i < maxClientsCount; i++) {  
                    if (threads[i]!= null) {
                      threads[i].os.println("<" + name + "--> " + line);
                    }
                   }
                }   
        }
        else if(line.startsWith("*")){
            String receiver = line.split("\\*")[1];
                ganti = receiver.replace("*", "");
                System.out.println("RECEIVER : " +ganti);
                System.out.println("tes2="+receiver);
               
                if (ganti !=null){
                  for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null && threads[i].getName().equals(ganti)) {
                      
                     threads[i].os.println("bc selain kamu");
                    }
                    else {
                        threads[i].os.println("<bc<" + name + "--> " + line);
                    }
                  
                } 
        
        
        }
        }
        else if (line.startsWith("#")){
                String kicker = line.split(" ")[0];
                kicked = kicker.replace("#", "");
                System.out.println("kicked : " +kicked );
                System.out.println("kicker : "+kicker);
                if (kicked !=null){
                    for(int i =0;i<maxClientsCount;i++){
                        if(threads[i] !=null && threads[i].getName().equals(kicked))
                        {   threads[i].os.println("kmu telah di kick , bye!");
                            threads[i].clientSocket.close();
                            threads[i]=null;
                            os.println("kmu telah mengkick "+kicked);
                        }
                    }
                }
        }
        else if (line.startsWith("$")){
            for(int i=0;i<maxClientsCount;i++){
                if (threads[i]!=null)
                os.println(threads[i].getName());
        }
        } 
        else {
       for (int i = 0; i < maxClientsCount; i++) {
        if (threads[i] != null) {
          threads[i].os.println(this.nama+"--->"+line);
        }
      }
        }
       
      }
      for (int i = 0; i < maxClientsCount; i++) {
        if (threads[i] != null && threads[i] != this) {
          threads[i].os.println("*** The user " + name + " is leaving the chat room !!! ***");
        }
      }
      os.println("*** Bye " + name + " ***");

      /*
       * Clean up. Set the current thread variable to null so that a new client
       * could be accepted by the server.
       */
      for (int i = 0; i < maxClientsCount; i++) {
        if (threads[i] == this) {
          threads[i] = null;
        }
      }

      /*
       * Close the output stream, close the input stream, close the socket.
       */
      is.close();
      os.close();
      clientSocket.close();
    } catch (IOException e) {
    }
  }
  }
        
