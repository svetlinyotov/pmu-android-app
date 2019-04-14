package com.snsdevelop.tusofia.sem6.pmu.Database.ViewModels;

import android.app.Application;

import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.QRMarkerEntity;
import com.snsdevelop.tusofia.sem6.pmu.Database.Repositories.QRMarkersRepository;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;

public class QRMarkersViewModel extends AndroidViewModel {
    private QRMarkersRepository qrMarkersRepository;

    public QRMarkersViewModel(Application application) {
        super(application);
        qrMarkersRepository = new QRMarkersRepository(application);
    }

    public List<QRMarkerEntity> getAll() {
        return qrMarkersRepository.getAll();
    }

    public List<QRMarkerEntity> getMarker(String result){return qrMarkersRepository.getMarker(result);}

    public void insert(QRMarkerEntity word) {
        qrMarkersRepository.insert(word);
    }

    public void deleteAll(){
        qrMarkersRepository.deleteAll();
    }
}
