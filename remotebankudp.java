import java.net.*;
import java.security.*;

public class remotebankudp {
	static int maxtry=3;
	   public static void main(String args[]) throws Exception { 
    	try{
        	if(args.length!=5 && args.length!=6){
            	throw new IllegalArgumentException("your input is invalid");
        	}
        	String[] temp = args[0].split(":");
        	InetAddress destAddr = InetAddress.getByName(temp[0]);  // Destination address
        	int portnum = Integer.parseInt(temp[1]);
        	DatagramSocket clientSocket = new DatagramSocket();
        	byte[] outputdata = new byte[1024];
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
        	outputdata = authentication.getBytes();
        	DatagramPacket sendPacket = new DatagramPacket(outputdata, outputdata.length, destAddr, portnum);
        	clientSocket.send(sendPacket);
        	if(debug == true){
	        	System.out.println("Sending authentication request to server");
        	}
        	clientSocket.setSoTimeout(2000);   // set the timeout in millisecounds.
        	while(maxtry >0){// recieve data until timeout
	            try {
	          		byte[] inputdata = new byte[1024];
	                DatagramPacket receivePacket = new DatagramPacket(inputdata, inputdata.length);
	                clientSocket.receive(receivePacket);
	                byte[]buffer = receivePacket.getData();
	                byte[] bufferData = new byte[64];
	                for(int m = 0; m<bufferData.length; m++){
	             		bufferData[m] = buffer[m];
	              	}
	              	String modifiedSentence = new String(bufferData);
			        String sentence = args[1]+",";
			        String hashString = args[1]+args[2]+modifiedSentence;
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
					if(debug == true){
						sentence+="-d,";
					}
 					outputdata = sentence.getBytes();  
        			sendPacket = new DatagramPacket(outputdata, sentence.getBytes().length, destAddr, portnum);
      				clientSocket.send(sendPacket);
	         		if(debug == true){
		         		System.out.println("Sending Hash String to server");
	         		}
      				clientSocket.setSoTimeout(2000);   // set the timeout in millisecounds.
	      		    while(maxtry >0){
          				try {
	            	 		byte[] inputdataNew = new byte[1024];
		 	                receivePacket = new DatagramPacket(inputdataNew, inputdataNew.length);
		 	                clientSocket.receive(receivePacket);
		 	                modifiedSentence = new String(receivePacket.getData());
		 	                System.out.println(modifiedSentence);
		 	                clientSocket.close();
	 	                }
               			catch (SocketTimeoutException e) {
          				System.out.println("Timeout reached " + e);
                 			if(debug == true){
    						System.out.println("Resending...");
    					}
                   			maxtry--;
               			}
   					}  
				}
		        catch (SocketTimeoutException e) {
		            System.out.println("Timeout reached " + e);
	             	if(debug == true){
		        		System.out.println("Resending...");
	        		}
						maxtry--;
					}
				}
        	clientSocket.close();
    	} 
     	catch (SocketException e1) {
   		}
	}
}
