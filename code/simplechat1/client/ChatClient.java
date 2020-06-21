// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************

  /**
   * The interface type variable.  It allows the implementation of
   * the display method in the client.
   */
  ChatIF clientUI;
  String loginid;

  //Constructors ****************************************************

  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */

  public ChatClient(String loginid, String host, int port, ChatIF clientUI)
    throws IOException
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginid = loginid;

    //tries to establish a connection and sending the loginid as soon as the connection is established
    //if there are no available server, prints an error message
    try{
      openConnection();
      sendToServer("#login " + loginid);
    }
    catch(IOException e){
      System.out.println("Error, cannot establish connection.");
    }

  }


  //Instance methods ************************************************

  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg)
  {
    clientUI.display(msg.toString());

  }

  /**
   * This method handles all data coming from the UI
   *
   * @param message The message from the UI.
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
      //if the message is not a command, simply send it to server
      if(message.charAt(0) != '#'){
        sendToServer(message);
      }
      else{

        //if the message is a command, then see if there are 2 parts to the command
        String[] cmd = message.split(" ");
        //if statement for single commands
        if(cmd.length == 1){
          if(cmd[0].equals("#quit")){
            quit();
          }else if(cmd[0].equals("#logoff")){
            try{
              closeConnection();
            }catch(Exception e){}
          }else if(cmd[0].equals("#login")){
            //if the client is already logged in, then prints a message
            if(isConnected()){
              System.out.println("User is already logged in");
            }else{
              openConnection();
            }
          }else if(cmd[0].equals("#gethost")){
            System.out.println(this.getHost());
          }else if(cmd[0].equals("#getport")){
            System.out.println(this.getPort());
          }
          else{
            System.out.println("Invalid command!");
          }
        }
        else if (cmd[0].contains("#set")){
          //if the command contains #set, it will require an input, hence length is 2
          if(cmd.length == 2){
            //for both setHost and setPort, the client needs to be diconnected first
            //otherwise prints a message
            if(!isConnected()){
              if(cmd[0].equals("#sethost")){
                  setHost(cmd[1]);
                  System.out.println("Host set to : " + cmd[1]);

              }else if(cmd[0].equals("#setport")){
                setPort(Integer.parseInt(cmd[1]));
                System.out.println("Port set to : " + Integer.parseInt(cmd[1]));
              }
            }
            else{
              System.out.println("client has not logged off yet.");
            }
          }
          else{
            System.out.println("Invalid command!");
          }
        }
        else{
          System.out.println("Invalid command!");
        }
      }
    }catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }

  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }

  //implementation of connectionClosed method in AbstractClient
  public void connectionClosed(){
    System.out.println("Connection to server has been shut down");
  }

  //implementation of connectionException method in AbstractClient
  public void connectionException(Exception ex){
    System.out.println("Warning - connection to server has stopped \n"+"Disconnecting");
    this.quit();
  }
}
//End of ChatClient class
