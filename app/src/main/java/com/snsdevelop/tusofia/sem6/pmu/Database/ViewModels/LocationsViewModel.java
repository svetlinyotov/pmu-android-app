package com.snsdevelop.tusofia.sem6.pmu.Database.ViewModels;

import android.app.Application;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;

import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.Location;
import com.snsdevelop.tusofia.sem6.pmu.Database.Repositories.LocationsRepository;

public class LocationsViewModel extends AndroidViewModel {
    private LocationsRepository locationsRepository;

    public LocationsViewModel(Application application) {
        super(application);
        locationsRepository = new LocationsRepository(application);
    }

    public List<Location> getAll() {
        return locationsRepository.getAll();
    }

    public void insert(Location word) {
        locationsRepository.insert(word);
    }
}
