import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;


public class IqpTcpClient {

	private static String DEFAULT_DEVICE_ID = "device1";

	public class Config{
		private String dstHost="localhost";
		private int dstPort=8123;
		private String TENANT = "dk";
		private String username="dk";
		private String password="iqp12345";
		private String deviceId = DEFAULT_DEVICE_ID;
		private int metricStartValue = 100;
	    private boolean sendLogin = true;
	    private boolean sendPublish = true;
	    private boolean sendSubscribe = true;
	    private int numberOfPublishMessages = 1;
	    private int delayBeweenPublish = 100;
	    private String instanceId = "Instance123";
		
		public String getDstHost() {
			return dstHost;
		}

		public void setDstHost(String dstHost) {
			this.dstHost = dstHost;
		}

		public int getDstPort() {
			return dstPort;
		}

		public void setDstPort(int dstPort) {
			this.dstPort = dstPort;
		}

		public String getTENANT() {
			return TENANT;
		}

		public void setTENANT(String tENANT) {
			TENANT = tENANT;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getDeviceId() {
			return deviceId;
		}

		public void setDeviceId(String deviceId) {
			this.deviceId = deviceId;
		}

		public int getMetricStartValue() {
			return metricStartValue;
		}

		public int getMetricStartValueAndIncrement() {
			return metricStartValue++;
		}
		
		public void setMetricStartValue(int metricStartValue) {
			this.metricStartValue = metricStartValue;
		}

		public boolean isSendLogin() {
			return sendLogin;
		}

		public void setSendLogin(boolean sendLogin) {
			this.sendLogin = sendLogin;
		}

		public boolean isSendPublish() {
			return sendPublish;
		}

		public void setSendPublish(boolean sendPublish) {
			this.sendPublish = sendPublish;
		}

		public boolean isSendSubscribe() {
			return sendSubscribe;
		}

		public void setSendSubscribe(boolean sendSubscribe) {
			this.sendSubscribe = sendSubscribe;
		}

		public int getNumberOfPublishMessages() {
			return numberOfPublishMessages;
		}

		public void setNumberOfPublishMessages(int numberOfPublishMessages) {
			this.numberOfPublishMessages = numberOfPublishMessages;
		}

		public int getDelayBeweenPublish() {
			return delayBeweenPublish;
		}

		public void setDelayBeweenPublish(int delayBeweenPublish) {
			this.delayBeweenPublish = delayBeweenPublish;
		}

		public String getInstanceId() {
			return instanceId;
		}

		public void setInstanceId(String instanceId) {
			this.instanceId = instanceId;
		}    
	}
	
	private Config config = new Config();
	public void setConfig(Config config){
		this.config = config;
	}
	public Config getConfig(){
		return config;
	}
	
	private static final String CHANNEL_IQP = "IQP_";
	//Note: the +00:00 (or similar), relate to time zone is a must! without it, the server parse time as if it is with MSECS!
	private static final String DATE_FORMAT_NO_MSEC = "yyyy-MM-dd'T'HH:mm:ss+00:00";
	//private static final String DATE_FORMAT_NO_MSEC = "yyyy/MM/dd'T'HH:mm:ss";
	private static final String DATE_FORMAT_WITH_MSEC = "yyyy/MM/dd'T'HH:mm:ss.XXX";
	// Connection details
	//private String host="localhost";

	//private static final String CLOUD_TIMESTAMP = "\"2016-03-08T09:08:30.123+09:00\""; //with msec, not supported in 2.8 and before
   //private static final String CLOUD_TIMESTAMP = "\"2016-03-08T09:08:30+09:00\""; //without msec
	private static final String CLOUD_TIMESTAMP = "2016-03-12T09:18:58+11:00"; //without msec

	
	// Sample Location Values
	private String latitude="35.587562";
	private String longitude="139.668916";
	private String altitude="1000";

	Socket clientSocket = null;
    PrintWriter out = null;
    BufferedReader in = null;

    // Login String
	private final String LOGIN="{\"handshake\":{\"stage\":\"login\"}, \"login\": {\"username\":\"%s\",\"password\":\"%s\"}}" ;
	private final String TCP_PAYLOAD = "{\"handshake\":{\"stage\":\"publish\"},"
			+ "\"messages\":[{\"dc\":\"" + CHANNEL_IQP + config.getTENANT() + "/"+ config.getDeviceId() +"/"+config.getInstanceId()+"\",\"cloud\":\"" + CLOUD_TIMESTAMP + "\","
			+ "\"data\":[{\"time\":\"" + CLOUD_TIMESTAMP + "\",\"alt\":\"11\",\"mets\":["
			+ "{\"met\":\"metric1\",\"val\":\"91\"},"
			+ "{\"met\":\"metric2\",\"val\":\"92\"},"
			+ "{\"met\":\"metric3\",\"val\":\"93\"},"
			+ "{\"met\":\"metric4\",\"val\":\"94\"},"
			+ "{\"met\":\"metric5\",\"val\":\"95\"},"
			+ "{\"met\":\"zzz\",\"val\":\"1\"}]}]}]}";

