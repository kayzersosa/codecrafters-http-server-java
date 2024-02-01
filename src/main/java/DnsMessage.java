import java.nio.ByteBuffer;

public class DnsMessage {
    private short id = 1234;
    private short flags = (short)0b10000000_00000000;
    private short qdcount;
    private short ancount;
    private short nscount;
    private short arcount;
    
    public DnsMessage() {}
    
    public byte[] array() {
        ByteBuffer buffer = ByteBuffer.allocate(12);
        buffer.putShort(id);
        buffer.putShort(flags);
        buffer.putShort(qdcount);
        buffer.putShort(ancount);
        buffer.putShort(nscount);
        buffer.putShort(arcount);
        return buffer.array();
    }
   
}
