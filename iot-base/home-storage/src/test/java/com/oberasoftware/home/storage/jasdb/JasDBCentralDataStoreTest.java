package com.oberasoftware.home.storage.jasdb;

import com.oberasoftware.iot.core.exceptions.DataStoreException;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.iot.core.model.storage.impl.IotThingImpl;
import com.oberasoftware.iot.core.model.storage.impl.WidgetImpl;
import com.oberasoftware.jasdb.api.exceptions.JasDBException;
import com.oberasoftware.jasdb.engine.HomeLocatorUtil;
import com.oberasoftware.jasdb.service.JasDBMain;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JasDBConfiguration.class)
public class JasDBCentralDataStoreTest {
    private static final Logger LOG = getLogger(JasDBCentralDataStoreTest.class);

    @Autowired
    private JasDBCentralDatastore centralDatastore;

    @Autowired
    private JasDBDAO jasDBDAO;

    @ClassRule
    public static TemporaryFolder folder = new TemporaryFolder();

    @BeforeClass
    public static void setUp() throws IOException {
        System.setProperty(HomeLocatorUtil.JASDB_HOME, folder.newFolder().toString());
    }

    @After
    public void tearDown() throws JasDBException {
        JasDBMain.shutdown();
    }

    @Test
    public void storeAndUpdateDevice() throws JasDBException, DataStoreException {
        String id = UUID.randomUUID().toString();

        centralDatastore.store(new IotThingImpl(id, "controller1", "device1", "plugin1", null, new HashMap<>()));

        Optional<IotThing> item = jasDBDAO.findThing("controller1",  "device1");
        assertThat(item.isPresent(), is(true));

        centralDatastore.store(new IotThingImpl(id, "controller1", "plugin1", "device1", "updated name", new HashMap<>()));

        assertThat(jasDBDAO.findThings("controller1").size(), is(1));

        item = jasDBDAO.findThing("controller1", "device1");
        assertThat(item.isPresent(), is(true));
    }

    @Test
    public void storeAndUpdateUIItem() throws JasDBException, DataStoreException {
        String id1 = UUID.randomUUID().toString();
        String id2 = UUID.randomUUID().toString();

        centralDatastore.store(new WidgetImpl(id1, "UI Item 1", "container1", "switch", "jsdlfjsd", "randomControllerId", new HashMap<>(), 0));
        centralDatastore.store(new WidgetImpl(id2, "UI Item 2", "container1", "switch", "jsdlfjsd", "randomControllerId", new HashMap<>(), 0));

        assertThat(jasDBDAO.findWidgets("container1").size(), is(2));
    }
}
