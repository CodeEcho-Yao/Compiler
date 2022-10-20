package Demo53.backend;

import java.util.Map;

public class DirectMemValueSetter implements IValueSetter {
    private int memAddr = 0;
    
    public DirectMemValueSetter(int memAddr) {
    	this.memAddr = memAddr;
    }
	
	@Override
	public void setValue(Object obj) throws Exception {
		MemoryHeap memHeap = MemoryHeap.getInstance();
		Map.Entry<Integer, byte[]> entry = memHeap.getMem(memAddr);
		byte[] content = entry.getValue();
		int offset = memAddr - entry.getKey();
		Integer i = (Integer)obj;
		content[offset] = (byte)(i & 0xFF);
	}

}
