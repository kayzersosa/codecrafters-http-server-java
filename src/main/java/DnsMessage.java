import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class DnsMessage {
    public short id;
    public short flags;
    public List<String> questionList = new ArrayList<>();
    public Map<String, byte[]> map = new HashMap<>();

    public DnsMessage() {
    }

    public DnsMessage(byte[] array) {
        ByteBuffer buffer = ByteBuffer.wrap(array);
        // Parse header section
        id = buffer.getShort();
        flags = buffer.getShort();
        int qdcount = buffer.getShort();
        int ancount = buffer.getShort();
        buffer.getShort(); // nscount
        buffer.getShort(); // arcount

        // Parse question section
        for (int i = 0; i < qdcount; i++) {
            questionList.add(decodeDomainName(buffer));
            buffer.getShort(); // Type
            buffer.getShort(); // Class
        }

        // Parse answer section
        for (int i = 0; i < ancount; i++) {
            String domain = decodeDomainName(buffer);
            buffer.getShort(); // Type = A
            buffer.getShort(); // Class = IN
            buffer.getInt(); // TTL
            buffer.getShort(); // Length
            byte[] ip = new byte[4];
            buffer.get(ip); // Data
            map.put(domain, ip);
        }
    }

    private ByteBuffer writeHeader(ByteBuffer buffer) {
        buffer.putShort(id);
        buffer.putShort(flags);
        buffer.putShort((short) questionList.size());
        buffer.putShort((short) map.size());
        buffer.putShort((short) 0); // nscount
        buffer.putShort((short) 0); // arcount
        return buffer;
    }

    private ByteBuffer writeQuestion(ByteBuffer buffer) {
        for (String domain : questionList) {
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
        byte labelL;
        StringJoiner labels = new StringJoiner(".");
        boolean compress = false;
        int index = 0;
        while ((labelL = buffer.get()) != 0) {
            if ((labelL & 0xC0) == 0xC0) {
                compress = true;
                int offset = ((labelL & 0x3F) << 8) | (buffer.get() & 0xFF);
                index = buffer.position();
                buffer.position(offset);
            } else {
                byte[] label = new byte[labelL];
                buffer.get(label);
                labels.add(new String(label));
            }
        }

        if (compress) {
            buffer.position(index);
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

    public DnsMessage clone() {
        DnsMessage clone = new DnsMessage();
        clone.id = id;
        clone.flags = flags;
        clone.questionList.addAll(questionList);
        clone.map.putAll(map);
        return clone;
    }
}
