package es.uji.al259348.sliwserver.ut.services;

import es.uji.al259348.sliwserver.model.Location;
import es.uji.al259348.sliwserver.model.Sample;
import es.uji.al259348.sliwserver.model.User;
import es.uji.al259348.sliwserver.repositories.SampleRepository;
import es.uji.al259348.sliwserver.services.MLServiceImpl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runners.Parameterized;
import weka.classifiers.Classifier;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MLServiceImplTest {

    private static MLServiceImpl mlService;

    private static User user;

    @BeforeClass
    public static void setUp() throws Exception {

        SampleRepository mockSampleRepository = mock(SampleRepository.class);

        mlService = new MLServiceImpl();
        mlService.setSampleRepository(mockSampleRepository);

        List<Location> locations = Arrays.asList("Hab1", "Hab2", "Hab3", "Salón", "Baño", "Cocina", "Terraza").stream()
                .map(Location::new).collect(Collectors.toList());

        user = new User();
        user.setLocations(locations);

        List<Sample> validSamples = new ArrayList<>();
        List<Sample.WifiScanResult> scanResults;
        Sample sample;

        // Habitación 1
        scanResults = Arrays.asList(
                new Sample.WifiScanResult("Casa", "Casa", -75),
                new Sample.WifiScanResult("Vecino1", "Vecino1", -60),
                new Sample.WifiScanResult("Vecino2", "Vecino2", -55),
                new Sample.WifiScanResult("Vecino3", "Vecino3", -40),
                new Sample.WifiScanResult("Vecino4", "Vecino4", -30)
        );

        sample = new Sample();
        sample.setLocation(locations.get(0));
        sample.setScanResults(scanResults);

        validSamples.add(sample);

        // Habitación 2
        scanResults = Arrays.asList(
                new Sample.WifiScanResult("Casa", "Casa", -85),
                new Sample.WifiScanResult("Vecino1", "Vecino1", -68),
                new Sample.WifiScanResult("Vecino2", "Vecino2", -38),
                new Sample.WifiScanResult("Vecino3", "Vecino3", -45),
                new Sample.WifiScanResult("Vecino4", "Vecino4", -40)
        );

        sample = new Sample();
        sample.setLocation(locations.get(1));
        sample.setScanResults(scanResults);

        validSamples.add(sample);

        // Habitación 3
        scanResults = Arrays.asList(
                new Sample.WifiScanResult("Casa", "Casa", -68),
                new Sample.WifiScanResult("Vecino1", "Vecino1", -40),
                new Sample.WifiScanResult("Vecino2", "Vecino2", -55),
                new Sample.WifiScanResult("Vecino3", "Vecino3", -30),
                new Sample.WifiScanResult("Vecino4", "Vecino4", -35),
                new Sample.WifiScanResult("Vecino5", "Vecino5", -25)
        );

        sample = new Sample();
        sample.setLocation(locations.get(2));
        sample.setScanResults(scanResults);

        validSamples.add(sample);

        // Salón
        scanResults = Arrays.asList(
                new Sample.WifiScanResult("Casa", "Casa", -90),
                new Sample.WifiScanResult("Vecino1", "Vecino1", -50),
                new Sample.WifiScanResult("Vecino2", "Vecino2", -39),
                new Sample.WifiScanResult("Vecino3", "Vecino3", -42),
                new Sample.WifiScanResult("Vecino4", "Vecino4", -50),
                new Sample.WifiScanResult("Vecino5", "Vecino5", -19)
        );

        sample = new Sample();
        sample.setLocation(locations.get(3));
        sample.setScanResults(scanResults);

        validSamples.add(sample);

        // Baño
        scanResults = Arrays.asList(
                new Sample.WifiScanResult("Casa", "Casa", -80),
                new Sample.WifiScanResult("Vecino1", "Vecino1", -48),
                new Sample.WifiScanResult("Vecino2", "Vecino2", -46),
                new Sample.WifiScanResult("Vecino3", "Vecino3", -38),
                new Sample.WifiScanResult("Vecino4", "Vecino4", -28),
                new Sample.WifiScanResult("Vecino5", "Vecino5", -15)
        );

        sample = new Sample();
        sample.setLocation(locations.get(4));
        sample.setScanResults(scanResults);

        validSamples.add(sample);

        // Cocina
        scanResults = Arrays.asList(
                new Sample.WifiScanResult("Casa", "Casa", -50),
                new Sample.WifiScanResult("Vecino1", "Vecino1", -19),
                new Sample.WifiScanResult("Vecino2", "Vecino2", -23),
                new Sample.WifiScanResult("Vecino3", "Vecino3", -32),
                new Sample.WifiScanResult("Vecino4", "Vecino4", -49),
                new Sample.WifiScanResult("Vecino5", "Vecino5", -50)
        );

        sample = new Sample();
        sample.setLocation(locations.get(5));
        sample.setScanResults(scanResults);

        validSamples.add(sample);

        // Terraza
        scanResults = Arrays.asList(
                new Sample.WifiScanResult("Casa", "Casa", -40),
                new Sample.WifiScanResult("Vecino2", "Vecino2", -10),
                new Sample.WifiScanResult("Vecino3", "Vecino3", -15),
                new Sample.WifiScanResult("Vecino4", "Vecino4", -40),
                new Sample.WifiScanResult("Vecino5", "Vecino5", -70)
        );

        sample = new Sample();
        sample.setLocation(locations.get(6));
        sample.setScanResults(scanResults);

        validSamples.add(sample);

        when(mockSampleRepository.findByUserAndValid(user, true)).thenReturn(validSamples);

    }

    @Test
    public void buildClassifiers() {

        mlService.buildClassifiers(user);

        assertFalse(user.getClassifiers().isEmpty());

        user.getClassifiers().forEach(classifier -> {

//            // Hab1?
//            Instance instance = new Instance(6);
//            instance.setValue(0, -78); // Casa
//            instance.setValue(1, -62); // Vecino1
//            instance.setValue(2, -53); // Vecino2
//            instance.setValue(3, -38); // Vecino3
//            instance.setValue(4, -32); // Vecino4
//            instance.setValue(5, 0); // Vecino5

//            // Hab3?
//            Instance instance = new Instance(6);
//            instance.setValue(0, -65); // Casa
//            instance.setValue(1, -45); // Vecino1
//            instance.setValue(2, -50); // Vecino2
//            instance.setValue(3, -33); // Vecino3
//            instance.setValue(4, -35); // Vecino4
//            instance.setValue(5, -30); // Vecino5

//            // Baño?
//            Instance instance = new Instance(6);
//            instance.setValue(0, -85); // Casa
//            instance.setValue(1, -50); // Vecino1
//            instance.setValue(2, -42); // Vecino2
//            instance.setValue(3, -42); // Vecino3
//            instance.setValue(4, -30); // Vecino4
//            instance.setValue(5, -19); // Vecino5

//            // Cocina?
//            Instance instance = new Instance(6);
//            instance.setValue(0, -52); // Casa
//            instance.setValue(1, -21); // Vecino1
//            instance.setValue(2, -24); // Vecino2
//            instance.setValue(3, -30); // Vecino3
//            instance.setValue(4, -45); // Vecino4
//            instance.setValue(5, -53); // Vecino5

            // Terraza?
            Instance instance = new Instance(6);
            instance.setValue(0, -42); // Casa
            instance.setValue(1, 0); // Vecino1
            instance.setValue(2, -12); // Vecino2
            instance.setValue(3, -12); // Vecino3
            instance.setValue(4, -40); // Vecino4
            instance.setValue(5, -72); // Vecino5
            
            try {
                System.out.println(user.getLocations().get((int) classifier.classifyInstance(instance)));

                double[] distribution = classifier.distributionForInstance(instance);
                System.out.println(Arrays.toString(distribution));

            } catch (Exception e) {
                e.printStackTrace();
            }

        });

    }

}