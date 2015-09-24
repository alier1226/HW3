import java.io.*;
import java.net.*;

public class remotebanktcp {
	public static void main(String args[]) throws Exception
	{
		try{
			if(args.length!=4){
				throw new IllegalArgumentException("4 arguments needed");
			}
			String[] temp = args[0].split(":");
			InetAddress destAddr = InetAddress.getByName(temp[0]);  // Destination address
			int portnum = Integer.parseInt(temp[1]);               // Destination port
			Socket clientSocket = new Socket(destAddr, portnum);
			String output;
			DataOutputStream sendtosever = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader getoutput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			sendtosever.writeBytes(args[1]+","+args[2]+","+args[3]+'\n');
			output = getoutput.readLine();
			System.out.println("FROM SERVER: " + output);
			clientSocket.close();
		}
		catch(SocketException e1) {
		}
	}	
}
