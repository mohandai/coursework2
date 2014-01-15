package switched_network;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;

public class Test {

	/**
	 * @param args
	 * @throws UnsupportedEncodingException 
	 * @throws UnknownHostException 
	 */
	
	public static void main(String[] args) throws UnsupportedEncodingException, UnknownHostException {
		
		/*
		// ip - byte

		InetAddress inet = InetAddress.getByName("255.255.255.255");
		byte[] ip = ipToBytes(inet);
		
		// port - byte
		
		// string - byte
		String b = "abcxdfawer013n QW*(_)%";
		System.out.println("send: "+b);
		byte[] send = b.getBytes("UTF-8");
		
		String rec = new String(send, "UTF-8");
		System.out.println("receive: "+rec);*/
		byte[] test = {1,2,3,4,1,3,5,7,-1,0,1,0,97,98,99};
		
		//byte[] test = portToBytes(64453);
		Test t = new Test();
	}
	
    public byte[] recv(int port) {

    	byte[] test = {1,2,3,4,1,3,5,7,-1,0,1,0,97,98,99};
    	byte[] test2 = {1,2,3,4,1,3,5,7,-1,0,0,0,97,98,99};

    	byte[] test3 = {1,2,3,4,1,3,5,7,-1,0,0,55,97,98,99};
    	LinkedBlockingQueue<byte[]> packetsRecv = new LinkedBlockingQueue<byte[]>();
    	
    	packetsRecv.add(test);
    	packetsRecv.add(test2);
    	packetsRecv.add(test3);
		//lock.lock();
    	// YOU NEED TO IMPLEMENT THIS METHOD.
    	int size = packetsRecv.size();
    	
    	for(int i = 0; i < size; i++) {
    		byte[] temp = packetsRecv.poll();
    		int pt = decodeRecvPort(temp);
    		
    		if(port == pt)
    			return temp;
    		else
    			packetsRecv.add(temp);
    	}
    	//lock.unlock();
    	
    	return null;

    }
    
	public Test() {
		byte[] temp = recv(54);
		int port = 0;
		if(temp != null){

			port = decodeRecvPort(temp);
			System.out.println(port);
		}
		
	}
	
    private static int decodeRecvPort(byte[] packet) {
    	byte[] p = new byte[2];
    	
    	p[0] = packet[10];
    	p[1] = packet[11];
    	
    	int port = bytesToPort(p);
    	
    	return port;
    }
    
	private static int bytesToPort(byte[] b) {

        int mask=0xff;
        int temp=0;
        int n=0;
        
        for(int i=0;i<2;i++){
        	n<<=8;
        	temp=b[i]&mask;
        	n |= temp;
           }
        
        return n;
	}
	
	public static byte[] portToBytes(int res) {
		byte[] targets = new byte[2];

		targets[1] = (byte) (res & 0xff);
		targets[0] = (byte) ((res >> 8) & 0xff);

		return targets; 
	}
	
	//##########################################TEST AREA##################################
	
	
	private static byte[] ipToBytes(InetAddress inet) throws UnknownHostException {
		// TODO Auto-generated method stub
		byte[] by = inet.getAddress();
		
		InetAddress in = InetAddress.getByAddress(by);
		System.out.println(in.toString());
		/*byte b = (byte)(i-128);
		System.out.println(b);
		
		int o = (int)b + 128;
		System.out.println(o);*/
		return null;
	}
	


	// binary string
	private static String to16bits(int temp) {
		String binary = Integer.toBinaryString(temp);
		int length = binary.length();
		
		StringBuilder t = new StringBuilder();
		
		if(length < 0 || length > 16)
			return "";
		else {
			for(int i = 0; i < (16 - length); i++) {
				t.append('0');
			}
			t.append(binary);
		}
		return t.toString();
	}

}
