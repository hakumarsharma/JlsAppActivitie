/*************************************************************
 *
 * Reliance Digital Platform & Product Services Ltd.
 * CONFIDENTIAL
 * __________________
 *
 *  Copyright (C) 2020 Reliance Digital Platform & Product Services Ltd.–
 *
 *  ALL RIGHTS RESERVED.
 *
 * NOTICE:  All information including computer software along with source code and associated *documentation contained herein is, and
 * remains the property of Reliance Digital Platform & Product Services Ltd..  The
 * intellectual and technical concepts contained herein are
 * proprietary to Reliance Digital Platform & Product Services Ltd. and are protected by
 * copyright law or as trade secret under confidentiality obligations.
 * Dissemination, storage, transmission or reproduction of this information
 * in any part or full is strictly forbidden unless prior written
 * permission along with agreement for any usage right is obtained from Reliance Digital Platform & *Product Services Ltd.
 **************************************************************/

package com.jio.devicetracker.network;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ExitRemoveDeleteAPI {
    String BASE_URL = "https://stg.borqs.io/accounts/api/users/";
    @HTTP(method = "DELETE", path = "{userId}/{sessiongroups}/{groupId}/sessiongroupconsents/status", hasBody = true)
    Call<ResponseBody> deleteGroupDetails(@Header("Authorization") String token, @Header("Content-Type") String type, @Path("userId") String userId,
                               @Path("sessiongroups") String sessiongroups, @Path("groupId") String groupId, @Body RequestBody jsonObject);
}
