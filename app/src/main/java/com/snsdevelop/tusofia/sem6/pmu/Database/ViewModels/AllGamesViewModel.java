package com.snsdevelop.tusofia.sem6.pmu.Database.ViewModels;

import android.app.Application;

import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.AllGamesEntity;
import com.snsdevelop.tusofia.sem6.pmu.Database.Repositories.AllGamesRepository;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;

public class AllGamesViewModel extends AndroidViewModel {
    private AllGamesRepository allGamesRepository;

    public AllGamesViewModel(Application application) {
        super(application);
        allGamesRepository = new AllGamesRepository(application);
    }

    public List<AllGamesEntity> getAll() {
        return allGamesRepository.getAll();
    }

    public void insert(AllGamesEntity word) {
        allGamesRepository.insert(word);
    }

    public void deleteAll(){
        allGamesRepository.deleteAll();
    }
}
