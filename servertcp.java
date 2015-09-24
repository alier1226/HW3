import java.io.*;
import java.net.*;

class servertcp {
	public static void main(String args[]) throws Exception {
		String input;
		int portnum = Integer.parseInt(args[0]);           
		ServerSocket svsocket = new ServerSocket(portnum);

		while(true) {
			Socket inputsocket = svsocket.accept();
			BufferedReader inputdata =
			new BufferedReader(new InputStreamReader(inputsocket.getInputStream()));
			DataOutputStream outputdata = new DataOutputStream(inputsocket.getOutputStream());
			input = inputdata.readLine();
			String[] outputtemp = input.split(",");
			int output=0;
			if(Integer.parseInt(outputtemp[1])>=0 && Integer.parseInt(outputtemp[2])>=0){
				if(outputtemp[0].equals("add")){
					output = Integer.parseInt(outputtemp[1])+Integer.parseInt(outputtemp[2]);
					if(output<65536){
						outputdata.writeBytes(output+"\n");
					}
					else{
						outputdata.writeBytes( "Output is not in 2 bytes\n");
					}
				}
				else if(outputtemp[0].equals("multiply")){
					output = Integer.parseInt(outputtemp[1])*Integer.parseInt(outputtemp[2]);
					if(output<65536){
						outputdata.writeBytes(output+"\n");
					}
					else{
						outputdata.writeBytes( "Output is not in 2 bytes\n");
					}
				}
				else{
					outputdata.writeBytes("wrong command"+"\n");
				}
			}
			else{
				outputdata.writeBytes("wrong input\n");
			}

		}
	}
}