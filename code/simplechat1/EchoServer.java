// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com

import ocsf.client.*;
import common.*;
import java.io.*;
import client.*;
import ocsf.server.*;
import java.util.ArrayList; // import the ArrayList class


/**
 * This class overrides some of the methods in the abstract
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer
{
  //Class variables *************************************************

  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  ServerConsole serverUI;

  //Constructors ****************************************************

  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port)
  {
    super(port);
  }


  //Instance methods ************************************************

  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
    //if the message has #login from a client, it means that a new client has connected to the server
    if(msg.toString().contains("#login")){
      //checks whether the login id is already in use
      if(client.getInfo("#login") == null){
        String[] log = msg.toString().split(" ");
        //storing login ID
        client.setInfo(log[0],log[1]);

        System.out.println(log[1] + " has logged on.");
        try{
          sendToAllClients(log[1] + " has logged on.");
        }catch(Exception e){}

      }else{
        try{
          client.sendToClient("Error, already logged in");
        }catch(Exception e){}
      }

    }
    else{
      System.out.println("Message received: " + msg + " from " + client.getInfo("#login"));
      String loginid = client.getInfo("#login").toString();
      String msg_str = msg.toString();
      this.sendToAllClients(loginid + "> " + msg_str);
    }

  }

  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }

  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }

  //implementation of clientConnected method in AbstractServer
  public void clientConnected(ConnectionToClient client){
    System.out.println("Welcome, a client has connected.");
  }

  //implementation of clientDisconnected method in AbstractServer
  public void clientDisconnected(ConnectionToClient client){
    System.out.println(client.getInfo("#login").toString() + " has disconnected.");
  }

  //implementation of clientException method in AbstractServer
  public void clientException(ConnectionToClient client, Throwable exception){
    System.out.println(client.getInfo("#login").toString() + " stopped.");
  }


  //this method handles messages from ServerConsole
  public void handleMessageFromServerUI(String msg, ServerConsole serverUI)
  {
    this.serverUI = serverUI;
    ///////////////////////////////////////////////////////////////////////////
    //if message does not have a hashtag, simply treat it as a normal message and echo it to all clients
    if(msg.charAt(0) != '#'){
      serverUI.display(msg);
      sendToAllClients("SERVER MSG>" + msg);
    }
    else{
      //
      String[] cmd = msg.split(" ");

        //if the message is a command, then see if there are 2 parts to the command
      if(cmd.length == 1){
        //if statement for single commands
        if(cmd[0].equals("#quit")){
          System.exit(0);
        }else if(cmd[0].equals("#stop")){
          stopListening();
        }else if(cmd[0].equals("#close")){
          try{
            stopListening();
            close();
          }catch(Exception e){}

        }else if(cmd[0].equals("#start")){
          if(!isListening()){
            try{
              listen();
            }catch(IOException e){}
          }else{
            serverUI.display("Server has already started.");
          }
        }else if(cmd[0].equals("#getport")){
          System.out.println(getPort());
        }
        else{
          serverUI.display("Invalid command!");
        }
      }
      else if (cmd.length == 2 && cmd[0].equals("#setport")){
        //if the command contains #setport, it will require an input, hence length is 2
          if(!isListening() && getNumberOfClients() == 0){
            setPort(Integer.parseInt(cmd[1]));
            serverUI.display("Port set to : " + Integer.parseInt(cmd[1]));
          }else{
            serverUI.display("Server is not closed.");
          }
      }
      else{
        serverUI.display("Invalid command!");
      }
    }
    ////////////////////////////////////////////////////////////////////////////
  }

  //Class methods ***************************************************

  /**
   * This method is responsible for the creation of
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555
   *          if no argument is entered.
   */
  public static void main(String[] args)
  {
    int port = 0; //Port to listen on

    //looks for a ServerConsole-defined port, sets to default(5555) otherwise
    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }

    //Initialize a new EchoServer
    EchoServer sv = new EchoServer(port);

    try
    {
      sv.listen(); //Start listening for connections
    }
    catch (Exception ex)
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}
//End of EchoServer class
