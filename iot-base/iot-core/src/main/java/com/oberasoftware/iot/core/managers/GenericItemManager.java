package com.oberasoftware.iot.core.managers;

import com.oberasoftware.iot.core.model.storage.VirtualItem;

import java.util.List;

/**
 * @author Renze de Vries
 */
public interface GenericItemManager<T extends VirtualItem> {
    List<? extends T> getItems();

    List<? extends T> getItems(String controllerId);

    T getItem(String itemId);

    T store(T item);

    void delete(String itemId);
}
