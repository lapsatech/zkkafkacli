package zk;

import java.util.List;
import java.util.function.Consumer;

import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.retry.RetryForever;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class CuratorZookeeperClientDemo {

  public static void main(String[] args) throws Exception {

    try (CuratorZookeeperClient c1 = new CuratorZookeeperClient("localhost:2181", 5000, 1000, null,
        new RetryForever(1000))) {
      c1.start();
      ZooKeeper zkClient = c1.getZooKeeper();

//      zkClient.create(
//          "/my44/zzz",
//          "node".getBytes(),
//          ImmutableList.of(new ACL(Perms.ALL, Ids.ANYONE_ID_UNSAFE)),
//          CreateMode.PERSISTENT);

      Stat s = new Stat();
      String value = new String(zkClient.getData("/my44/zzz", false, s));
      zkClient.setData("/my44/zzz", (value + "1").getBytes(), s.getVersion());

      scanPath(
          zkClient,
          "/",
          (path, data, stat) -> System.out
              .println(path + ":" + (data.length == 0 ? "<empty value>" : new String(data)) + ":v" + stat.getVersion()),
          path -> System.out.println(path));

    }
  }

  public static void scanPath(ZooKeeper zk, String path, TriConsumer<String, byte[], Stat> dataConsumer,
      Consumer<String> pathConsumer) throws InterruptedException, KeeperException {
    scanPathInternal(zk, path, 0, dataConsumer, pathConsumer);
  }

  private static interface TriConsumer<A, B, C> {
    void accept(A a, B b, C c);
  }

  private static void scanPathInternal(ZooKeeper zk, String path, int level,
      TriConsumer<String, byte[], Stat> dataConsumer, Consumer<String> pathConsumer)
      throws  InterruptedException, KeeperException {
    if (level > 10) {
      throw new RuntimeException("Scan went too deep");
    }

    String requestPath;
    String parentPath;
    if (path.equals("/")) {
      requestPath = path;
      parentPath = "";
    } else {
      requestPath = path;
      parentPath = path;
    }
    try {
      Stat s = new Stat();
      byte[] data = zk.getData(requestPath, false, s);
      if (data != null) {
        dataConsumer.accept(requestPath, data, s);
      }
      List<String> children = zk.getChildren(requestPath, false);
      for (String child : children) {
        String childPath = parentPath + "/" + child;
        pathConsumer.accept(childPath);
        scanPathInternal(zk, childPath, level + 1, dataConsumer, pathConsumer);
      }
    } catch (NoNodeException supressed) {
    }
  }

}