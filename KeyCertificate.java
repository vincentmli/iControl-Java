import java.io.*;
import java.util.Scanner;

public class KeyCertificate {

	  public iControl.Interfaces m_interfaces = new iControl.Interfaces();

	  public void usage()
	  {
	    System.out.println("Usage: KeyCertificate hostname username password");
	  }
	  
	  public void Run(String [] args) throws Exception
	  {
	    if ( args.length < 3 )
	    {
	      usage();
	    }
	    else
	    {
	      String host = args[0];
	      String user = args[1];
	      String pass = args[2];
	      
	      boolean bInit = m_interfaces.initialize(host, user, pass);
	      if ( bInit )
	      {
	        addCertificates();
	        
	      }
	    }
	  }
	  
	    private String readFile(String pathname) throws IOException {

	        File file = new File(pathname);
	        StringBuilder fileContents = new StringBuilder((int)file.length());
	        Scanner scanner = new Scanner(file);
	        String lineSeparator = System.getProperty("line.separator");

	        try {
	            while(scanner.hasNextLine()) {        
	                fileContents.append(scanner.nextLine() + lineSeparator);
	            }
	            return fileContents.toString();
	        } finally {
	            scanner.close();
	        }
	    }
	    
	  
   
    private void addCertificates()
            throws Exception
    {
 

        String[] certs = new String[1];
        String[] keys = new String[1];
        String[] key_ids = null;
        key_ids = new String[]{"vli_self_server_key"};
        String[] cert_ids = null;
        cert_ids = new String[]{"vli_self_server_cert"};
        iControl.ManagementKeyCertificateBindingStub  certstub = m_interfaces.getManagementKeyCertificate();
        iControl.ManagementKeyCertificateManagementModeType mode = iControl.ManagementKeyCertificateManagementModeType.MANAGEMENT_MODE_DEFAULT;

    	keys[0] = readFile("C:\\Users\\vli\\iControl-java\\vli_self_server.key");
        certs[0] =  readFile("C:\\Users\\vli\\iControl-java\\vli_self_server.crt");
            

        certstub.key_delete(mode, key_ids);
    	certstub.certificate_delete(mode, cert_ids);

    	certstub.certificate_import_from_pem(mode, cert_ids, certs, true);
    	certstub.key_import_from_pem(mode, key_ids, keys, true);
    	certstub.certificate_bind(mode, cert_ids, key_ids);
    }

  

	/**
	 * @param args
	 */
    public static void main(String[] args) {
        try
        {
          KeyCertificate kc = new KeyCertificate();
          kc.Run(args);
        }
        catch(Exception ex)
        {
          ex.printStackTrace(System.out);
        }
      }

}
