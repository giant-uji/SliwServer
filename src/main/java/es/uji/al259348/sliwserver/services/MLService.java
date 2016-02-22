package es.uji.al259348.sliwserver.services;

import es.uji.al259348.sliwserver.model.Sample;
import es.uji.al259348.sliwserver.model.User;
import weka.classifiers.Classifier;

import java.util.List;

public interface MLService {

    List<Classifier> buildClassifiers(User user, List<Sample> samples);
    String classify(User user, Sample sample);

}
