package com.meamobile.printicular_sdk.core;

import android.app.Activity;
import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;

import com.meamobile.printicular_sdk.core.models.Address;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class PrinticularServiceManagerTest
{
    @Before
    public void setUp()
    {

    }


    @Test
    public void it_can_fetch_addresses() throws Exception
    {

        PrinticularServiceManager serviceManager = PrinticularServiceManager.getInstance();

        serviceManager.validateAccessToken().subscribe();
    }


}