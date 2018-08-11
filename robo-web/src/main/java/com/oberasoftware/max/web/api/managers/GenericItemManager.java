package com.oberasoftware.max.web.api.managers;


import com.oberasoftware.max.web.api.model.Item;

import java.util.List;

/**
 * @author Renze de Vries
 */
public interface GenericItemManager<T extends Item> {
    List<? extends T> getItems();

    List<? extends T> getItems(String controllerId);

    T getItem(String itemId);

    T store(T item);

    void delete(String itemId);
}
