import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class KeyCertificate {

	public iControl.Interfaces m_interfaces = new iControl.Interfaces();

	public void usage() {
		System.out.println("Usage: KeyCertificate hostname username password");
	}

	public void Run(String[] args) throws Exception {
		if (args.length < 3) {
			usage();
		} else {
			String host = args[0];
			String user = args[1];
			String pass = args[2];

			boolean bInit = m_interfaces.initialize(host, user, pass);
			if (bInit) {
				removeVirtualServer();
				addCertificates();
				addClientSSLProfile();
				addVirtualServer();

			}
		}
	}

	private String readFile(String pathname) throws IOException {

		File file = new File(pathname);
		StringBuilder fileContents = new StringBuilder((int) file.length());
		Scanner scanner = new Scanner(file);
		String lineSeparator = System.getProperty("line.separator");

		try {
			while (scanner.hasNextLine()) {
				fileContents.append(scanner.nextLine() + lineSeparator);
			}
			return fileContents.toString();
		} finally {
			scanner.close();
		}
	}

	private void addCertificates() throws Exception {

		String[] certs = new String[1];
		String[] keys = new String[1];
		String[] key_ids = null;
		key_ids = new String[] { "vli_self_server_key" };
		String[] cert_ids = null;
		cert_ids = new String[] { "vli_self_server_cert" };
		iControl.ManagementKeyCertificateBindingStub certstub = m_interfaces
				.getManagementKeyCertificate();
		iControl.ManagementKeyCertificateManagementModeType mode = iControl.ManagementKeyCertificateManagementModeType.MANAGEMENT_MODE_DEFAULT;

		keys[0] = readFile("C:\\Users\\vli\\iControl-java\\vli_self_server.key");
		certs[0] = readFile("C:\\Users\\vli\\iControl-java\\vli_self_server.crt");

		try {
			certstub.key_delete(mode, key_ids);
			certstub.certificate_delete(mode, cert_ids);
		} catch (Exception e) {
			System.out.println("certificate and key not exist");
		}

		certstub.certificate_import_from_pem(mode, cert_ids, certs, true);
		certstub.key_import_from_pem(mode, key_ids, keys, true);
		certstub.certificate_bind(mode, cert_ids, key_ids);
		System.out.println("certificate and key import succeeded!");

	}

	private void addClientSSLProfile() throws Exception {

		String[] profileClientSslName = null;
		profileClientSslName = new String[] { "vli_self_clientssl" };

		iControl.LocalLBProfileClientSSLBindingStub clientsslstub = m_interfaces
				.getLocalLBProfileClientSSL();

		iControl.LocalLBProfileString key = new iControl.LocalLBProfileString(
				"vli_self_server_key.key", false);
		iControl.LocalLBProfileString cert = new iControl.LocalLBProfileString(
				"vli_self_server_cert.crt", false);
		clientsslstub.delete_profile(profileClientSslName);
		clientsslstub.create_v2(profileClientSslName,
				new iControl.LocalLBProfileString[] { key },
				new iControl.LocalLBProfileString[] { cert });
		System.out.println("client ssl profile created!");

	}

	private void removeVirtualServer() throws Exception {
		iControl.LocalLBVirtualServerBindingStub virtualStub = m_interfaces
				.getLocalLBVirtualServer();
		try {
			virtualStub.delete_virtual_server(new String[] { "test" });
		} catch (Exception e) {
			System.out.println("virtual not exist!");
		}
	}

	private void addVirtualServer() throws Exception {

		iControl.LocalLBVirtualServerVirtualServerType res_pool = iControl.LocalLBVirtualServerVirtualServerType.RESOURCE_TYPE_POOL;
		iControl.CommonProtocolType proto_type = iControl.CommonProtocolType.PROTOCOL_TCP;
		iControl.LocalLBProfileContextType profile_type = iControl.LocalLBProfileContextType.PROFILE_CONTEXT_TYPE_ALL;

		iControl.LocalLBVirtualServerVirtualServerProfile vs_profile = new iControl.LocalLBVirtualServerVirtualServerProfile(
				profile_type, "http");
		iControl.LocalLBVirtualServerVirtualServerProfile ssl_profile = new iControl.LocalLBVirtualServerVirtualServerProfile(
				profile_type, "vli_self_clientssl");
		iControl.CommonVirtualServerDefinition virtualDef = new iControl.CommonVirtualServerDefinition(
				"test", "10.1.72.13", 443, proto_type);
		iControl.LocalLBVirtualServerVirtualServerResource virtualRes = new iControl.LocalLBVirtualServerVirtualServerResource(
				res_pool, "http_pool");

		iControl.LocalLBVirtualServerBindingStub virtualStub = m_interfaces
				.getLocalLBVirtualServer();

		virtualStub
				.create(new iControl.CommonVirtualServerDefinition[] { virtualDef },
						new String[] { "255.255.255.255" },
						new iControl.LocalLBVirtualServerVirtualServerResource[] { virtualRes },
						new iControl.LocalLBVirtualServerVirtualServerProfile[][] { {
								vs_profile, ssl_profile } });
		System.out.println("Virtual created!");

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			KeyCertificate kc = new KeyCertificate();
			kc.Run(args);
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
	}

}
