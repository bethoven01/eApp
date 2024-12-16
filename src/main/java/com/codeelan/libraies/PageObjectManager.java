package com.codeelan.libraies;

import com.codeelan.pages.*;
import com.epam.healenium.SelfHealingDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PageObjectManager extends BaseClass {
    private static final Logger Log = LogManager.getLogger(PageObjectManager.class);
    private final SelfHealingDriver driver;
    private E2EFlowDataPage onE2EFlowDataPage;
    private RestMasterPage onRestMasterPage;
    public PageObjectManager(SelfHealingDriver driver) {
        this.driver = driver;
    }

    public E2EFlowDataPage getE2EFlowDataPage() {
        try {
            return (onE2EFlowDataPage == null) ? onE2EFlowDataPage = new E2EFlowDataPage(driver) : onE2EFlowDataPage;
        } catch (Exception e) {
            Log.error("Instance creations of E2EFlowDataPage Failed ", e);
            throw new FLException("Instance creations of E2EFlowDataPage Failed " + e.getMessage());
        }
    }

    public RestMasterPage getRestMasterPage() {
        try {
            return (onRestMasterPage == null) ? onRestMasterPage = new RestMasterPage(driver) : onRestMasterPage;
        } catch (Exception e) {
            Log.error("Instance creations of RestMasterPage Failed ", e);
            throw new FLException("Instance creations of RestMasterPage Failed " + e.getMessage());
        }
    }
}
