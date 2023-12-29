package kafka;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class MapBuilder<K> {

  private final TreeMap<K, K> map = new TreeMap<>();
  private final AtomicReference<K> key = new AtomicReference<>();

  public <T extends Map<K, K>> T build(Supplier<T> mapSupplier) {
    T res = mapSupplier.get();
    key.getAndUpdate(prevValue -> {
      if (prevValue != null) {
        throw new IllegalStateException(
            "Map entry is incomplete. Key " + prevValue + " is accepted but no following value is given");
      } else {
        res.putAll(map);
      }
      return prevValue;
    });
    return res;
  }

  public MapBuilder<K> accept(K t) {
    key.getAndUpdate(prevValue -> {
      if (prevValue == null) {
        return t;
      } else {
        map.put(prevValue, t);
        return null;
      }
    });
    return this;
  }

}
