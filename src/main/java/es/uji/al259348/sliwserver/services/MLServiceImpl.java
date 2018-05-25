package es.uji.al259348.sliwserver.services;

import es.uji.al259348.sliwserver.ml.TrainingSetBuilder;
import es.uji.al259348.sliwserver.model.Location;
import es.uji.al259348.sliwserver.model.Sample;
import es.uji.al259348.sliwserver.model.User;
import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MLServiceImpl implements MLService {

    private static final String UNKNOWN_LOCATION = "Localización desconocida";

    @Override
    public List<Classifier> buildClassifiers(User user, List<Sample> validSamples) {

        Instances trainingSet = new TrainingSetBuilder()
                .setAttributes(user.getBssids())
                .setClassAttribute("Location", user.getLocations().stream()
                        .map(Location::getName)
                        .collect(Collectors.toList())
                )
                .build("TrainingSet", validSamples.size());

        // Create instances
        validSamples.forEach(sample -> {
            Map<String,Integer> BSSIDLevelMap = getBSSIDLevelMap(sample);

            Instance instance = new Instance(trainingSet.numAttributes());

            for (Enumeration e = trainingSet.enumerateAttributes(); e.hasMoreElements();) {
                Attribute attribute = (Attribute) e.nextElement();
                String bssid = attribute.name();
                int level = (BSSIDLevelMap.containsKey(bssid)) ? BSSIDLevelMap.get(bssid) : 0;
                instance.setValue(attribute, level);
            }

            instance.setValue(trainingSet.classAttribute(), sample.getLocation());

            instance.setDataset(trainingSet);
            trainingSet.add(instance);
        });

        // Build classifiers
        List<Classifier> classifiers = buildClassifiers(trainingSet);
        return classifiers;
    }

    @Override
    public String classify(User user, Sample sample) {
        if (sample.getScanResults().size() == 0) {
            return UNKNOWN_LOCATION;
        }

        Instances trainingSet = new TrainingSetBuilder()
                .setAttributes(user.getBssids())
                .setClassAttribute("Location", user.getLocations().stream()
                        .map(Location::getName)
                        .collect(Collectors.toList())
                )
                .build("TrainingSet", 1);

        // Create instance
        Map<String,Integer> BSSIDLevelMap = getBSSIDLevelMap(sample);

        Instance instance = new Instance(trainingSet.numAttributes());

        int hits = 0;
        for (Enumeration e = trainingSet.enumerateAttributes(); e.hasMoreElements();) {
            Attribute attribute = (Attribute) e.nextElement();
            String bssid = attribute.name();
            int level = (BSSIDLevelMap.containsKey(bssid)) ? BSSIDLevelMap.get(bssid) : 0;
            if (level < 0) {
                hits++;
            }
            instance.setValue(attribute, level);
        }

        if (sample.getLocation() != null)
            instance.setValue(trainingSet.classAttribute(), sample.getLocation());

        instance.setDataset(trainingSet);
        trainingSet.add(instance);

        int predictedClass = classify(fromBase64(user.getClassifiers()), instance);
        String location = UNKNOWN_LOCATION;
        if (predictedClass >= 0 && hits > 0) {
            location = trainingSet.classAttribute().value(predictedClass);
        }

        return location;
    }

    private boolean classifierOK(Classifier classifier) {
        String name = classifier.getClass().getName();
        if (name.contains("RandomForest") || name.contains("BayesNet"))
            return true;
        else return false;
    }

    private int classify(List<Classifier> classifiers, Instance instance) {

//        List<Double> predictedClasses = classifiers.stream()
//                .map(classifier -> {
//                    try {
//                        return classifier.classifyInstance(instance);
//                    } catch (Exception e) {
//                        return -1.0;
//                    }
//                }).collect(Collectors.toList());

        List<Double> predictedClasses = classifiers.stream()
                .filter(classifier -> classifierOK(classifier))
                .map(classifier -> {
                    try {
                        return classifier.classifyInstance(instance);
                    } catch (Exception e) {
                        return -1.0;
                    }
                }).collect(Collectors.toList());

//        System.out.println("Resultados de clasificar muestra:");
//        System.out.println(predictedClasses);

        Map<Double, Long> predictedClassOcurrencesMap = predictedClasses.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

//        System.out.println("Mapa de ocurrencias:");
//        System.out.println(predictedClassOcurrencesMap);

        Double predictedClass = predictedClassOcurrencesMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get().getKey();

//        System.out.println("Resultado final:");
//        System.out.println(predictedClass);

//        System.out.println("Real: " + instance.classValue());

        return predictedClass.intValue();
    }

    public Map<String,Integer> getBSSIDLevelMap(Sample sample) {
        return sample.getScanResults().stream()
                .collect(Collectors.toMap(
                        wifiScanResult -> wifiScanResult.BSSID,
                        wifiScanResult -> wifiScanResult.level
                ));
    }

    public List<Classifier> buildClassifiers(Instances trainingSet) {

        List<Classifier> classifiers = new ArrayList<>();
        classifiers.add(new MultilayerPerceptron());
        classifiers.add(new SMO());
        classifiers.add(new J48());
        classifiers.add(new RandomForest());
        classifiers.add(new BayesNet());

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

    public static byte[] toByteArray(Classifier classifier) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(classifier);
        oos.close();
        baos.close();

        return baos.toByteArray();
    }

    public static Classifier fromByteArray(byte[] bytes) throws IOException, ClassNotFoundException {

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Classifier classifier = (Classifier) ois.readObject();
        ois.close();
        bais.close();

        return classifier;
    }

    public static String toBase64(Classifier classifier) {
        try {
            byte[] bytes = toByteArray(classifier);
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            return null;
        }
    }

    public static Classifier fromBase64(String base64EncodedClassifier) {
        try {
            byte[] bytes = Base64.getDecoder().decode(base64EncodedClassifier);;
            return fromByteArray(bytes);
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static List<String> toBase64(List<Classifier> classifiers) {
        return classifiers.stream()
                .map(MLServiceImpl::toBase64)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<Classifier> fromBase64(List<String> base64EncodedClassifiers) {
        return base64EncodedClassifiers.stream()
                .map(MLServiceImpl::fromBase64)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
