import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/ControlarLuz")
public class ControlarLuz extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

    public ControlarLuz() {
        super();
        
        
    }
    
    public SerialInterface getSerialInterface (HttpServletRequest request) {
    	//colocando o controlador de comunicacao serial no contexto da aplicação
        Object sc = request.getServletContext().getAttribute("serialComm");
        if (sc == null) {
        	SerialInterface si = new SerialInterface ("COM6",9600);
        	si.read(new SerialReadAction() {

        		public void read(byte b) {
					System.out.print((char)b);
					
				}
			});
        	request.getServletContext().setAttribute("serialComm", si);
        	return si;
        }
        return (SerialInterface) sc;
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost (request,response);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SerialInterface si =  getSerialInterface(request);
		//pega o comando
		String comando = request.getParameter("comando");
		//envia o comando
		if (comando!=null)
			si.write(comando.getBytes());
		//redireciona para a pagina de controle
		response.sendRedirect("index.html");
	}

}
