import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class DnsMessage {
    public short id = 1234;
    public short flags;
    // public short qdcount;
    // public short ancount;
    public short nscount;
    public short arcount;
    public Map<String, byte[]> map = new HashMap<>();

    public DnsMessage(byte[] array) {
        ByteBuffer buffer = ByteBuffer.wrap(array);
        // Parse header section
        id = buffer.getShort();
        flags = buffer.getShort();
        int qdcount = buffer.getShort();
        buffer.getShort(); // ancount
        nscount = buffer.getShort();
        arcount = buffer.getShort();
        // Parse question section
        for (int i = 0; i < qdcount; i++) {
            map.put(decodeDomainName(buffer), new byte[] { 8, 8, 8, 8 });
            buffer.getShort(); // Type
            buffer.getShort(); // Class
        }
    }

    private ByteBuffer writeHeader(ByteBuffer buffer) {
        buffer.putShort(id);
        buffer.putShort(flags);
        buffer.putShort((short) map.size());
        buffer.putShort((short) map.size());
        buffer.putShort(nscount);
        buffer.putShort(arcount);
        return buffer;
    }

    private ByteBuffer writeQuestion(ByteBuffer buffer) {
        for (String domain : map.keySet()) {
            buffer.put(encodeDomainName(domain));
            buffer.putShort((short) 1); // Type = A
            buffer.putShort((short) 1); // Class = IN
        }
        return buffer;
    }

    private void writeAnswerSection(ByteBuffer buffer) {
        for (String domain : map.keySet()) {
            buffer.put(encodeDomainName(domain));
            buffer.putShort((short) 1); // Type = A
            buffer.putShort((short) 1); // Class = IN
            buffer.putInt(60); // TTL
            buffer.putShort((short) 4); // Length
            buffer.put(map.get(domain)); // Data
        }
    }

    private byte[] encodeDomainName(String domain) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (String label : domain.split("\\.")) {
            out.write(label.length());
            out.writeBytes(label.getBytes(StandardCharsets.UTF_8));

        }
        out.write(0);
        return out.toByteArray();
    }

    private String decodeDomainName(ByteBuffer buffer) {
        byte b;
        StringJoiner labels = new StringJoiner(".");
        while ((b = buffer.get()) != 0) {
            byte[] dst = new byte[b];
            buffer.get(dst);
            labels.add(new String(dst));
        }
        return labels.toString();
    }

    public byte[] array() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        try {
            writeHeader(byteBuffer);
            writeQuestion(byteBuffer);
            writeAnswerSection(byteBuffer);
        } catch (Exception e) {
            System.err.println("Error DnsMessage array: " + e.getMessage());
        }
        return byteBuffer.array();
    }

}
