import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class DnsMessage {
    private short id = 1234;
    private short flags = (short) 0b10000000_00000000;
    private short qdcount = 1;
    private short ancount;
    private short nscount;
    private short arcount;

    public DnsMessage() {
    }

    private ByteBuffer writeHeader(ByteBuffer buffer) {
        buffer.putShort(id);
        buffer.putShort(flags);
        buffer.putShort(qdcount);
        buffer.putShort(ancount);
        buffer.putShort(nscount);
        buffer.putShort(arcount);
        return buffer;
    }

    private ByteBuffer writeQuestion(ByteBuffer buffer) {
        buffer.put(encodeDomainName("codecrafters.io"));
        buffer.putShort((short) 1);// Type = A
        buffer.putShort((short) 1);// Class = IN
        return buffer;
    }

    private byte[] encodeDomainName(String domain) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (String label : domain.split("\\.")) {
            out.write(label.length());
            out.writeBytes(label.getBytes());
        }
        out.write(0);
        return out.toByteArray();
    }

    public byte[] array() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        writeHeader(byteBuffer);
        writeQuestion(byteBuffer);
        return byteBuffer.array();
    }

}
