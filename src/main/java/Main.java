import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;

public class Main {
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible
    // when running tests.
    System.out.println("Logs from your program will appear here!");
    String resolverIP = args[1].split(":")[0];
    int resolverPort = Integer.parseInt(args[1].split(":")[1]);
    SocketAddress resolver = new InetSocketAddress(resolverIP, resolverPort);

    try (DatagramSocket serverSocket = new DatagramSocket(2053)) {
      while (true) {
        final byte[] buf = new byte[512];
        final DatagramPacket packet = new DatagramPacket(buf, buf.length);
        serverSocket.receive(packet);
        System.out.println("Received data");
        // Request
        DnsMessage dnsMessage = new DnsMessage(buf);

        for (String qd : dnsMessage.questionList) {
          DnsMessage forward = dnsMessage.clone();
          forward.questionList= new ArrayList<>();
          forward.questionList.add(qd);

          byte[] buffer = forward.array();
          DatagramPacket forwardPacket = new DatagramPacket(buffer, buffer.length, resolver);
          serverSocket.send(forwardPacket);

          buffer = new byte[512];
          forwardPacket = new DatagramPacket(buffer, buffer.length);
          serverSocket.receive(forwardPacket);
          forward = new DnsMessage(buffer);

          for (String an : forward.map.keySet()) {
            dnsMessage.map.put(an, forward.map.get(an));
          }

        }

        // Set flags

        char[] requestFlags = String.format("%16s", Integer.toBinaryString(dnsMessage.flags))
        .replace(' ', '0')
        .toCharArray();

        requestFlags[0] = '1';  // QR
        requestFlags[13] = '1'; // RCODE
        dnsMessage.flags = (short)Integer.parseInt(new String(requestFlags), 2);


        byte[] bufResponse = dnsMessage.array();

        final DatagramPacket packetResponse = new DatagramPacket(bufResponse, bufResponse.length,
            packet.getSocketAddress());
        serverSocket.send(packetResponse);
      }
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }
}
