package com.geekwims.hereiam.api;


import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geekwims.hereiam.api.request.AuthenticationRequest;
import com.geekwims.hereiam.api.request.BoardRequest;
import com.geekwims.hereiam.attendance.AttendHistory;
import com.geekwims.hereiam.constant.BOARD_TYPE;
import com.geekwims.hereiam.response.AuthResponse;
import com.geekwims.hereiam.response.BoardPageResponse;
import com.geekwims.hereiam.response.BoardResponse;
import com.geekwims.hereiam.response.course.CourseInfo;
import com.geekwims.hereiam.response.course.NextCourseInfo;
import com.geekwims.hereiam.response.schedule.ClassSchedule;

import java.io.IOException;
import java.util.List;


public class ApiService {
    private final static String TAG = "ApiService";
    private static ApiService apiService = null;
    private final String API_SERVER_URL = "http://210.89.176.83:8080/hereiam";
    private final String AUTH_PATH = "/auth/authenticate";

    private AuthResponse authResponse = null;

    private ApiService() {
    }

    public static ApiService getInstance() {
        if (apiService == null)
            apiService = new ApiService();

        return apiService;
    }

    public boolean refreshAccessToken(String id, String password, String type) {
        return refreshAccessToken(new AuthenticationRequest(id, password, type));
    }

    public boolean refreshAccessToken(AuthenticationRequest request) {
        ApiRequest apiRequest = new ApiRequest();
        ObjectMapper objectMapper = new ObjectMapper();

        ApiResponse apiResponse = null;
        try {
            apiResponse = apiRequest.post(API_SERVER_URL + AUTH_PATH, objectMapper.writeValueAsString(request));
            if (apiResponse.getStatus() == 200) {
                this.authResponse = objectMapper.readValue(apiResponse.getResponse(), AuthResponse.class);
            } else {
                throw new IOException("Can not access api server");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public ClassSchedule getTimeTable() {

        // TODO : authentication exception is required
        if (authResponse == null) {
            return null;
        }

        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setAccessToken(authResponse.getToken());

        ObjectMapper objectMapper = new ObjectMapper();
        String url = API_SERVER_URL + "/students/" + authResponse.getUsername() + "/timeTable";

        ApiResponse apiResponse = null;
        ClassSchedule classSchedule = null;
        try {
            apiResponse = apiRequest.get(url);
            if (apiResponse.getStatus() == 200) {
                classSchedule = objectMapper.readValue(apiResponse.getResponse(), ClassSchedule.class);
            } else {
                throw new IOException("Can not access api server");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classSchedule;
    }

    public NextCourseInfo getNextCourseInfo() {

        // TODO : authentication exception is required
        if (authResponse == null) {
            return null;
        }

        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setAccessToken(authResponse.getToken());

        ObjectMapper objectMapper = new ObjectMapper();
        String url = API_SERVER_URL + "/students/" + authResponse.getUsername() + "/getNextClassInfo";

        ApiResponse apiResponse;
        NextCourseInfo nextCourseInfo = null;

        Log.d(TAG, "getNextCourseInfo");

        try {
            apiResponse = apiRequest.get(url);
            if (apiResponse.getStatus() == 200) {
                nextCourseInfo = objectMapper.readValue(apiResponse.getResponse(), NextCourseInfo.class);

                Log.d(TAG, "got data successfully");
            } else {
                Log.d("server connection error", String.valueOf(apiResponse.getStatus()));
                throw new IOException("Can not access api server");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return nextCourseInfo;
    }


    public List<CourseInfo> getTakes() {

        // TODO : authentication exception is required
        if (authResponse == null) {
            return null;
        }

        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setAccessToken(authResponse.getToken());

        ObjectMapper objectMapper = new ObjectMapper();
        String url = API_SERVER_URL + "/students/" + authResponse.getUsername() + "/takes";

        ApiResponse apiResponse;
        List<CourseInfo> courseInfos = null;

        try {
            apiResponse = apiRequest.get(url);
            if (apiResponse.getStatus() == 200) {
                courseInfos = objectMapper.readValue(apiResponse.getResponse(), new TypeReference<List<CourseInfo>>(){});
            } else {
                Log.d("server connection error", String.valueOf(apiResponse.getStatus()));
                throw new IOException("Can not access api server");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return courseInfos;
    }

    public AttendHistory getAttendHistory(CourseInfo courseInfo) {
        // TODO : authentication exception is required
        if (authResponse == null) {
            return null;
        }

        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setAccessToken(authResponse.getToken());

        ObjectMapper objectMapper = new ObjectMapper();
        String url = API_SERVER_URL + "/students/" + authResponse.getUsername() + "/attendHistory/" + courseInfo.getCourseId() + "/1";

        ApiResponse apiResponse;
        AttendHistory attendHistory = null;

        try {
            apiResponse = apiRequest.get(url);
            if (apiResponse.getStatus() == 200) {
                attendHistory = objectMapper.readValue(apiResponse.getResponse(), AttendHistory.class);
            } else {
                Log.d("server connection error", String.valueOf(apiResponse.getStatus()));
                throw new IOException("Can not access api server");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return attendHistory;
    }

    public BoardPageResponse getBoards(BOARD_TYPE board_type, int page) {
        // TODO : authentication exception is required
        if (authResponse == null) {
            return null;
        }

        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setAccessToken(authResponse.getToken());

        ObjectMapper objectMapper = new ObjectMapper();
        String url = API_SERVER_URL + "/board/" + board_type + "/" + page;

        ApiResponse apiResponse;
        BoardPageResponse boardPageResponse = null;

        try {
            apiResponse = apiRequest.get(url);
            if (apiResponse.getStatus() == 200) {
                boardPageResponse = objectMapper.readValue(apiResponse.getResponse(), BoardPageResponse.class);
            } else {
                System.out.println(url);
                System.out.println(apiResponse.getResponse());
                throw new IOException("Can not access api server");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return boardPageResponse;
    }

    public BoardResponse writeBoard(BoardRequest boardRequest) {
        // TODO : authentication exception is required
        if (authResponse == null) {
            return null;
        }

        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setAccessToken(authResponse.getToken());

        ObjectMapper objectMapper = new ObjectMapper();
        String url = API_SERVER_URL + "/board";

        ApiResponse apiResponse;

        BoardResponse boardResponse = null;

        try {
            apiResponse = apiRequest.post(url, objectMapper.writeValueAsString(boardRequest));
            if (apiResponse.getStatus() == 200) {
                boardResponse = objectMapper.readValue(apiResponse.getResponse(), BoardResponse.class);
            } else {
                throw new IOException("Can not access api server");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return boardResponse;
    }

    public BoardResponse getBoard(int id) {
        // TODO : authentication exception is required
        if (authResponse == null) {
            return null;
        }

        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setAccessToken(authResponse.getToken());

        ObjectMapper objectMapper = new ObjectMapper();
        String url = API_SERVER_URL + "/board/" + id;

        ApiResponse apiResponse;

        BoardResponse boardResponse = null;

        try {
            apiResponse = apiRequest.get(url);
            if (apiResponse.getStatus() == 200) {
                boardResponse = objectMapper.readValue(apiResponse.getResponse(), BoardResponse.class);
            } else {
                Log.d(TAG, UserInfo.getInstance().toString());
                Log.d(TAG, String.valueOf(apiResponse.getStatus()));
                Log.d(TAG, apiResponse.getResponse());
                throw new IOException("Can not access api server");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return boardResponse;
    }
}