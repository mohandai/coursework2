/*
 *  (c) K.Bryson, Dept. of Computer Science, UCL (2013)
 */

package switched_network;

import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * This represents a 'Switch Port' on the front of a Network Switch.
 *
 * For instance, a Network Switch may have 4 ports which are
 * Ethernet sockets at the front of the Network Switch.
 * Each Ethernet socket is physically connected to the
 * network card of a computer using an Ethernet cable.
 *
 * @author K. Bryson.
 */
public class SwitchPort {

    private final int portNumber;
    private NetworkCard connectedNetworkCard = null;
    private InetAddress ipAddress = null;
    
    private LinkedBlockingQueue<byte[]> packets = new LinkedBlockingQueue<byte[]>();
    private static Lock lock = new ReentrantLock();

    public SwitchPort(int number) {
        portNumber = number;
    }
    
    public int getNumber() {
    	return portNumber;
    }
    
    public InetAddress getIPAddress() {
    	return ipAddress;
    }

    /*
     * This method is USED BY THE COMPUTER to send a packet of
     * data to this Port on the Switch.
     *
     * The packet of data should follow the simplified
     * header format as specified in the coursework descriptions.
     */
    public void sendToNetwork(byte[] packet) {

    	// YOU NEED TO IMPLEMENT THIS METHOD.
    	//TODO FINISHED
    	packets.add(packet);
    }


    public byte[] getIncomingPacket() {

    	// TODO FINISHED
    	// YOU NEED TO IMPLEMENT THIS METHOD.
    	lock.lock();
    	if(!packets.isEmpty()) {
    		byte[] temp = packets.poll();
    		lock.unlock();
    		return temp;
    	}
    	
    	lock.unlock();
    	return null;
        
    }

    void sendToComputer(byte[] packet) {

    	// YOU NEED TO IMPLEMENT THIS METHOD.
    	// TODO FINISHED
    	connectedNetworkCard.sendToComputer(packet);
    }


    void connectNetworkCard(NetworkCard networkCard) {

        connectedNetworkCard = networkCard;
        ipAddress = networkCard.getIPAddress();

    }

}
