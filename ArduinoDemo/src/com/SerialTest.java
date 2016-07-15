package com;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 
import java.util.Enumeration;


public class SerialTest implements SerialPortEventListener {
	SerialPort serialPort;
        /** The port we're normally going to use. */
	private static final String PORT_NAMES[] = { 
			"/dev/tty.usbserial-A9007UX1", // Mac OS X
                        "/dev/ttyACM0", // Raspberry Pi
			"/dev/ttyUSB0", // Linux
			"COM3", // Windows
	};
	/**
	* A BufferedReader which will be fed by a InputStreamReader 
	* converting the bytes into characters 
	* making the displayed results codepage independent
	*/
	
	public String inputLine;
	public static final int buffers=30;
	public static volatile float humi[]=new float[buffers];
	public static volatile float temp[]=new float[buffers];
	public static volatile float pres[]=new float[buffers];
	public static volatile float light[]=new float[buffers];
	public static volatile float dust[]=new float[buffers];
	public static volatile float CO[]=new float[buffers];
	
	public static String plotdata1="['Time', 'Humidity', 'Temperature','Light']";
	public static String plotdata2="['Pressue','Dust']";
	
	public static boolean all_inputs_updated=false;
	private boolean inputs_taken[]={false,false,false,false,false,false};
	
	private BufferedReader input;
	/** The output stream to the port */
	private OutputStream output;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 9600;

	public void initialize() {
                // the next line is for Raspberry Pi and 
                // gets us into the while loop and was suggested here was suggested http://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186
                //System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");

		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		//First, Find an instance of serial port as set in PORT_NAMES.
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}
		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		}

		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// open the streams
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			output = serialPort.getOutputStream();

			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
			System.out.println("Port closed");
		}
	}

	public void trigger() throws IOException
	{
		//output.write('R');
	}

	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				inputLine=input.readLine();
				System.out.println(inputLine);
				if(inputLine.length()>=1)
				{
				String s=inputLine.substring(0,inputLine.length()-1);
				  float val=Float.parseFloat(s.substring(1,s.length()));
				  char ch=s.charAt(0);
				  
				  switch(ch)
				  {
				    case 'H': 	for(int i=0;i<buffers-1;i++)
				    				humi[i]=humi[i+1];
				    			humi[buffers-1]=val;inputs_taken[0]=true;
				              break;
				    case 'T': for(int i=0;i<buffers-1;i++)
	    							temp[i]=temp[i+1];
				    			temp[buffers-1]=val;inputs_taken[1]=true;
				    			break;
				    case 'P': 	for(int i=0;i<buffers-1;i++)
	    							pres[i]=pres[i+1];
				    			pres[buffers-1]=val;inputs_taken[2]=true;
				    			break;
				    case 'L': for(int i=0;i<buffers-1;i++)
	    						light[i]=light[i+1];
				    			light[buffers-1]=val;inputs_taken[3]=true;
				    			break;
				    case 'D': for(int i=0;i<buffers-1;i++)
	    						dust[i]=dust[i+1];
				    			dust[buffers-1]=val;inputs_taken[4]=true;
				    			break;
				    case 'C': for(int i=0;i<buffers-1;i++)
	    							CO[i]=CO[i+1];
				    			CO[buffers-1]=val;inputs_taken[5]=true;
				    			break;
				  }
				  all_inputs_updated=true;
				  for(int i=0;i<6;i++)
					  if(inputs_taken[i]==false)
					  {
						  all_inputs_updated=false;
						  break;
					  }
				  if(all_inputs_updated)
				  {
					  plotdata1="['Time(s)', 'Humidity (%)', 'Temperature (c) ','Light (vol) ']";
					  plotdata2="['Time(s)','Pressue (pa) ','Dust (pcs/0.01cf) ']";
					  for(int i=0;i<buffers;i++)
					 {
						  plotdata1+= ",['"+i+"',"+humi[i]+","+temp[i]+","+light[i]+"]";
						  plotdata2+= ",['"+i+"',"+pres[i]+","+dust[i]+"]";
					 }
					 System.out.println("All inputs updated");
					 // close();
				  }
				}
			} catch (Exception e) {
				all_inputs_updated=true;
				e.printStackTrace();
			}
		}
		// Ignore all the other eventTypes, but you should consider the other ones.
	}
	
	public void start() throws Exception {
		SerialTest main = new SerialTest();
		  
		main.initialize();
//		Thread t=new Thread() {
//			public void run() {
//				//the following line will keep this app alive for 1000 seconds,
//				//waiting for events to occur and responding to them (printing incoming messages to console).
//				try {Thread.sleep(1000000);} catch (InterruptedException ie) {}
//			}
//		};
//		t.start();
		System.out.println("Started");
		trigger();
	}
}