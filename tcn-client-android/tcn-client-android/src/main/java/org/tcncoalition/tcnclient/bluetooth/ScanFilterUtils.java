package org.tcncoalition.tcnclient.bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.os.Build;
import android.os.ParcelUuid;


import org.tcncoalition.tcnclient.TcnConstants;

import java.util.UUID;



public final class ScanFilterUtils
{
    private static final int MANUFACTURER_ID = 76;

    private ScanFilterUtils()
    {
    }

    public static ScanFilter getScanFilter()
    {
        return getScanFilter(TcnConstants.INSTANCE.getBEACON_UUID_SERVICE(),3838,4949);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static ScanFilter getScanFilter(UUID uuid,int major, int minor)
    {
        final ScanFilter.Builder builder = new ScanFilter.Builder();


        // the manufacturer data byte is the filter!
        final byte[] manufacturerData = new byte[]
        {
                0,0,

                // uuid
                0,0,0,0,
                0,0,
                0,0,
                0,0,0,0,0,0,0,0,

                // major
                0,0,

                // minor
                0,0,

                0
        };

        // the manufacturer data byte is the filter!
        final byte[] uuidManufacturerData = new byte[]
                {
                        0,0,

                        // uuid
                        0,0,0,0,
                        0,0,
                        0,0,
                        0,0,0,0,0,0,0,0,

                        0
                };

        // the mask tells what bytes in the filter need to match, 1 if it has to match, 0 if not
        final byte[] manufacturerDataMask = new byte[]
        {
                0,0,

                // uuid
                1,1,1,1,
                1,1,
                1,1,
                1,1,1,1,1,1,1,1,

                // major
                1,1,

                // minor
                1,1,

                0
        };

        final byte[] noMajorMinormanufacturerDataMask = new byte[]
                {
                        0,0,

                        // uuid
                        1,1,1,1,
                        1,1,
                        1,1,
                        1,1,1,1,1,1,1,1,

                        // major
                        0,0,

                        // minor
                        0,0,

                        0
                };

        // copy UUID (with no dashes) into data array
        System.arraycopy(ConversionUtils.UuidToByteArray(uuid), 0, manufacturerData, 2, 16);

        // copy major into data array
       // System.arraycopy(ConversionUtils.integerToByteArray(0), 0, manufacturerData, 18, 2);

        // copy minor into data array
        //System.arraycopy(ConversionUtils.integerToByteArray(0), 0, manufacturerData, 20, 2);

        builder.setManufacturerData(
                MANUFACTURER_ID,
                manufacturerData,noMajorMinormanufacturerDataMask);
        //builder.setServiceUuid(new ParcelUuid(TcnConstants.INSTANCE.getUUID_SERVICE()));

        return builder.build();
    }
    public static Boolean isBeacon(ScanResult scanResult)
    {
        return false;
    }
}
