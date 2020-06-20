import ocsf.client.*;
import common.*;
import java.io.*;
import client.*;
import ocsf.server.*;

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

  public void command(String msg){

    ////////////////////////////////////////////////////////////////

    System.out.println("1");

    if(msg.charAt(0) != '#'){
      System.out.println("2");
      server.serverConsoleDisplay(msg);
      System.out.println("3");
      server.sendToAllClients(msg);
    }
    else{
      String[] cmd = msg.split(" ");


      if(cmd.length == 1){

        System.out.println("5");

        if(cmd[0].equals("#quit")){
          System.out.println("6");
          System.exit(0);
        }else if(cmd[0].equals("#stop")){
          System.out.println("7");
          server.stopListening();
        }else if(cmd[0].equals("#close")){
          System.out.println("8");
          try{
            server.close();
          }catch(Exception e){}

        }else if(cmd[0].equals("#start")){
          System.out.println("9");
          if(!server.isListening()){
            try{
              server.listen();
            }catch(Exception e){}
          }else{
            display("Server has already started.");
          }
        }else if(cmd[0].equals("#getport")){
          System.out.println(server.getPort());
        }
      }
      else if (cmd.length == 2){
        System.out.println("10");
        if(cmd[0].equals("#setport")){
            server.setPort(Integer.parseInt(cmd[1]));
        }
      }
      else{
        display("Invalid command!");
      }
    }
    ////////////////////////////////////////////////////////////////

  }

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
