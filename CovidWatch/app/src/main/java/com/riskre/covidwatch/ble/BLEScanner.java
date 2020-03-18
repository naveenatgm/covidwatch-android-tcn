package com.riskre.covidwatch.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.riskre.covidwatch.utils.UUIDAdapter;
import com.riskre.covidwatch.utils.UUIDs;
import com.riskre.covidwatch.data.ContactEvent;
import com.riskre.covidwatch.data.ContactEventDAO;
import com.riskre.covidwatch.data.CovidWatchDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BLEScanner {

    // CONSTANTS
    private static final String TAG = "BLEScanner";

    // BLE
    private BluetoothLeScanner scanner;

    // CONTEXT
    Context context;

    /**
     * BLEScanner is responsible for scanning the advertisements containing the CEN
     * and logging that information in the Room database.
     *
     * @param ctx The context this object is in
     * @param adapter The default adapter to use for BLE
     */
    public BLEScanner(Context ctx, BluetoothAdapter adapter) {
        scanner = adapter.getBluetoothLeScanner();
        context = ctx;
    }

    /**
     * Callback when scanning start and stops
     */
    private final ScanCallback scanCallback;
    {
        scanCallback = new ScanCallback() {
            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                for (ScanResult result : results) {

                    Log.w(TAG, "Contact Event number: "+result.getScanRecord().getServiceData().toString());
                    Log.w(TAG, "Signal strength: "+result.getRssi());
                    Log.w(TAG, "Found another human: " + result.getDevice().getAddress());

                    UUID contactEventNumber = UUIDAdapter.getUUIDFromBytes(
                                                    result.getScanRecord().getServiceData().get(
                                                            new ParcelUuid(UUIDs.CONTACT_EVENT_SERVICE)));

                    CovidWatchDatabase.databaseWriteExecutor.execute(() -> {
                        // Populate the database in the background.
                        // If you want to start with more words, just add them.
                        ContactEventDAO dao = CovidWatchDatabase.getDatabase(context).contactEventDAO();
                        ContactEvent old = dao.findByPrimaryKey(contactEventNumber.toString());

                        // If the signal is stronger, update the RSSI value, otherwise just add the Event
                        // to the DB if it doesn't exist
                        if(old != null && old.getSignalStrength() > result.getRssi()){
                            old.setSignalStrength(result.getRssi());
                            dao.update(old);
                        } else if (old == null){
                            ContactEvent cen = new ContactEvent(contactEventNumber.toString(), result.getRssi());
                            dao.insert(cen);
                        }
                    });
                }
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startScanning(UUID[] serviceUUIDs){

        List<ScanFilter> filters = null;

        // construct filters from serviceUUIDs
        if(serviceUUIDs != null) {
            filters = new ArrayList<>();
            for (UUID serviceUUID : serviceUUIDs) {
                ScanFilter filter = new ScanFilter.Builder()
                        .setServiceUuid(new ParcelUuid(serviceUUID))
                        .build();
                filters.add(filter);
            }
        }

        // we use low power scan mode to conserve battery,
        // CALLBACK_TYPE_ALL_MATCHES will run the callback for every discovery
        // instead of batching them up. MATCH_MODE_AGGRESSIVE will try to connect
        // even with 1 advertisement.
        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
                .setReportDelay(30000)
                .build();

        // The scan filter is incredibly important to allow android to run scans
        // in the background
        if (scanner != null) {
            scanner.startScan(filters, scanSettings, scanCallback);
            Log.i(TAG, "scan started");
        }  else {
            Log.e(TAG, "could not get scanner object");
            // TODO error handling
        }
    }

    public void stopScanning() {
        scanner.stopScan(scanCallback);
    }
}

