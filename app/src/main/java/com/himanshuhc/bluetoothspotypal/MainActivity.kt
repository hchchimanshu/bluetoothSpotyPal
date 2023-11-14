package com.himanshuhc.bluetoothspotypal

import android.Manifest
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.spotypal.spotypalconnect.service.BleInterface
import com.spotypal.spotypalconnect.service.BleScanner
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener{

    lateinit var bleScanner : BleScanner
    lateinit var bleInterface : BleInterface
    var bluetoothAdapter : BluetoothAdapter? = null
    var bluetoothManager : BluetoothManager? = null
    lateinit var progressDialog : ProgressDialog
    var scanningDisposable : Disposable? = null
    var connectionDisposable : Disposable? = null
    var isConnected : Boolean = false
    var ringDeviceValue : Boolean = false
    val ACCESS_FINE_LOCATION_CODE = 0
    val STORAGE_BLUETOOTH_SCAN_CODE = 1


    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_PRIVILEGED
    )
    private val PERMISSIONS_LOCATION = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_PRIVILEGED
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scan_TV.setOnClickListener(this)
        connect_disconnect_TV.setOnClickListener(this)
        connect_disconnect_TV.visibility = View.GONE
        ring_device_TV.setOnClickListener(this)
        ring_device_TV.visibility = View.GONE

        bluetoothInit()

        fineLocationPermission()

        checkStorageAndScanPermission()
    }

    private fun bluetoothInit() {
        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
        bluetoothAdapter = bluetoothManager!!.adapter
        bleScanner = BleScanner(this, bluetoothAdapter!!)
    }

    private fun checkStorageAndScanPermission() {
        //write external permission
        var writeExternalPermission = ActivityCompat.checkSelfPermission(this,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        //bluetooth scan permission
        var bluetoothScanPermission = ActivityCompat.checkSelfPermission(this,
        android.Manifest.permission.BLUETOOTH_SCAN)

        if (writeExternalPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, STORAGE_BLUETOOTH_SCAN_CODE)
        }else if (bluetoothScanPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, PERMISSIONS_LOCATION, STORAGE_BLUETOOTH_SCAN_CODE)
        }

    }

    private fun fineLocationPermission() {
        //access fine location
        if (ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {

            ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), ACCESS_FINE_LOCATION_CODE)
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == ACCESS_FINE_LOCATION_CODE){
            checkStorageAndScanPermission()
        }
        else if(requestCode == STORAGE_BLUETOOTH_SCAN_CODE){
            if (!bluetoothAdapter!!.isEnabled()){
                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivity(enableBluetoothIntent)
            }
        }

    }

    override fun onClick(v: View?) {
        when (v?.id){
            R.id.scan_TV -> {
                if (!bleScanner!!.isScanning()){
                    showDialogBox("Scanning...")
                    scanningDisposable = bleScanner.startScanForDevices().subscribe({   macAddress ->
                        bleInterface = BleInterface(
                            this,
                            bluetoothAdapter!!, macAddress
                        )
                        connection_status_TV.visibility = View.VISIBLE
                        ring_device_TV.visibility = View.GONE
                        device_name_TV.setText(macAddress)
                        dismissDialogBox()

                        dismissDialogBox()
                    },{ error ->

                    })
                }
                else{
                    showDialogBox("Stop Scanning...")
                    if (scanningDisposable!=null){
                        scanningDisposable!!.dispose()
                        scanningDisposable = null
                    }
                    dismissDialogBox()
                }
            }
            R.id.connect_disconnect_TV -> {
                if (isConnected && connectionDisposable != null) {
                    showDialogBox("Disconnecting...")
                    connectionDisposable!!.dispose()
                    Toast.makeText(this, "New connection state: Disconnected", Toast.LENGTH_SHORT)
                        .show()
//                    Log.d("FadeMove", "onClick: New connection state: Disconnected")
                    connectionDisposable = null
                    dismissDialogBox()
                    connection_status_TV.setText("Connection Status : Disconnected")
                    ring_device_TV.setVisibility(View.GONE)
                } else {
                    showDialogBox("Connecting...")
                    connectionDisposable =
                        bleInterface.connect(false).subscribe(Consumer<String> { s ->
                            runOnUiThread {
                                dismissDialogBox()
                                Toast.makeText(
                                    this@MainActivity,
                                    "New connection state:$s",
                                    Toast.LENGTH_SHORT
                                ).show()
//                                Log.d("FadeMove", "run: $s")
                                connect_disconnect_TV.setText("Connection Status : $s")
                                ring_device_TV.setVisibility(View.VISIBLE)
                                isConnected = true
                            }
                        })
                }
            }
            R.id.ring_device_TV -> {
                if (!ringDeviceValue) {
                    ringDeviceValue = true
                    ring_device_TV.setText("Stop Ringing")
                }else{
                    ringDeviceValue = false
                    ring_device_TV.setText("Ring Device");
                }
                bleInterface.toggleDeviceRing(ringDeviceValue);
            }
        }
    }

    private fun dismissDialogBox() {
        progressDialog.dismiss()
    }

    private fun showDialogBox(message : String) {
        progressDialog = ProgressDialog.show(this,"",message)
    }


}