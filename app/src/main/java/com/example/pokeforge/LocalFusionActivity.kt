package com.example.pokeforge

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import androidx.recyclerview.widget.RecyclerView
import com.example.pokeforge.databinding.ActivityLocalFusionBinding
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.android.gms.nearby.connection.ConnectionsClient


class LocalFusionActivity : AppCompatActivity() {


    private lateinit var binding: ActivityLocalFusionBinding

    private lateinit var playerRecyclerView: RecyclerView
    private lateinit var playerList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val REQUIRED_PERMISSIONS =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_ADVERTISE,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
            }

        requestPermissions(REQUIRED_PERMISSIONS, 1)

        binding = ActivityLocalFusionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playerRecyclerView = binding.playerRecyclerView
        playerRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        playerList = ArrayList()
        playerList.add("Player 1")
        playerList.add("Player 2")

        var playerAdapter = PlayerAdapter(this, playerList, this)
        playerRecyclerView.adapter = playerAdapter




        val connectionsClient: ConnectionsClient = Nearby.getConnectionsClient(this)

        val strategy = Strategy.P2P_POINT_TO_POINT

        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(strategy).build()

        val payloadCallback = object : PayloadCallback() {
            override fun onPayloadReceived(endpointId: String, payload: Payload) {
                // Handle the payload
            }

            override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
                // Handle progress
            }
        }

        val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
                // Automatically accept the connection on both sides.
                connectionsClient.acceptConnection(endpointId, payloadCallback)
            }

            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                if (result.status.isSuccess) {
                    // We're connected! Can now start sending and receiving data.
                } else {
                    // We were unable to connect to the other device.
                }
            }

            override fun onDisconnected(endpointId: String) {
                // We've been disconnected from this endpoint. No more data can be
                // sent or received.
            }
        }

        connectionsClient.startAdvertising(
            "PokeForge",
            "com.example.pokeforge",
            connectionLifecycleCallback,
            advertisingOptions
        )
            .addOnSuccessListener {
                // We're advertising!
                println("We're advertising!")
            }
            .addOnFailureListener {
                // We were unable to start advertising.
                println("We were unable to start advertising.")
            }

        val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
            override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                // Add the discovered endpoint to the list
                val name = info.endpointName
                playerList.add(name)

                System.out.println("We found an endpoint!")
                playerAdapter = PlayerAdapter(this@LocalFusionActivity, playerList, this@LocalFusionActivity)
                playerRecyclerView.adapter = playerAdapter

                // Request a connection to the endpoint
                connectionsClient.requestConnection(
                    "PokeForge",
                    endpointId,
                    connectionLifecycleCallback
                )
                    .addOnSuccessListener {
                        // We successfully requested a connection. Now both sides
                        // must accept before the connection is established.
                    }
                    .addOnFailureListener {
                        // Nearby Connections failed to request the connection.
                    }
            }

            override fun onEndpointLost(endpointId: String) {
                // Remove the lost endpoint from the list
                playerList.remove(endpointId)
                playerAdapter = PlayerAdapter(this@LocalFusionActivity, playerList, this@LocalFusionActivity)
                playerRecyclerView.adapter = playerAdapter
            }
        }

        connectionsClient.startDiscovery(
            "com.example.pokeforge",
            endpointDiscoveryCallback,
            DiscoveryOptions.Builder().setStrategy(strategy).build()
        )
            .addOnSuccessListener {
                // We're discovering!
                System.out.println("We're discovering!")
            }
            .addOnFailureListener {
                // We were unable to start discovering.
                System.out.println("We were unable to start discovering.")
            }


    }

}