	/*private final String TCP_PAYLOAD = "{\"handshake\":{\"stage\":\"publish\"},"
			+ "\"messages\":[{\"dc\":\"IQP_IQPIOT/ASGW_000000/ASSns_000000\",\"cloud\":\"2016-03-08T09:08:30.123+09:00\","
			+ "\"data\":[{\"time\":\"2016-03-08T09:08:30.123+09:00\",\"alt\":\"11\",\"mets\":["
			+ "{\"met\":\"自動運転時間_累計\",\"val\":\"73\"},"
			+ "{\"met\":\"自動運転時間_当日\",\"val\":\"2\"},"
			+ "{\"met\":\"総脱貝成功数_累計\",\"val\":\"187\"},"
			+ "{\"met\":\"総脱貝成功数_当日\",\"val\":\"1\"},"
			+ "{\"met\":\"総脱貝失敗数_累計\",\"val\":\"24\"},"
			+ "{\"met\":\"総脱貝失敗数_当日\",\"val\":\"4\"},"
			+ "{\"met\":\"真空ポンプ1駆動時間_累計\",\"val\":\"79\"},"
			+ "{\"met\":\"レーン8脱貝失敗数_累計\",\"val\":\"68\"},"
			+ "{\"met\":\"レーン8脱貝失敗数_当日\",\"val\":\"1\"}]}]}]}";*/

	// Subscribe String
	private final String SUBSCRIBE_MESSAGE="{\"handshake\":{\"stage\":\"subscribe\"}, \"payload\":{\"dc\":\"%s\",\"request_type\":\"subscribe\"}}";

	/**
     * Open a connection to the IQP TCP server
     */
	
	String generatePublishMessage(String deviceId, boolean fixedTimestamp, int baseMetricValue){
		String timeStamp = CLOUD_TIMESTAMP;
		if (!fixedTimestamp){
			timeStamp = getCurrentDateNoMsec();
		}
		
		// Data Channel: Cloud_ID/Device_ID/Instance_Name
		String data_channel= CHANNEL_IQP + config.getTENANT() + "/" + deviceId + "/" + config.getInstanceId();

		String message = 
					"{\"handshake\":{\"stage\":\"publish\"},"
						+ "\"messages\":[{\"dc\":\"" + data_channel + "\",\"cloud\":\"" + timeStamp + "\","
						+ "\"data\":[{\"time\":\"" + timeStamp + "\",\"alt\":\"11\",\"mets\":["
						+ "{\"met\":\"metric1\",\"val\":\"" + (config.getMetricStartValueAndIncrement()) + "\"},"
						+ "{\"met\":\"metric2\",\"val\":\"" + (config.getMetricStartValueAndIncrement()) + "\"},"
						+ "{\"met\":\"metric3\",\"val\":\"" + (config.getMetricStartValueAndIncrement()) + "\"},"
						+ "{\"met\":\"metric4\",\"val\":\"" + (config.getMetricStartValueAndIncrement()) + "\"},"
						+ "{\"met\":\"metric5\",\"val\":\"" + (config.getMetricStartValueAndIncrement()) + "\"},"
						+ "{\"met\":\"zzz\",\"val\":\"10\"}]}]}]}";
//				"{\"handshake\":{\"stage\":\"publish\"},"
//
//				+ "\"messages\":[{\"" + "IQP_" + TENANT + "/" + DEVICE_ID + "/" + INSTANCE_ID + "\",\"cloud\":\"" + timeStamp + "\","
//						+ "\"data\":[{\"time\":\"" + timeStamp + "\",\"alt\":\"11\",\"mets\":["
//						+ "{\"met\":\"metric1\",\"val\":\"61\"},"
//						+ "{\"met\":\"metric2\",\"val\":\"62\"},"
//						+ "{\"met\":\"metric3\",\"val\":\"63\"},"
//						+ "{\"met\":\"metric4\",\"val\":\"64\"},"
//						+ "{\"met\":\"metric5\",\"val\":\"65\"},"
//						+ "{\"met\":\"zzz\",\"val\":\"1\"}" 
//						+ "]}]}]}";

		return message;
	}
	
