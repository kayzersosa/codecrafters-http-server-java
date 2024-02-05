import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class DnsMessage {
    public short id = 1234;
    public short flags = (short)0b10000000_00000000;
    public short qdcount = 1;
    public short ancount = 1;
    public short nscount;
    public short arcount;

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
        buffer.putShort((short)1); // Type = A
        buffer.putShort((short)1); // Class = IN
        return buffer;
    }

    private void writeAnswerSection(ByteBuffer buffer) {
        buffer.put(encodeDomainName("codecrafters.io"));
        buffer.putShort((short)1);           // Type 1 for A record
        buffer.putShort((short)1);           // Class 1 for IN
        buffer.putInt(60);                   // TTL
        buffer.putShort((short)4);           // RDLENGTH
        buffer.put(new byte[] {8, 8, 8, 8}); // RDATA
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
