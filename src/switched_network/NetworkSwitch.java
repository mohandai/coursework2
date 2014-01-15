/*
 *  (c) K.Bryson, Dept. of Computer Science, UCL (2013)
 */
package switched_network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * Defines a network switch with a number of LAN Ports.
 *
 * @author K. Bryson.
 */
public class NetworkSwitch extends Thread {

    private final SwitchPort[] ports;
    

    private LinkedBlockingQueue<byte[]> packets = new LinkedBlockingQueue<byte[]>();
    private static Lock lock = new ReentrantLock();
    /*
     * Create a Network Switch the specified number of LAN Ports.
     */
    public NetworkSwitch(int numberPorts) {
    	
        ports = new SwitchPort[numberPorts];

        // Create each ports.
        for (int i = 0; i < numberPorts; i++) {
            ports[i] = new SwitchPort(i);
        }

    }


    public int getNumberPorts() {
    	
        return ports.length;
        
    }
    
    
    public SwitchPort getPort(int number) {
    	
    	return ports[number];
    	
    }


    /*
     * Power up the Network Switch so that it starts
     * processing/forwarding network packet traffic.
     */
    public void powerUp() {

    	// YOU NEED TO IMPLEMENT THIS METHOD.
    	this.run();
    }

    // This thread is responsible for delivering any current incoming packets.
    public void run() {
    	// YOU NEED TO IMPLEMENT THIS METHOD.
    	ExecutorService executor = Executors.newCachedThreadPool();
    	for(int i = 0; i < this.getNumberPorts(); i++) {
        	executor.execute(new PortListener(ports[i]));
        	}
    	executor.execute(new PortSender());
    }
    
    private class PortSender implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				sender();
				//Thread.yield();
			}
		}

		private void sender() {
			// TODO Auto-generated method stub
			lock.lock();
			byte[] temp = null;
			boolean success = false;
			if(!packets.isEmpty()) {
				temp = packets.poll();
				success = sendBytes(temp);
			}
			if(!success && temp != null)
				packets.add(temp);
			
			lock.unlock();
		}

		private boolean sendBytes(byte[] temp) {
			// TODO Auto-generated method stub
			byte[] ip = new byte[4];
			for(int i = 0; i < 4; i++){
				ip[i] = temp[i+4];
			}
			
			InetAddress ip_to = null;
			try {
				ip_to = InetAddress.getByAddress(ip);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//InetAddress ip_temp;
			
			int portLength = ports.length;
			for(int i = 0; i < portLength; i++){
				if(match2IPs(ip_to, ports[i].getIPAddress())){
					ports[i].sendToComputer(temp);
					return true;
				}
			}
			
			return false;
		}

		private boolean match2IPs(InetAddress ip_to, InetAddress ipAddress) {
			// TODO Auto-generated method stub
			if(ip_to.getHostAddress().equalsIgnoreCase(ipAddress.getHostAddress()))
				return true;
			return false;
		}
    }
    
    private class PortListener implements Runnable {
    	private final SwitchPort thisPort;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			byte[] b;
			while(true) {
				lock.lock();
				if((b = thisPort.getIncomingPacket()) != null) {
					packets.add(b);
				}
				lock.unlock();
			}
		}
		
		public PortListener(SwitchPort port) {
			this.thisPort = port;
		}
    	
    }

}

