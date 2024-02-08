import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Main {
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible
    // when running tests.
    System.out.println("Logs from your program will appear here!");

    try (DatagramSocket serverSocket = new DatagramSocket(2053)) {
      while (true) {
        final byte[] buf = new byte[512];
        final DatagramPacket packet = new DatagramPacket(buf, buf.length);
        serverSocket.receive(packet);
        System.out.println("Received data");
        // Request

        DnsMessage dnsMessage = new DnsMessage(buf);
        DnsMessage responseDnsMessage = dnsMessage;

        // Set flags
        char[] requestFlags = String.format("%16s", Integer.toBinaryString(responseDnsMessage.flags))
            .replace(' ', '0')
            .toCharArray();
        requestFlags[0] = '1'; // QR
        requestFlags[13] = '1'; // RCODE

        responseDnsMessage.flags = (short) Integer.parseInt(new String(requestFlags), 2);

        byte[] bufResponse = responseDnsMessage.array();

        final DatagramPacket packetResponse = new DatagramPacket(bufResponse, bufResponse.length,
            packet.getSocketAddress());
        serverSocket.send(packetResponse);
      }
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }
}
