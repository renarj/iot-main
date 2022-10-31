package com.oberasoftware.home.hue.actions;

import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Renze de Vries
 */
public class GroupHelper {
    private static final Logger LOG = getLogger(GroupHelper.class);

    private GroupHelper() {

    }

//    public static PHGroup getOrCreateGroup(GroupItem groupItem, PHBridge bridge, List<DeviceItem> deviceItems) {
//        List<PHGroup> groups = bridge.getResourceCache().getAllGroups();
//
//        Set<String> deviceIds = deviceItems.stream().map(DeviceItem::getDeviceId).collect(Collectors.toSet());
//
//        Optional<PHGroup> existingGroup = groups.stream().filter(g -> deviceIds.equals(new HashSet<>(g.getLightIdentifiers()))).findFirst();
//        if(existingGroup.isPresent()) {
//            LOG.debug("Identified an existing hue group, using this: {}", existingGroup.get().getIdentifier());
//            return existingGroup.get();
//        } else {
//            BlockingGroupListener listener = new BlockingGroupListener();
//            bridge.createGroup(groupItem.getName(), new ArrayList<>(deviceIds), listener);
//
//            listener.waitForCompletion();
//
//            if(listener.isSuccess()) {
//                LOG.debug("Group was succesfully created, returning");
//                return listener.getPhGroup();
//            } else {
//                throw new RuntimeHomeAutomationException("Unable to create Philips hue group");
//            }
//        }
//    }
//
//    public static class BlockingGroupListener implements PHGroupListener {
//
//        private CountDownLatch latch = new CountDownLatch(1);
//        private boolean success = false;
//
//        private PHGroup phGroup;
//
//        @Override
//        public void onCreated(PHGroup phGroup) {
//            LOG.debug("Group was created: {}", phGroup);
//            this.phGroup = phGroup;
//            success = true;
//            latch.countDown();
//        }
//
//        @Override
//        public void onReceivingGroupDetails(PHGroup phGroup) {
//
//        }
//
//        @Override
//        public void onReceivingAllGroups(List<PHBridgeResource> list) {
//
//        }
//
//        @Override
//        public void onSuccess() {
//            LOG.debug("Group operation succeeded");
//            success = true;
//            latch.countDown();
//        }
//
//        @Override
//        public void onError(int i, String s) {
//            LOG.error("Group operation failed code: {} reason: {}", i, s);
//            latch.countDown();
//        }
//
//        @Override
//        public void onStateUpdate(Map<String, String> map, List<PHHueError> list) {
//
//        }
//
//        public PHGroup getPhGroup() {
//            return phGroup;
//        }
//
//        public void waitForCompletion() {
//            LOG.debug("Waiting for callback");
//            Uninterruptibles.awaitUninterruptibly(latch);
//            LOG.debug("Callback received, releasing latch");
//        }
//
//        public boolean isSuccess() {
//            return success;
//        }
//    }
}
