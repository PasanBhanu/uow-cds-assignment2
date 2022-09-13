package iit.uow.nameserver;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class LoadBalancerClient {

    private EtcdCommunicator etcdClient;

    public LoadBalancerClient(String nameServiceAddress) throws IOException {
        etcdClient = new EtcdCommunicator(nameServiceAddress);
    }

    public static String buildServerDetailsEntry(String serviceAddress, int port, String protocol) {
        return new JSONObject()
                .put("ip", serviceAddress)
                .put("port", Integer.toString(port))
                .put("protocol", protocol)
                .toString();
    }

    public ServiceDetails findService(String serviceName) throws InterruptedException, IOException {
        System.out.println("Searching for details of service :" + serviceName);
        String etcdResponse = etcdClient.get(serviceName);
        ServiceDetails serviceDetails = new ServiceDetails().populate(etcdResponse);
        while (serviceDetails == null) {
            System.out.println("Couldn't find details of service" + serviceName + ", retrying in 3 seconds.");
            Thread.sleep(5000);
            etcdResponse = etcdClient.get(serviceName);
            serviceDetails = new ServiceDetails().populate(etcdResponse);
        }
        return serviceDetails;
    }

    public void registerService(String serviceName, String ipAddress, int port, String protocol) throws IOException {
        String serviceInfoValue = buildServerDetailsEntry(ipAddress, port, protocol);
        etcdClient.put(serviceName + port, serviceInfoValue);

        System.out.println("Service Registered - " + serviceName + " - " + ipAddress + ":" + port);
    }

    public void unregisterService(String serviceName, int port) throws IOException {
        etcdClient.put(serviceName + port, "");
    }

    public class ServiceDetails {
        private String ipAddress;
        private int port;
        private String protocol;

        ServiceDetails populate(String serverResponse) {
            JSONObject serverResponseJSONObject = new JSONObject(serverResponse);
            if (serverResponseJSONObject.has("kvs")) {
                JSONArray servers = new JSONArray();

                JSONArray values = serverResponseJSONObject.getJSONArray("kvs");
                for (Object item : values) {
                    JSONObject firstValue = (JSONObject) item;
                    if (firstValue.has("value")) {
                        String encodedValue = (String) firstValue.get("value");
                        if (encodedValue != null) {
                            servers.put(firstValue);
                        }
                    }
                }

                JSONObject firstValue = (JSONObject) servers.get(0);
                String encodedValue = (String) firstValue.get("value");
                byte[] serverDetailsBytes = Base64.getDecoder().decode(encodedValue.getBytes(StandardCharsets.UTF_8));
                JSONObject serverDetailsJson = new JSONObject(new String(serverDetailsBytes));
                ipAddress = serverDetailsJson.get("ip").toString();
                port = Integer.parseInt(serverDetailsJson.get("port").toString());
                protocol = serverDetailsJson.get("protocol").toString();
                return this;
            } else {
                return null;
            }
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public int getPort() {
            return port;
        }

        public String getProtocol() {
            return protocol;
        }
    }
}
