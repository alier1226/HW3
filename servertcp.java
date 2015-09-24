import java.io.*;
import java.net.*;
import java.security.*;

public class servertcp {
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
		ServerSocket svsocket = new ServerSocket(portnum);

		while(true) {
			Socket inputsocket = svsocket.accept();
			BufferedReader inputdata =
			new BufferedReader(new InputStreamReader(inputsocket.getInputStream()));
			DataOutputStream outputdata = new DataOutputStream(inputsocket.getOutputStream());
			String input = inputdata.readLine();
			String[] inputtemp = input.split(",");
			boolean login = false;

			if(inputtemp[0].equals("request authentication")){
				//send challenge
				challenge = "";
				for (int i = 64;i>0;i--){
					int num = (int) (Math.random()*characters.length());
					challenge+=characters.charAt(num);
				}
				outputdata.writeBytes(challenge+"\n");
				if(inputtemp.length >1 && inputtemp[1].equals("-d")){
					 System.out.println("Generating challenge");
				}
		  	}
		  	else {
		  		//check pw/username
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
							if(inputtemp.length >4 && inputtemp[4].equals("-d")){
								System.out.println("Sending successful message");
							}
							if(inputtemp[2].equals("deposit")){
								users[i].balance += Double.parseDouble(inputtemp[3]);
								outputdata.writeBytes("Welcome " + inputtemp[0] + ",Your deposit of " +Double.parseDouble(inputtemp[3])+" is sucessfully recorded,");
								outputdata.writeBytes("Your new account balance is " + Double.toString(users[i].balance)+",");
								outputdata.writeBytes("Thank you for banking with us.,\n");
							}
							else if(inputtemp[2].equals("withdraw")){
								if(users[i].balance < Double.parseDouble(inputtemp[3])){
									outputdata.writeBytes("You don't have enough money,\n");
								}
								else{
									users[i].balance -= Double.parseDouble(inputtemp[3]);
									outputdata.writeBytes("Welcome " + inputtemp[0] + ",Your withdraw of " +inputtemp[3]+" is sucessfully recorded,");
									outputdata.writeBytes("Your new account balance is " + Double.toString(users[i].balance)+",");
									outputdata.writeBytes("Thank you for banking with us.,\n");
								}
							}
						  	else{
						   		outputdata.writeBytes("Invalid command: must be deposit or withdraw,\n");
						  	}
						}
						else{
						  outputdata.writeBytes("Wrong input: must be positive number,\n");
						} 
					}
					catch(IllegalArgumentException e){
						throw new IllegalArgumentException("Please check your input. Must be valid number.,\n");
					}
				}
				else{
					//login failed
					outputdata.writeBytes("Wrong username/password combination.,\n");
				}
		  	}
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