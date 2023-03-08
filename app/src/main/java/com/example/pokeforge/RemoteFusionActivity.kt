package com.example.pokeforge

import NearbyManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.pokeforge.com.example.pokeforge.RemoteSelectionActivity
import com.example.pokeforge.databinding.ActivityRemoteFusionBinding
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import java.nio.charset.Charset


class RemoteFusionActivity : AppCompatActivity() {


    private lateinit var binding: ActivityRemoteFusionBinding

    private lateinit var playerRecyclerView: RecyclerView
    private lateinit var playerList: ArrayList<Map<String, String>>

    private lateinit var connectionLifecycleCallback: ConnectionLifecycleCallback
    private lateinit var payloadCallback: PayloadCallback

    @RequiresApi(Build.VERSION_CODES.M)
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

        binding = ActivityRemoteFusionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playerRecyclerView = binding.playerRecyclerView
        playerRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        playerList = ArrayList()

        var playerAdapter = PlayerAdapter(this, playerList, this)
        playerRecyclerView.adapter = playerAdapter




        val connectionsClient: ConnectionsClient = Nearby.getConnectionsClient(this)

        val strategy = Strategy.P2P_POINT_TO_POINT

        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(strategy).build()

        this.payloadCallback = object : PayloadCallback() {

            val nearbyManager = NearbyManager.getInstance()

            override fun onPayloadReceived(endpointId: String, payload: Payload) {
                println("onPayloadReceived")
                // Build a Toast to show that the payload was received.
                Toast.makeText(
                    this@RemoteFusionActivity,
                    "Payload received from endpoint $endpointId with type ${payload.type}",
                    Toast.LENGTH_LONG
                ).show()

                val data = String(payload.asBytes()!!, Charset.defaultCharset())
                // if the string begin with "newPokemonSelected" then it is a new pokemon selected
                if (data.startsWith("newPokemonSelected")) {
                    val value = String(payload.asBytes()!!, Charset.defaultCharset()).split(":")[1]
                    val dna1 :Int = value.split(",")[0].toInt()
                    val list = ArrayList<Int>()
                    list.add(dna1)
                    if(value.split(",").size > 1){
                        val dna2 :Int = value.split(",")[1].toInt()
                        list.add(dna2)
                    }
                    nearbyManager.advertiseNewSelection(list)
                } else if (data.startsWith("validate")) {
                    val value = String(payload.asBytes()!!, Charset.defaultCharset()).split(":")[1]
                    val dna1: Int = value.split(",")[0].toInt()
                    val list = ArrayList<Int>()
                    list.add(dna1)
                    if (value.split(",").size > 1) {
                        val dna2: Int = value.split(",")[1].toInt()
                        list.add(dna2)
                    }
                    val nearbyManager = NearbyManager.getInstance()
                    nearbyManager.advertiseValidation(list)
                } else if (data.startsWith("invalidate")) {
                    nearbyManager.advertiseInvalidation()
                }

            }

            override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) {
                println("onPayloadTransferUpdate")
                println(p0)
            }
        }

        connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
                // Show a confirmation dialog before accepting the connection
                AlertDialog.Builder(this@RemoteFusionActivity)
                    .setTitle("Accept connection")
                    .setMessage("Do you want to accept the connection from ${connectionInfo.endpointName}?")
                    .setPositiveButton("Yes") { dialog, which ->
                        // Accept the connection
                        connectionsClient.acceptConnection(endpointId, payloadCallback)
                        println("Accepted connection")
                        //Intent(this@LocalFusionActivity, RemoteSelectionActivity::class.java)
                        // Put the conn
                    }
                    .setNegativeButton("No") { dialog, which ->
                        // Reject the connection
                        connectionsClient.rejectConnection(endpointId)
                    }
                    .show()
            }

            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                if (result.status.isSuccess) {
                    // We're connected! Can now start sending and receiving data.
                    println("We're connected! Can now start sending and receiving data.")
                    // Start activity RemoteSelectionActivity
                    val intent = Intent(this@RemoteFusionActivity, RemoteSelectionActivity::class.java)
                    intent.putExtra("connectionId", endpointId)
                    println("Starting RemoteSelectionActivity")
                    startActivity(intent)
                } else {
                    // We were unable to connect to the other device.
                    println("We were unable to connect to the other device.")
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
                playerList.add(mapOf("name" to name, "id" to endpointId))

                System.out.println("We found an endpoint!")
                playerAdapter = PlayerAdapter(this@RemoteFusionActivity, playerList, this@RemoteFusionActivity)
                playerRecyclerView.adapter = playerAdapter

                checkIfEmptyMessage(playerList.size)

            }

            override fun onEndpointLost(endpointId: String) {
                // Remove the lost endpoint from the list
                val name = playerList.find { it["id"] == endpointId }?.get("name")
                playerList.remove(mapOf("name" to name, "id" to endpointId))
                playerAdapter = PlayerAdapter(this@RemoteFusionActivity, playerList, this@RemoteFusionActivity)
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

    fun checkIfEmptyMessage(size: Int){
        if(size == 0){
            binding.emptyMessage.visibility = View.VISIBLE
        } else {
            binding.emptyMessage.visibility = View.GONE
        }
    }

    fun connectToPlayer(id: String) {
        val connectionsClient: ConnectionsClient = Nearby.getConnectionsClient(this)

        val RequestConnectionLifecycleCallback = object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
                // Automatically accept the connection on both sides.
                connectionsClient.acceptConnection(endpointId, payloadCallback)

            }

            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                if (result.status.isSuccess) {
                    // We're connected! Can now start sending and receiving data.
                    System.out.println("We're connected! Can now start sending and receiving data.")
                    val intent = Intent(this@RemoteFusionActivity, RemoteSelectionActivity::class.java)
                    intent.putExtra("connectionId", endpointId)
                    println("Starting RemoteSelectionActivity")
                    startActivity(intent)
                } else {
                    // We were unable to connect to the other device.
                    System.out.println("We were unable to connect to the other device.")
                }
            }

            override fun onDisconnected(endpointId: String) {
                // We've been disconnected from this endpoint. No more data can be
                // sent or received.
            }
        }

        connectionsClient.requestConnection(
            "PokeForge",
            id,
            RequestConnectionLifecycleCallback
        )
            .addOnSuccessListener {
                // We successfully requested a connection. Now both sides
                // must accept before the connection is established.
                System.out.println("We successfully requested a connection. Now both sides must accept before the connection is established.")
            }
            .addOnFailureListener {
                // Nearby Connections failed to request the connection.
                System.out.println("Nearby Connections failed to request the connection.")
            }
    }

}