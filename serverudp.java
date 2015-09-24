import java.net.*;
import java.security.*;

public class serverudp {    
	static int portnum;
	static String challenge = "";
	static user[] users= {new user("Jingyuan","123456",50),new user("Alier","654321",1000),new user("Hu","abcd",200)};
	static final String characters = "abcdefghijlmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	public static void main(String args[]) throws Exception {
		try{
		   portnum = Integer.parseInt(args[0]);
		}
		catch(Exception e){
		   throw new IllegalArgumentException ("you did not specify a port number, please try again");
		}
		DatagramSocket serverSocket = new DatagramSocket(portnum);
		
		while(true){
			byte[] inputdata = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(inputdata, inputdata.length);
			serverSocket.receive(receivePacket);
			String input = new String(receivePacket.getData());
			String[] inputtemp = input.split(",");
			String outputstr="";
			boolean login = false;
			if(inputtemp[0].equals("request authentication")){
				challenge = "";
				for (int i = 64;i>0;i--){
					int num = (int) (Math.random()*characters.length());
					challenge+=characters.charAt(num);
				}
			outputstr = challenge;
			if(inputtemp[1].equals("-d")){
				 System.out.println("Generating challenge");
			}
		  }
		  else{
			int i = 0;
			for(i =0; i < users.length; i++){
			  if(inputtemp[0].equals(users[i].name)){
				String hashString = inputtemp[0]+users[i].password+challenge;
				byte[] bytesOfMessage = hashString.getBytes();
				MessageDigest md = MessageDigest.getInstance("MD5");
				byte[] thedigest = md.digest(bytesOfMessage);
				StringBuffer sb = new StringBuffer();
				for (int t = 0; t < thedigest.length; ++t) {
				  sb.append(Integer.toHexString((thedigest[t] & 0xFF) | 0x100).substring(1,3));
				}
				if(sb.toString().equals(inputtemp[1])){
				  login = true;
				  break;
				}
			  }
			}
			if(login == true){
			  try{
				if(Double.parseDouble(inputtemp[3])>=0){
				  if(inputtemp[4].equals("-d")){
					System.out.println("Sending successful message");
				  }
				  if(inputtemp[2].equals("deposit")){
					users[i].balance += Double.parseDouble(inputtemp[3]);
					outputstr = "Welcome " + inputtemp[0] + "\nYour deposit of " +Double.parseDouble(inputtemp[3])+" is sucessfully recorded\n";
					outputstr += "Your new account balance is " + Double.toString(users[i].balance);
					 outputstr += ".\nThank you for banking with us.";
				   }
				  else if(inputtemp[2].equals("withdraw")){
					if(users[i].balance < Double.parseDouble(inputtemp[3])){
					  outputstr = "You don't have enough money";
					}
					else{
					 users[i].balance -= Double.parseDouble(inputtemp[3]);
					 outputstr = "Welcome " + inputtemp[0] + "\nYour withdraw of " +inputtemp[3]+" is sucessfully recorded\n";
					 outputstr += "Your new account balance is " + Double.toString(users[i].balance);
					 outputstr += ".\nThank you for banking with us.";
					}
				  }
				  else{
				   outputstr = "Invalid command: must be deposit or withdraw";
				  }
				}
				else{
				  outputstr = "Wrong input: must be positive number";
				}       
			  }
					 
			  catch(IllegalArgumentException e){
				throw new IllegalArgumentException("Please check your input. Must be valid number");
			  }
			}
			else{
			  outputstr = "Wrong username/password combination.";
			}
		  }
		  byte[] outputdata = new byte[1024];
		  outputdata = outputstr.getBytes("UTF-8");
		  InetAddress IPAddress = receivePacket.getAddress();
		  int port = receivePacket.getPort();
		  DatagramPacket sendPacket = new DatagramPacket(outputdata, outputdata.length, IPAddress, port);
		  serverSocket.send(sendPacket);
	  }
  }

  static class user{
	String name;
	String password;
	double balance;
	public user(String a, String b, double c){
		name = a;
		password = b;
		balance = c;
	}
  }
}
