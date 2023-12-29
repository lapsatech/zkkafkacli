package zk;

import java.util.List;
import java.util.function.Consumer;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.transaction.CuratorOp;
import org.apache.curator.framework.api.transaction.TransactionOp;
import org.apache.curator.retry.RetryForever;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.data.Stat;

public class CuratorFrameworkDemo {

  public static void main(String[] args) throws Exception {

    try (CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("localhost:2181",
        new RetryForever(1000))) {
      curatorFramework.start();

      CuratorFrameworkDemo demo = new CuratorFrameworkDemo(curatorFramework);
//      demo.amendOrCreateKey("/zk/test");
      demo.scanPath(
          "/",
          (path, data, stat) -> System.out
              .println(path + ":" + (data.length == 0 ? "<empty value>" : ByteArrays.toString(data)) + ":v"
                  + stat.getVersion()),
          path -> System.out.println(path));
    }
  }

  private final CuratorFramework curatorFramework;

  public CuratorFrameworkDemo(CuratorFramework curatorFramework) {
    this.curatorFramework = curatorFramework;
  }

  public void deleteKeys(String... keys) throws Exception {
    CuratorOp[] ops = new CuratorOp[keys.length];
    TransactionOp op = curatorFramework.transactionOp();
    for (int i = 0; i < keys.length; i++) {
      ops[i] = op.delete().forPath(keys[i]);
    }
    curatorFramework.transaction().forOperations(ops);
  }

  public void amendOrCreateKey(String path) throws Exception {
    if (curatorFramework.checkExists().forPath(path) == null) {
      curatorFramework.create().forPath(path, ByteArrays.of("test"));
    } else {
      String value = ByteArrays.toString(curatorFramework.getData().forPath(path));
      curatorFramework.setData().forPath(path, ByteArrays.of(value + "1"));
    }
  }

  public void scanPath(String path, TriConsumer<String, byte[], Stat> dataConsumer,
      Consumer<String> pathConsumer) throws Exception {
    scanPathInternal(curatorFramework, path, 0, dataConsumer, pathConsumer);
  }

  public static interface TriConsumer<A, B, C> {
    void accept(A a, B b, C c);
  }

  private static void scanPathInternal(CuratorFramework curatorFramework, String path, int level,
      TriConsumer<String, byte[], Stat> dataConsumer, Consumer<String> pathConsumer)
      throws Exception {
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
      byte[] data = curatorFramework.getData().storingStatIn(s).forPath(requestPath);
      if (data != null) {
        dataConsumer.accept(requestPath, data, s);
      }
      List<String> children = curatorFramework.getChildren().forPath(requestPath);
      for (String child : children) {
        String childPath = parentPath + "/" + child;
        pathConsumer.accept(childPath);
        scanPathInternal(curatorFramework, childPath, level + 1, dataConsumer, pathConsumer);
      }
    } catch (NoNodeException supressed) {
    }
  }

}