package de.toomuchcoffee.figurearchive.view.controls;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

import java.util.Map;

public class TabMenu extends Tabs {

    public TabMenu(Map<Tab, Component> tabsToPages, VerticalLayout tabSheet, int defaultSelection) {
        super(tabsToPages.keySet().toArray(new Tab[0]));

        setSelectedIndex(defaultSelection);
        tabSheet.add(tabsToPages.get(getSelectedTab()));

        addSelectedChangeListener(event -> {
            tabSheet.removeAll();
            tabSheet.add(tabsToPages.get(getSelectedTab()));
        });
    }

}
