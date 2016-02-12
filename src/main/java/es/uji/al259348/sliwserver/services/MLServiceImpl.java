package es.uji.al259348.sliwserver.services;

import es.uji.al259348.sliwserver.model.Sample;
import es.uji.al259348.sliwserver.model.User;
import es.uji.al259348.sliwserver.repositories.SampleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import weka.classifiers.Classifier;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MLServiceImpl implements MLService {

    @Autowired
    private SampleRepository sampleRepository;

    public void setSampleRepository(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }

    @Override
    public void buildClassifiers(User user) {

        List<Sample> validSamples = sampleRepository.findByUserAndValid(user, true);

        List<String> allBSSIDs = getAllBSSIDs(validSamples);

        // Create attributes
        List<Attribute> attributes = allBSSIDs.stream()
                .map(Attribute::new)
                .collect(Collectors.toList());

        // Create class attribute
        FastVector fvClassValues = new FastVector(user.getLocations().size());
        user.getLocations().forEach(location -> fvClassValues.addElement(location.getName()));
        Attribute classAttribute = new Attribute("Location", fvClassValues);

        // Create training set
        FastVector fvAttributes = new FastVector(attributes.size()+1);
        attributes.forEach(fvAttributes::addElement);
        fvAttributes.addElement(classAttribute);

        Instances trainingSet = new Instances("TrainingSet", fvAttributes, validSamples.size());
        trainingSet.setClassIndex(fvAttributes.size()-1);

        // Create instances
        validSamples.forEach(sample -> {
            Map<String,Integer> BSSIDLevelMap = getBSSIDLevelMap(sample);

            Instance instance = new Instance(fvAttributes.size());

            attributes.forEach(attribute -> {
                String bssid = attribute.name();
                int level = (BSSIDLevelMap.containsKey(bssid)) ? BSSIDLevelMap.get(bssid) : 0;
                instance.setValue(attribute, level);
            });

            instance.setValue(classAttribute, sample.getLocation().getName());

            instance.setDataset(trainingSet);
            trainingSet.add(instance);
        });

        // Build classifiers
        List<Classifier> classifiers = buildClassifiers(trainingSet);
        user.setClassifiers(classifiers);

    }

    private List<String> getAllBSSIDs(List<Sample> samples) {
        return samples.stream()
                .flatMap(sample -> sample.getScanResults().stream())
                .map(wifiScanResult -> wifiScanResult.BSSID)
                .distinct()
                .collect(Collectors.toList());
    }

    private Map<String,Integer> getBSSIDLevelMap(Sample sample) {
        return sample.getScanResults().stream()
                .collect(Collectors.toMap(
                        wifiScanResult -> wifiScanResult.BSSID,
                        wifiScanResult -> wifiScanResult.level
                ));
    }

    public List<Classifier> buildClassifiers(Instances trainingSet) {

        List<Classifier> classifiers = new ArrayList<>();
        classifiers.add(new MultilayerPerceptron());


        classifiers.forEach(classifier -> {
            try {
                classifier.buildClassifier(trainingSet);
            } catch (Exception e) {
                // The classifier has not been generated successfully
                e.printStackTrace();
            }
        });

        return classifiers;
    }

}
