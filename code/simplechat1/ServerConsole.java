import ocsf.client.*;
import common.*;
import java.io.*;
import client.*;
import ocsf.server.*;

//ServerConsole is a UI for EchoServer, it allows user to input commands for EchoServer
public class ServerConsole implements ChatIF
{
  final public static int DEFAULT_PORT = 5555;
  EchoServer server;

  public ServerConsole(int port)
  {
    try
    {
      server = new EchoServer(port);
    }
    catch(Exception exception)
    {
      System.out.println("Error: Can't setup connection!"
                + " Terminating server.");
      System.exit(1);
    }
  }

  //Similar to ClientConsole, this method accepts user to input commands
  public void accept()
  {

    try
    {
      BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));
      String message;

      while (true)
      {
        message = fromConsole.readLine();
        server.handleMessageFromServerUI(message,this);

      }
    }
    catch (Exception ex)
    {
      System.out.println
        ("Server : Unexpected error while reading from console!");
    }
  }

  //accessor to
  public EchoServer getServer(){
    return this.server;
  }

  public void display(String message)
  {
    System.out.println("SERVER MSG> " + message);
  }


  public static void main(String[] args)
  {

    int port = 0;  //The port number

    //looks for user-defined port, otherwise sets it to default
    try{
      port = Integer.parseInt(args[0]);
    } catch(Exception e){
      port = DEFAULT_PORT;
    }

    ServerConsole serverUI = new ServerConsole(port);
    
    try
    {
      serverUI.getServer().listen(); //Start listening for connections
      serverUI.accept();  //Wait for console data
    }
    catch (Exception ex)
    {
      System.out.println("ERROR - Could not listen for clients!");
    }

  }
}
