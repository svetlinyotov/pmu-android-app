package com.snsdevelop.tusofia.sem6.pmu.Database.ViewModels;

import android.app.Application;

import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.RankEntity;
import com.snsdevelop.tusofia.sem6.pmu.Database.Repositories.RankingRepository;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;

public class RankingViewModel extends AndroidViewModel {
    private RankingRepository rankingRepository;

    public RankingViewModel(Application application) {
        super(application);
        rankingRepository = new RankingRepository(application);
    }

    public List<RankEntity> getAll() {
        return rankingRepository.getAll();
    }

    public void insert(RankEntity word) {
        rankingRepository.insert(word);
    }

    public void deleteAll(){
        rankingRepository.deleteAll();
    }
}
