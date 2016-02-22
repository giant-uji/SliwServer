package es.uji.al259348.sliwserver.services;

import es.uji.al259348.sliwserver.model.Sample;

public interface SampleService {

    Sample save(Sample sample);
    void classify(Sample sample);

}
