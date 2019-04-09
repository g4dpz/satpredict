package com.badgersoft.satpredict.client;

import com.badgersoft.satpredict.client.dto.SatPosDTO;

public interface SatPredictClient {

	SatPosDTO getPosition(long catnum, double latitude, double longitude,double altitude);
}
