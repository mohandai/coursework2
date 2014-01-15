/*
 *  (c) K.Bryson, Dept. of Computer Science, UCL (2013)
 */

package switched_network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Models a computer system which can have a number of
 * applications running on it and provides these applications
 * with operating system services including networking.
 *
 * The Computer provides the operating system services by
 * implementing the ComputerOS interface.
 *
 * The Computer also handles network traffic to/from switch ports
 * by implementing a NetworkCard interface.
 *
 * @author K. Bryson.
 */
public class Computer implements ComputerOS, NetworkCard {

    private final String hostname;
    private final InetAddress ipAddress;

    // This is the switch port which the computer is attached to.
    private SwitchPort port = null;

    private final static int MAX_PORTS = 65536;
    
    private LinkedBlockingQueue<byte[]> packetsRecv = new LinkedBlockingQueue<byte[]>();
    
    private static Lock lock = new ReentrantLock();


    public Computer(String hostname, InetAddress ipAddress) {

        this.hostname = hostname;
        this.ipAddress = ipAddress;


    }

    
    
    /**********************************************************************************
     * The following methods provide the 'Operating System Services'
     * of the computer which are directly used by applications.
     **********************************************************************************/

    /*
     * Get the host name of the computer.
     */
    public String getHostname() {
        return hostname;
    }

    /*
     * Ask the operating system to send this message
     * (byte array as the payload) to the computer with
     * the given IP Address.
     *
     * The message goes from a 'port' on this machine to
     * the specified port on the other machine.
     */
    public void send(byte[] payload, InetAddress ip_address_to, int port_from, int port_to) {
    	
    	//TODO FINISHED
    	// YOU NEED TO IMPLEMENT THIS METHOD.
    	lock.lock();
    	byte[] packet = encode(payload, ip_address_to, port_from, port_to);
    	port.sendToNetwork(packet);
    	lock.unlock();
    }

    private byte[] encode(byte[] payload, InetAddress ip_address_to,int port_from, int port_to) 
    {
    	int length = payload.length + 12;
    	byte[] packet = new byte[length];
    
		byte[] ip_from = this.ipAddress.getAddress();
    	byte[] ip_to = ip_address_to.getAddress();
    	byte[] port_f = portToBytes(port_from);
    	byte[] port_t = portToBytes(port_to);
    	
    	int i = 0;
    	for(int j = 0; j < 4; i++, j++)
    		packet[i] = ip_from[j];
    	for(int j = 0; j < 4; i++, j++)
    		packet[i] = ip_to[j];
    	for(int j = 0; j < 2; i++, j++)
    		packet[i] = port_f[j];
    	for(int j = 0; j < 2; i++, j++)
    		packet[i] = port_t[j];
    	for(int j = 0; j < payload.length; i++, j++)
    		packet[i] = payload[j];
		
		/*for(i = 0; i < packet.length; i++) {
			System.out.print(packet[i] + " ");
		}*/
		
		return packet;
	}
    
	public static byte[] portToBytes(int res) {
		byte[] targets = new byte[2];

		targets[1] = (byte) (res & 0xff);
		targets[0] = (byte) ((res >> 8) & 0xff);

		return targets; 
	}



	/*
     * This asks the operating system to check whether any incoming messages
     * have been received on the given port on this machine.
     *
     * If a message is pending then the 'payload' is returned as a byte array.
     * (i.e. without any UDP/IP header information)
     */
    public byte[] recv(int port) {

    	lock.lock();
    	//TODO FINISHED
    	// YOU NEED TO IMPLEMENT THIS METHOD.
    	int size = packetsRecv.size();
    	
    	for(int i = 0; i < size; i++) {
    		byte[] temp = packetsRecv.poll();
    		int pt = decodeRecvPort(temp);
    		
    		if(port == pt){
    			int length = temp.length - 12;
    			byte[] result = new byte[length];
    			for(int j = 0; j < length; j++) {
    				result[j] = temp[j+12];
    			}
    			lock.unlock();
    			return result;
    		}
    		else
    			packetsRecv.add(temp);
    	}
    	lock.unlock();
    	
    	return null;

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

    /**********************************************************************************
     * The following methods implement the Network Card interface for this computer.
     *
     * They are used by the 'operating system' to send and recv packets.
     ***********************************************************************************/


    /*
     * Get the IP Address of the network card.
     */
    public InetAddress getIPAddress() {
        return ipAddress;
    }

    
    /*
     * This allows a port of a network switch
     * to be attached to this computers network card.
     */
    public void connectPort(SwitchPort port) {
        this.port = port;
    }
    

    /*
     * This method is used by the Network Switch (SwitchPort)
     * to send a packet of data from the network to this computer.
     */
    public void sendToComputer(byte[] packet) {

    	// YOU NEED TO IMPLEMENT THIS METHOD.
    	//TODO FINISHED
        packetsRecv.add(packet);
    }
    

}
