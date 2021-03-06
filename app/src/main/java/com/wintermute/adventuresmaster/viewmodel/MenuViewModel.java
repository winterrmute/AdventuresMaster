package com.wintermute.adventuresmaster.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.wintermute.adventuresmaster.database.entity.menu.ActivityDesc;
import com.wintermute.adventuresmaster.database.entity.menu.ActivityExtras;
import com.wintermute.adventuresmaster.database.entity.menu.MenuItem;
import com.wintermute.adventuresmaster.database.repository.MenuRepository;

import java.util.List;

/**
 * Observes changes in menu and sends notifications about changes to the activity showing menu.
 *
 * @author wintermute
 */
public class MenuViewModel extends ViewModel
{
    private MenuRepository repository;

    /**
     * @param repository data cache.
     */
    public void initRepository(MenuRepository repository)
    {
        this.repository = repository;
    }

    /**
     * @return top level items in main menu.
     */
    public LiveData<List<MenuItem>> getTopLevelItems()
    {
        return repository.getTopLevelItems();
    }

    /**
     * @param target menu item that has been selected.
     * @return selected menu item.
     */
    public LiveData<List<MenuItem>> getSelectedItemContent(MenuItem target)
    {
        return repository.getSelectedItemContent(target);
    }

    /**
     * @param target menu item that has been selected.
     * @return its children menu item elements.
     */
    public LiveData<List<MenuItem>> getItemParentContent(MenuItem target)
    {
        return repository.getItemParentContent(target);
    }

    /**
     * @param target menu item that has been selected.
     * @return its parent menu item element.
     */
    public LiveData<MenuItem> getItemParent(MenuItem target)
    {
        return repository.getItemParent(target);
    }

    /**
     * @param target menu item that has been selected.
     * @return activity description of selected menu item.
     */
    public LiveData<ActivityDesc> getActivity(MenuItem target)
    {
        return repository.getActivity(target);
    }

    /**
     * @param target activity description of menu item that has been selected.
     * @return activity extras of selected activity.
     */
    public LiveData<List<ActivityExtras>> getActivityExtras(ActivityDesc target)
    {
        return repository.getActivityExtras(target);
    }
}
