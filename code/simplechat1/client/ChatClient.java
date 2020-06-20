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
      if(message.charAt(0) != '#'){
        sendToServer(message);
      }
      else{
        String[] cmd = message.split(" ");

        if(cmd.length == 1){
          if(cmd[0].equals("#quit")){
            quit();
          }else if(cmd[0].equals("#logoff")){
            try{
              closeConnection();
            }catch(Exception e){}
          }else if(cmd[0].equals("#login")){
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
        }
        else if (cmd.length == 2){
          if(cmd[0].equals("#sethost")){
            System.out.println("1");
            if(!isConnected()){
              System.out.println("2");
              try{
                System.out.println("cmd[1] is \"" + cmd[1]+ "\"");
                setHost(cmd[1]);
                System.out.println("Host set to :" + Integer.parseInt(cmd[1]));
              }catch(Exception e){
                System.out.println("error");
              }
            }else{
              System.out.println("client has not logged off yet.");
            }



          }else if(cmd[0].equals("#setport")){
            setPort(Integer.parseInt(cmd[1]));
            System.out.println("Port set to :" + Integer.parseInt(cmd[1]));
          }
        }
        else{
          sendToServer("Invalid command!");
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

  public void connectionClosed(){
    System.out.println("Connection to server has been shut down");
  }

  public void connectionException(Exception ex){
    System.out.println("Warning - connection to server has stopped \n"+"Disconnecting");
    this.quit();
    /*
    try{
      closeConnection();
    } catch(Exception e){}
    */
  }
}
//End of ChatClient class