	public void startClient(){
        try {
        	clientSocket = new Socket(config.getDstHost(), config.getDstPort());
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
            		clientSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:"+config.getDstHost());
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                               + "the connection to: "+config.getDstHost()+":"+config.getDstPort());
            e.printStackTrace();
            System.exit(1);
        }

	}
	
	/**
     * Login
     */
	
	public String login(){
        String formattedString = String.format(LOGIN, config.getUsername(), config.getPassword()); 

		System.out.println("Client: login:"+formattedString);
		//send data to socket
		this.out.println(formattedString);
		//read response
		try{
			String line=this.in.readLine();
			return "Client received: "+line;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}		

	}

	/**
	 * Publish the payload
	 */

	public String publishPayload(){

		// Format TCP_PAYLOAD
		boolean fixedTimestamp = false;
		for (int i = 1 ; i <= config.getNumberOfPublishMessages() ; i++){			
			String formattedString = generatePublishMessage(config.getDeviceId(), fixedTimestamp, 123);
			//String formattedString = String.format(TCP_PAYLOAD);
			System.out.println("Client: publishing: "+formattedString);
			this.out.println(formattedString);
			try {
				Thread.sleep(config.getDelayBeweenPublish());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//this.out.println("\n\n");
/*
 * READ the results after sending all messages
		try {
			String line=this.in.readLine();
			return line;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
*/
		return "tbd read results";
	}

	/**
	 * Subscribe to receive commands
	 */
	
	public String sendSubsribe(){
		// Data Channel Cloud_ID/Device_ID/Instance_Name
		String data_channel=CHANNEL_IQP + config.getTENANT() + "/" + config.getDeviceId() + "/" + config.getInstanceId();
		String formattedString = String.format(SUBSCRIBE_MESSAGE, data_channel); 
		System.out.println("Client: writing: "+formattedString);
		this.out.println(formattedString);
		//this.out.println("\n\n");
		try {
			String line=this.in.readLine();
			return line;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}

	}

	/**
	 * close the socket
	 * @return
	 */
	
	public boolean close(){
		out.close();
		try {
			in.close();
		
			clientSocket.close();
		} catch (IOException e) {
			System.out.println("client error: "+e.getMessage());
			return false;
		}
		System.out.println("client: closed connection");
		return true;
		
	}	

	/**
	 * helper method to get current timestamp to add to JSON message
	 * @param increment
	 * @return
	 */
	
	public String getDate(long increment){
		increment=increment*1000;
		Format formatter = new SimpleDateFormat("yyyy/MM/dd'T'HH:mm:ss.SSSXXX");
		String s = formatter.format(new Timestamp(new Date().getTime()+increment));
		return s;
	}	

	public static String getCurrentDateNoMsec(){
		Format formatter = new SimpleDateFormat(DATE_FORMAT_NO_MSEC);
		String s = formatter.format(new Timestamp(new Date().getTime()));
		return s;
	}	

	/**
	 * main function
	 * @param args
	 */
	
//	public static void main(String[] args) {
//		System.out.println("start TCP simulator");
//
//		IqpTcpClient client = new IqpTcpClient();
//
//		//call open connection
//		client.startClient();
//
//		String response;
//		
//		//call login method
//		if (sendLogin){
//			response=client.login();
//			System.out.println("Client: Login response: "+response+"\n");
//		}
//		
//		//call publish method to publish payload to the IQP server
//		if (sendPublish){
//			response=client.publishPayload();
//			System.out.println("Client: Publish response: "+response+"\n");
//		}
//		
//		//call subscribe method to subscribe to command messages from the IQP server
//		if (sendSubscribe){
//			response=client.sendSubsribe();
//			System.out.println("Client: Subscribe response: "+response+"\n");
//		}
//		//close socket
//		client.close();
//		 
//	}

	public void activate(){
		startClient();
		
		String response;
		//call login method
		if (config.isSendLogin()){
			response = login();
			System.out.println("Client: Login response: "+response+"\n");
		}
		
		//call publish method to publish payload to the IQP server
		if (config.isSendPublish()){
			response = publishPayload();
			System.out.println("Client: Publish response: "+response+"\n");
		}
		
		//call subscribe method to subscribe to command messages from the IQP server
		if (config.isSendSubscribe()){
			response = sendSubsribe();
			System.out.println("Client: Subscribe response: "+response+"\n");
		}
		//close socket
		close();
		
	}
}
