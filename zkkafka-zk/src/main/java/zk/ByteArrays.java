package zk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

public class ByteArrays {

  public static byte[] of(int integer) {
    return toByteArrayInternal(dd -> dd.writeInt(integer));
  }

  public static byte[] of(String string) {
    return toByteArrayInternal(dd -> dd.writeChars(string));
  }

  public static int toInt(byte[] array) {
    return intFromByteArrayInternal(array, DataInputStream::readInt);
  }

  public static String toString(byte[] array) {
    return fromByteArrayInternal(array, dd -> {
      StringBuilder sb = new StringBuilder();
      try {
        while (true) {
          sb.append(dd.readChar());
        }
      } catch (EOFException e) {
        //
      }
      return sb.toString();
    });
  }

  @FunctionalInterface
  private static interface Wr {
    void writeToStream(DataOutputStream daos) throws IOException;
  }

  private static byte[] toByteArrayInternal(ByteArrays.Wr dataWriter) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (DataOutputStream dd = new DataOutputStream(baos)) {
      dataWriter.writeToStream(dd);
    } catch (IOException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
    return baos.toByteArray();
  }

  private static interface Rr<T> {
    T readFromStream(DataInputStream dais) throws IOException;
  }

  private static interface RrInt {
    int readFromStream(DataInputStream dais) throws IOException;
  }

  private static <T> T fromByteArrayInternal(byte[] bytes, ByteArrays.Rr<T> dataReader) {
    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
    try (DataInputStream dd = new DataInputStream(bais)) {
      return dataReader.readFromStream(dd);
    } catch (IOException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
  }

  private static int intFromByteArrayInternal(byte[] bytes, ByteArrays.RrInt dataReader) {
    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
    try (DataInputStream dd = new DataInputStream(bais)) {
      return dataReader.readFromStream(dd);
    } catch (IOException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
  }
}