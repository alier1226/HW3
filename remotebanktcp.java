import java.io.*;
import java.net.*;
import java.security.*;

public class remotebanktcp {
	static int maxtry=3;
	public static void main(String args[]) throws Exception {
		try {
			if(args.length!=5 && args.length!=6){
            	throw new IllegalArgumentException("your input is invalid");
        	}
			String[] temp = args[0].split(":");
			InetAddress destAddr = InetAddress.getByName("127.0.0.1");  // Destination address
        	int portnum = 1234;
        	try {
        		destAddr = InetAddress.getByName(temp[0]); 
        		portnum = Integer.parseInt(temp[1]);
        		if(portnum>65535 ||portnum<1024){
        			portnum = 1234;
        			System.out.println("Invalid input. Set portnum to 1234");
        		}
        	}
        	catch (Exception e){
        		System.out.println("Invalid input. Set address to local, set portnum to 1234");
        	} 
			Socket clientSocket = new Socket(destAddr, portnum);
			Socket clientSocket2 = new Socket(destAddr, portnum);
			Boolean debug = false;
			if(args.length == 6){
        		if(args[5].equals("-d")){
        			debug = true;
        		}
        		else{
        			System.out.println("Unregonized command. Use -d to debug");
        		}
        	}
        	String authentication = "request authentication,";
        	if(debug == true){
        		authentication = "request authentication,-d,";
        	}
        	//send package
        	DataOutputStream sendtosever = new DataOutputStream(clientSocket.getOutputStream());
        	if(debug == true){
	        	System.out.println("Sending authentication request to server");
        	}
			BufferedReader getoutput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			sendtosever.writeBytes(authentication+'\n');
			String chanllenge = getoutput.readLine();

			//received challenge
			String sentence = args[1]+",";
	        String hashString = args[1]+args[2]+chanllenge;
	        byte[] bytesOfMessage = hashString.getBytes();
				MessageDigest md = MessageDigest.getInstance("MD5");
				byte[] thedigest = md.digest(bytesOfMessage);
				StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < thedigest.length; ++i) {
	          	sb.append(Integer.toHexString((thedigest[i] & 0xFF) | 0x100).substring(1,3));
	       	}
		    sentence+= sb.toString()+",";
			for(int i =3;i<args.length;i++){
					sentence+=args[i]+",";
			}
			try{
				Double balance= Double.parseDouble(args[4]);
			}
			catch(Exception e){
				throw new IllegalArgumentException("invalid input");
			}
			sendtosever = new DataOutputStream(clientSocket2.getOutputStream());
        	if(debug == true){
	        	System.out.println("Sending Hash String to server: ");
        	}
			getoutput = new BufferedReader(new InputStreamReader(clientSocket2.getInputStream()));
			sendtosever.writeBytes(sentence+'\n');
			String modifiedSentence = getoutput.readLine();
			String[] output = modifiedSentence.split(",");
			for(int i =0;i<output.length;i++){
				System.out.println(output[i]);
			}

			clientSocket.close();
			clientSocket2.close();

		}
		catch(SocketException e1) {

     		System.out.println("Can't create socket based on your input. Please check.");
		}
	}	
}
