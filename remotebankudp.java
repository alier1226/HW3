import java.net.*;
import java.security.*;
public class remotebankudp {
	   public static void main(String args[]) throws Exception
	   {
	      try{
	         if(args.length!=5 && args.length!=6){
	            throw new IllegalArgumentException("your input is invalid");
	         }
	         String[] temp = args[0].split(":");
	         InetAddress destAddr = InetAddress.getByName(temp[0]);  // Destination address
	         int portnum = Integer.parseInt(temp[1]);
	         DatagramSocket clientSocket = new DatagramSocket();
	         byte[] outputdata = new byte[1024];
	         
	         String authentication = "request authentication,";
	         outputdata = authentication.getBytes();
	         DatagramPacket sendPacket = new DatagramPacket(outputdata, outputdata.length, destAddr, portnum);
	         clientSocket.send(sendPacket);
	         clientSocket.setSoTimeout(2000);   // set the timeout in millisecounds.
	   
	           while(true){// recieve data until timeout
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
			         outputdata = sentence.getBytes();  
	                  sendPacket = new DatagramPacket(outputdata, sentence.getBytes().length, destAddr, portnum);
	     	          clientSocket.send(sendPacket);
	     	          clientSocket.setSoTimeout(2000);   // set the timeout in millisecounds.
		     	       while(true){
		  	               try {
		  	            	 byte[] inputdataNew = new byte[1024];
		  	                  receivePacket = new DatagramPacket(inputdataNew, inputdataNew.length);
		  	                  clientSocket.receive(receivePacket);
		  	                  modifiedSentence = new String(receivePacket.getData());
		  	                  clientSocket.close();
		  	               }
		  	               catch (SocketTimeoutException e) {
		  	                   // timeout exception.
		  	                   System.out.println("Timeout reached " + e);
		  	                   clientSocket.close();
		  	               }
		  	           }  
	               }
	               catch (SocketTimeoutException e) {
	                   // timeout exception.
	                   System.out.println("Timeout reached " + e);
	                   clientSocket.close();
	               }
	           }
	      } 
	      catch (SocketException e1) {
	   }
	}
}
