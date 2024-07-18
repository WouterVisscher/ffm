package nl.kadaster.ffm;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.concurrent.ThreadLocalRandom;

public class Int {
  private static final int COUNT = 100;

  public static void main(String[] args) {
    MemorySegment numbers = Arena.ofAuto().allocate(4 * COUNT);

    ThreadLocalRandom random = ThreadLocalRandom.current();
    for (int i = 0; i < COUNT; i++) {
      numbers.set(ValueLayout.JAVA_INT, i * 4, random.nextInt());
    }

    for (int j = 0; j < COUNT; j++) {
      int number = numbers.get(ValueLayout.JAVA_INT, j * 4);
      System.out.println(number);
    }
  }
}
