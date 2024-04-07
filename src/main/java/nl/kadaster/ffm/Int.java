package nl.kadaster.ffm;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.concurrent.ThreadLocalRandom;

public class Int {
    private static final int COUNT = 100;

    public static void main(String[] args) {
      MemorySegment numbers = Arena.global().allocateArray(ValueLayout.JAVA_INT, COUNT);
  
      ThreadLocalRandom random = ThreadLocalRandom.current();
      for (int i = 0; i < COUNT; i++) {
        numbers.setAtIndex(ValueLayout.JAVA_INT, i, random.nextInt());
      }
  
      for (int j = 0; j < COUNT; j++) {
        int number = numbers.getAtIndex(ValueLayout.JAVA_INT, j);
        System.out.println(number);
      }
    }
}
