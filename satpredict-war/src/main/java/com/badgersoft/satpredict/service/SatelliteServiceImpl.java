package com.badgersoft.satpredict.service;

import com.badgersoft.satpredict.client.dto.PassDTO;
import com.badgersoft.satpredict.client.dto.PassesDTO;
import com.badgersoft.satpredict.client.dto.SatelliteCharacter;
import com.badgersoft.satpredict.client.dto.SatelliteCharacteristics;
import com.badgersoft.satpredict.dao.AliasDao;
import com.badgersoft.satpredict.dao.TleDao;
import com.badgersoft.satpredict.domain.TleEntity;
import com.badgersoft.satpredict.utils.Cache;
import com.google.auth.oauth2.ServiceAccountJwtAccessCredentials;
import com.google.protobuf.Timestamp;
import com.stellarstation.api.v1.groundstation.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.auth.MoreCallCredentials;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.me.g4dpz.satellite.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class SatelliteServiceImpl implements SatelliteService {

    private static final Logger LOG = LoggerFactory.getLogger(TleUpdateServiceImpl.class);
    public static final int THREE_DAYS_MILLS = (3 * 24 * 60 * 60 * 1000);

    private static String STELLARSTATION_API_KEY = null;

    @Autowired
    TleDao tleDao;

    @Autowired
    AliasDao aliasDao;

    @Autowired
    Cache cache;

    static {
        initApiKey();
    }

    @Override
    public void reserveStellarStationSlots(String... noradeIdList) throws IOException {

        try {
            removeUnavailableSlots();
            bookUnavailableSlots(39444L, 42017L, 43803L);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    @Override
    public SatelliteCharacteristics getSatelliteCharacteristics() {
        Iterator<TleEntity> iterator = tleDao.findAll().iterator();

        ConcurrentMap<Long, SatelliteCharacter> satelliteCharacterMap = new ConcurrentHashMap<>();

        while (iterator.hasNext()) {
            TleEntity tleEntity = iterator.next();
            long catnum = tleEntity.getCatnum();
            SatelliteCharacter satelliteCharacter = new SatelliteCharacter();
            satelliteCharacter.setCatnum(catnum);
            satelliteCharacter.setAliases(tleEntity.getAliases());
            satelliteCharacterMap.put(catnum, satelliteCharacter);
        }

        List<SatelliteCharacter> satelliteCharacterList = new ArrayList<>();

        Iterator<Map.Entry<Long, SatelliteCharacter>> satelliteMapIterator
                = satelliteCharacterMap.entrySet().iterator();

        while (satelliteMapIterator.hasNext()) {
            satelliteCharacterList.add(satelliteMapIterator.next().getValue());
        }

        SatelliteCharacteristics characteristics = new SatelliteCharacteristics();
        characteristics.setCharacters(satelliteCharacterList);
        return characteristics;
    }

    @Override
    public SatelliteCharacter getSatelliteCharacter(Long catnum) {
        SatelliteCharacter satelliteCharacter = new SatelliteCharacter();

        if (!cache.containsKey(catnum)) {
            List<TleEntity> tleEntities = tleDao.findByCatnum(catnum);
            if ((tleEntities != null) && !tleEntities.isEmpty()) {
                cache.put(catnum, tleEntities.get(0), 86400000L);
            } else {
                return null;
            }
        }

        TleEntity tleEntity = (TleEntity) cache.get(catnum);

        satelliteCharacter.setAliases(tleEntity.getAliases());
        satelliteCharacter.setCatnum(catnum);
        return satelliteCharacter;
    }

    @Override
    public SatPos getPosition(Long catnum, double latitude, double longitude, double altitude) {
        SatPos satPos;

        if (!cache.containsKey(catnum)) {
            List<TleEntity> tleEntities = tleDao.findByCatnum(catnum);
            if ((tleEntities != null) && !tleEntities.isEmpty()) {
                cache.put(catnum, tleEntities.get(0), 86400000L);
            } else {
                return null;
            }
        }

        TleEntity tleEntity = (TleEntity) cache.get(catnum);

        String[] lines = new String[]{tleEntity.getLine1(), tleEntity.getLine2(), tleEntity.getLine3()};

        TLE tle = new TLE(lines);

        Satellite satellite = SatelliteFactory.createSatellite(tle);

        Date now = new Date(System.currentTimeMillis());
        satPos = satellite.getPosition(new GroundStationPosition(latitude, longitude, altitude), now);
        return satPos;
    }

    @Override
    public PassesDTO getPassesDTO(Long catnum, Double latitude, Double longitude, Double altitude, Integer hours) throws SatNotFoundException, InvalidTleException {
        if (!cache.containsKey(catnum)) {
            List<TleEntity> tleEntities = tleDao.findByCatnum(catnum);
            if ((tleEntities != null) && !tleEntities.isEmpty()) {
                cache.put(catnum, tleEntities.get(0), 86400000L);
            } else {
                return null;
            }
        }

        TleEntity tleEntity = (TleEntity) cache.get(catnum);

        String[] lines = new String[]{tleEntity.getLine1(), tleEntity.getLine2(), tleEntity.getLine3()};

        TLE tle = new TLE(lines);

        Date now = new Date(System.currentTimeMillis());
        PassPredictor passPredictor;


        passPredictor = new PassPredictor(tle, new GroundStationPosition(latitude, longitude, altitude));
        List<SatPassTime> passes = passPredictor.getPasses(now, hours, true);
        PassesDTO passesDTO = new PassesDTO(passes);
        return passesDTO;

    }

    private static void initApiKey() {
        try {
            BufferedReader txtReader = new BufferedReader(new InputStreamReader(TleUpdateServiceImpl.class.getResourceAsStream("/stellarstation-private-key.json")));
            StringBuffer lines = new StringBuffer();
            String strLine;
            while ((strLine = txtReader.readLine()) != null) {
                lines.append(strLine).append("\n");
            }
            STELLARSTATION_API_KEY = lines.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeUnavailableSlots() throws IOException {

        // Load the private key downloaded from the StellarStation Console.
        ServiceAccountJwtAccessCredentials credentials =
                ServiceAccountJwtAccessCredentials.fromStream(
                        this.getClass().getResource("/stellarstation-private-key.json").openStream(),
                        URI.create("https://api.stellarstation.com"));
        LOG.info(credentials.toString());

        // Setup the gRPC client.
        ManagedChannel channel =
                ManagedChannelBuilder.forAddress("api.stellarstation.com", 443)
                        .build();

        GroundStationServiceGrpc.GroundStationServiceStub client =
                GroundStationServiceGrpc.newStub(channel)
                        .withCallCredentials(MoreCallCredentials.from(credentials));

        final Date time1 = new Date();

        final Timestamp startTime = Timestamp.newBuilder().setSeconds(time1.getTime() / 1000).build();
        final Timestamp endTime = Timestamp.newBuilder().setSeconds((time1.getTime() + THREE_DAYS_MILLS) / 1000).build();

        ListUnavailabilityWindowsRequest listUnavailabilityWindowsRequest
                = ListUnavailabilityWindowsRequest.newBuilder()
                .setGroundStationId("15")
                .setStartTime(startTime)
                .setEndTime(endTime)
                .build();

        StreamObserver<ListUnavailabilityWindowsResponse> listObserver = new StreamObserver<ListUnavailabilityWindowsResponse>() {

            @Override
            public void onNext(ListUnavailabilityWindowsResponse listUnavailabilityWindowsResponse) {
                final List<UnavailabilityWindow> windowList = listUnavailabilityWindowsResponse.getWindowList();

                for (UnavailabilityWindow window : windowList) {
                    DeleteUnavailabilityWindowRequest windowRequest
                            = DeleteUnavailabilityWindowRequest.newBuilder()
                            .setWindowId(window.getWindowId())
                            .build();

                    StreamObserver<DeleteUnavailabilityWindowResponse> deleteObserver = new StreamObserver<DeleteUnavailabilityWindowResponse>() {

                        @Override
                        public void onNext(DeleteUnavailabilityWindowResponse response) {
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            throwable.printStackTrace();
                        }

                        @Override
                        public void onCompleted() {
                            LOG.info("Complete");
                        }
                    };

                    client.deleteUnavailabilityWindow(windowRequest, deleteObserver);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                LOG.info("Complete");
            }
        };

        client.listUnavailabilityWindows(listUnavailabilityWindowsRequest, listObserver);

    }

    private void bookUnavailableSlots(long... catalogueNumbers) throws SatNotFoundException, InvalidTleException, IOException {

        List<PassDTO> passes = new ArrayList<>();

        boolean init = false;

        for (long catalogueNumber : catalogueNumbers) {
            PassesDTO passesDTO = getPassesDTO(catalogueNumber, 52.4670, -2.022, 200.0, 36);
            if (!init) {
                for (PassDTO passDTO : passesDTO.getPassList()) {
                    passes.add(passDTO);
                }
                init = true;
            }
            else {
                for (PassDTO pass : passesDTO.getPassList()) {
                    if (!mergePasses(passes, pass)) {
                        passes.add(pass);
                    }
                }
            }
        }

        LOG.info(STELLARSTATION_API_KEY);
        // Load the private key downloaded from the StellarStation Console.
        ServiceAccountJwtAccessCredentials credentials =
                ServiceAccountJwtAccessCredentials.fromStream(
                        this.getClass().getResource("/stellarstation-private-key.json").openStream(),
                        URI.create("https://api.stellarstation.com"));
        LOG.info(credentials.toString());

        // Setup the gRPC client.
        ManagedChannel channel =
                ManagedChannelBuilder.forAddress("api.stellarstation.com", 443)
                        .build();

        GroundStationServiceGrpc.GroundStationServiceStub client =
                GroundStationServiceGrpc.newStub(channel)
                        .withCallCredentials(MoreCallCredentials.from(credentials));

        for (PassDTO pass : passes) {
            final Date time1 = pass.getStartTime();
            final Date time2 = pass.getEndTime();


            final Timestamp startTime = Timestamp.newBuilder().setSeconds(time1.getTime() / 1000).build();
            final Timestamp endTime = Timestamp.newBuilder().setSeconds(time2.getTime() / 1000).build();

            AddUnavailabilityWindowRequest unavailabilityWindowsRequest
                    = AddUnavailabilityWindowRequest.newBuilder()
                    .setGroundStationId("15")
                    .setStartTime(startTime)
                    .setEndTime(endTime)
                    .build();

            StreamObserver<AddUnavailabilityWindowResponse> responseObserver = new StreamObserver<AddUnavailabilityWindowResponse>() {

                @Override
                public void onNext(AddUnavailabilityWindowResponse addUnavailabilityWindowResponse) {
                    final String windowId = addUnavailabilityWindowResponse.getWindowId();
                    LOG.info(windowId);
                }

                @Override
                public void onError(Throwable throwable) {
                    throwable.printStackTrace();
                }

                @Override
                public void onCompleted() {
                    LOG.info("Complete");
                }
            };

            client.addUnavailabilityWindow(unavailabilityWindowsRequest, responseObserver);

        }
    }

    private boolean mergePasses(List<PassDTO> passes, PassDTO pass) {
        boolean merged = false;
        for(PassDTO existingPass : passes) {
            if (pass.getStartTime().getTime() >= existingPass.getStartTime().getTime()
                    && pass.getStartTime().getTime() <= existingPass.getEndTime().getTime()
                    && pass.getEndTime().getTime() > existingPass.getEndTime().getTime()) {
                existingPass.setEndTime(pass.getEndTime());
                LOG.info("Extending " + existingPass.getStartTime().getTime() + " - " + existingPass.getEndTime() + " to " + pass.getEndTime());
                merged = true;
                break;
            }
            else if (pass.getStartTime().getTime() < existingPass.getStartTime().getTime()
                    && pass.getEndTime().getTime() >= existingPass.getStartTime().getTime()
                    && pass.getEndTime().getTime() < existingPass.getEndTime().getTime()) {
                existingPass.setStartTime(pass.getStartTime());
                LOG.info("Extending " + existingPass.getStartTime().getTime() + " - " + existingPass.getEndTime() + " to " + pass.getStartTime());
                merged = true;
                break;
            }
        }
        return merged;
    }
}
