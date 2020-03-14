package com.geekwims.hereiam;

import com.geekwims.hereiam.api.ApiService;
import com.geekwims.hereiam.api.request.AuthenticationRequest;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void authenticateTest() throws Exception {
        ApiService apiService = ApiService.getInstance();

        apiService.refreshAccessToken(new AuthenticationRequest("21360017", "password", "ROLE_STUDENT"));
    }

    @Test
    public void getTimeTableTest() throws Exception {
        ApiService apiService = ApiService.getInstance();

        apiService.refreshAccessToken(new AuthenticationRequest("21360017", "password", "ROLE_STUDENT"));

        String[][] table = apiService.getTimeTable().getSchedule();

        for (String[] row : table) {
            for (String aRow : row) {
                System.out.print(aRow + " ");
            }
            System.out.println();
        }
    }

    @Test
    public void calendarTest() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_WEEK, 0);

        System.out.println(calendar.getTime().toString());
    }
}