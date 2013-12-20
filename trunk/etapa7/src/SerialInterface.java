
import gnu.io.CommDriver;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;
import java.util.logging.Logger;


public class SerialInterface extends Thread implements SerialPortEventListener  {
	
	

	private InputStream inputStream;
	
	private OutputStream outputStream;
	
	private SerialPort serialPort;
	
	private CommPortIdentifier portId;
	
	
	private SerialReadAction serialReadAction = null;
	
		
	
	/**
	 * Inicializa a interface serial
	 * @param commPort
	 * @param rate
	 */
	public SerialInterface(String commPort,int rate) {
		super ();

		Enumeration portList = CommPortIdentifier.getPortIdentifiers();
		boolean portFound = false;
		while (portList.hasMoreElements()) {
		    portId = (CommPortIdentifier) portList.nextElement();
		    if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
		         if (portId.getName().equals(commPort)) {
			//                if (portId.getName().equals("/dev/term/a")) {
		        	 System.out.println("Porta Serial "+commPort+" encontrada!");
		        	 portFound = true;
		           break;
		        }
		    }
		}
		if (!portFound)
			return;
		//abrindo porta
		try {
			serialPort = (SerialPort) portId.open("OdelotStuff", 2000);
		} catch (PortInUseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//configurando porta
		try {
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
			serialPort.setSerialPortParams(rate,
			        SerialPort.DATABITS_8,
			        SerialPort.STOPBITS_1,
			        SerialPort.PARITY_NONE);
		} catch (TooManyListenersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedCommOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//configurando input e output stream
		try {
			inputStream = serialPort.getInputStream();
			outputStream = serialPort.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			sleep (3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		start ();
		
		
	}
	
	
	/**
	 * Registra o listener de leitura de dados
	 * @param listener
	 */
	public void read (SerialReadAction listener) {
		this.serialReadAction = listener;
	}
	
	/**
	 * Escreve um array de bytes na porta serial
	 * @param data
	 * @throws IOException
	 */
	public void write (byte[] data) throws IOException {
		outputStream.write (data,0,data.length);
	}
	

	/**
	 * Fica escutando a porta serial para ler dados e mandar para a action registrada
	 */
	public void serialEvent(SerialPortEvent event) {
        switch(event.getEventType()) {
        case SerialPortEvent.BI:
        case SerialPortEvent.OE:
        case SerialPortEvent.FE:
        case SerialPortEvent.PE:
        case SerialPortEvent.CD:
        case SerialPortEvent.CTS:
        case SerialPortEvent.DSR:
        case SerialPortEvent.RI:
        case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
            break;
        case SerialPortEvent.DATA_AVAILABLE:
            byte[] readBuffer = new byte[512];
            

            try {
            	int ETX = 03;
                int STX = 02;
                int CR = 13;
                int LF = 10;            	
            	try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int numBytes = 0;
                while (inputStream.available() > 0) {
                    numBytes = inputStream.read(readBuffer);
                    for (int i=0; i< numBytes; i+=1) {
                    	if (this.serialReadAction != null)
                    		this.serialReadAction.read(readBuffer[i]);
                    }
                }
            } catch (IOException e) {
            	e.printStackTrace();
            }
            break;
        }
    }
	
	
	
	
	

}
