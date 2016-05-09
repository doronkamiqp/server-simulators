
public class TesterMain {
	public static void main(String[] args) {
		System.out.println("IQP TCP simulator");
		String deviceArg = null;
		int maxDevices = 1;
		int metricStartValue = 300;
		
		if (args.length > 0){
			String parameter1 = args[0];
			if (parameter1.isEmpty() || parameter1.equalsIgnoreCase("help") || parameter1.equalsIgnoreCase("/?")){
				System.out.println("USAGE: TesterMain [<deviceId> <metricStartValue>]");
				return;
			}
			deviceArg = args[0];
			System.out.println("sending messages for device: " + deviceArg);
			maxDevices = 1;
			
			if (args.length > 1){
				metricStartValue = Integer.valueOf(args[1]);
				System.out.println("metrics start value: " + metricStartValue);
			}
		}
		int maxMetrics = 5; //tbd...
		boolean cont = true;
		for (int loop = 0; cont; loop++){
			for (int i = 1; i <= maxDevices; i++){
				IqpTcpClient client = new IqpTcpClient();
				String deviceId = deviceArg!=null ? deviceArg : "device"+i;
				client.getConfig().setDeviceId(deviceId);
				int currentMetricStartValue = metricStartValue + loop*maxDevices*maxMetrics + i*5;
				client.getConfig().setMetricStartValue(currentMetricStartValue);
				client.activate();		
			}
		}
	}

}
