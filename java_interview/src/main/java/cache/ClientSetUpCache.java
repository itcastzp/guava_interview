package cache;

import java.util.HashMap;
import java.util.Map;

public class ClientSetUpCache {
    private static final ClientSetup SETUP = new ClientSetup();
    private final Map<String, ClientSetup> caches = new HashMap<>();

    ClientSetup getCurrentUserCache(String user) {
        ClientSetup setup = caches.get(user);
        if (setup == null) {
            //具体业务场景，如果另一种可以采取通过客户文件加载的方式来讲文件缓存载入到内存缓存中实现运行态的缓存
            /**
             * <p>
             *     private static File getClientSetupFile(){
             *         String root = ClientToolKit.getNCCodeBase();
             *         StringBuffer sb = new StringBuffer("clientsetup/");
             *         WorkbenchEnvironment env = WorkbenchEnvironment.getInstance();
             *         String bcCode = env.getLoginBusiCenter().getCode();
             *         String groupID = env.getGroupVO() == null ? "0001" : env.getGroupVO().getPk_group();
             *         String userId =env.getLoginUser().getPrimaryKey();
             *         sb.append(bcCode).append("/cs").append(groupID).append(userId).append(".csf");
             *         return new File(root, sb.toString());
             *     }
             * </p>
             *
             *
             */
            //ClientSetup client = initFromFile(user){
            //   File file= getClientSetupFile();
            //    file!=null->return getObjectFromSerializeFile();
            //     file==null ->return new ClientSetup();
            //
            // };
            ClientSetup clientSetup = new ClientSetup();

            caches.put(user, clientSetup);
            setup = clientSetup;
        }
        return setup;

    }


}
