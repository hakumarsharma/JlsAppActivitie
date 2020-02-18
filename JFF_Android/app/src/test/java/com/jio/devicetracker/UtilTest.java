package com.jio.devicetracker;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import com.jio.devicetracker.database.pojo.SearchDevice;
import com.jio.devicetracker.util.Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class UtilTest{

    private Context context = ApplicationProvider.getApplicationContext();

    @Test
    public void getInstance() {
        Util util = Util.getInstance();
        assertNotNull(util);
    }

    @Test
    public void toJSON() {
        Util util = Util.getInstance();
        SearchDevice searchDevice = new SearchDevice();
        List<String> usersAssignedList = new ArrayList<>();
        usersAssignedList.add("123456789876543");
        searchDevice.setUsersAssigned(usersAssignedList);
        String jsonResponse = util.toJSON(searchDevice);
        assertEquals(jsonResponse, "{\"usersAssigned\":[\"123456789876543\"]}");
    }

    @Test
    public void getPojoObject() {
        SearchDevice searchDevice = Util.getInstance().getPojoObject("{\"usersAssigned\":[\"123456789876543\"]}", SearchDevice.class);
        assertNotNull(searchDevice);
    }

    @Test
    public void isMobileNetworkAvailable() {
        boolean isMobileNetworkAvailable = Util.isMobileNetworkAvailable(context);
        assertTrue(isMobileNetworkAvailable);
    }

    @Test
    public void getIMEI() {
        String imeiNumber = Util.getInstance().getIMEI(context);
        assertNotNull(imeiNumber);
        assertTrue(!imeiNumber.isEmpty());
    }

    @Test
    public void isValidEmailId() {
        boolean isValidEmailId = Util.isValidEmailId("harish1.kumar@radisys.com");
        assertTrue(isValidEmailId);
        boolean isNotValidEmailId = Util.isValidEmailId("harish1kumarradisys.com");
        assertFalse(isNotValidEmailId);
        boolean isNullEmailId = Util.isValidEmailId(null);
        assertFalse(isNullEmailId);
    }

    @Test
    public void isValidPassword() {
        boolean isValidPassword = Util.isValidPassword("Radisys@123456");
        assertTrue(isValidPassword);
        boolean isNotValidPassword = Util.isValidPassword("Radisys123456");
        assertFalse(isNotValidPassword);
        assertFalse(Util.isValidPassword(null));
    }

    @Test
    public void convertTimeToEpochtime() {
        long epochTime = Util.convertTimeToEpochtime();
        assertNotNull(epochTime);
    }

    @Test
    public void getTimeEpochFormatAfterCertainTime() {
        long epochTimeAfterTimeInterval = Util.getTimeEpochFormatAfterCertainTime(15);
        assertNotNull(epochTimeAfterTimeInterval);
    }

    @Test
    public void showProgressBarDialog() {
        System.out.println("Inside test method");
    }

    @Test
    public void showProgressBarDialog1() {
        System.out.println("Inside test method");
    }

    @Test
    public void dismissProgressBarDialog() {
        System.out.println("Inside test method");
    }

    @Test
    public void setTermconditionFlag() {
        System.out.println("Inside test method");
    }

    @Test
    public void getTermconditionFlag() {
        boolean getTermconditionFlag = Util.getTermconditionFlag(context);
        if(Util.sharedpreferences !=  null){
            assertTrue(getTermconditionFlag);
        }
        else{
            assertFalse(getTermconditionFlag);
        }
    }

    @Test
    public void setAutologinStatus() {
        System.out.println("Inside test method");
    }

    @Test
    public void getAutologinStatus() {
        System.out.println("Inside test method");
    }

    @Test
    public void clearAutologinstatus() {
        System.out.println("Inside test method");
    }

    @Test
    public void getMQTTTimeFormat() {
        String mqttTimeFormat = Util.getInstance().getMQTTTimeFormat();
        assertNotNull(mqttTimeFormat);
    }

    @Test
    public void isValidIMEINumber() {
        boolean isValidIMEINumber = Util.isValidIMEINumber("123456789009876");
        assertTrue(isValidIMEINumber);
        boolean isNotValidIMEINumber = Util.isValidIMEINumber("123456789560");
        assertFalse(isNotValidIMEINumber);
    }

    @Test
    public void isValidMobileNumber() {
        boolean isValidMobileNumber = Util.isValidMobileNumber("9801234563");
        assertTrue(isValidMobileNumber);
        boolean isNotValidMobileNumber = Util.isValidMobileNumber("123456789560");
        assertFalse(isNotValidMobileNumber);
    }
}