package techtown.org.kotlintest.mySchedule

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.common.api.Status

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import techtown.org.kotlintest.R
import techtown.org.kotlintest.databinding.ActivityMapBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapBinding
    var placesClient: PlacesClient? = null

    private lateinit var location: String

    var latitude: Double = 23.0225
    var longitude: Double = 72.5714

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val apiKey = getString(R.string.google_maps_key)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }

        if(intent.hasExtra("latitude") && intent.hasExtra("longitude")
            && intent.hasExtra("location")) {

            location = intent.getStringExtra("location")!!
            latitude = intent.getDoubleExtra("latitude", 0.0)!!
            longitude = intent.getDoubleExtra("longitude", 0.0)!!
        } else {
            location = ""
        }

        // Create a new Places client instance.
        placesClient = Places.createClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as AutocompleteSupportFragment?
        autocompleteFragment!!.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
            )
        )

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                location = place.address.toString()

                latitude = place.latLng?.latitude!!
                longitude = place.latLng?.longitude!!

                Toast.makeText(applicationContext, "${latitude}", Toast.LENGTH_SHORT).show()
                Toast.makeText(applicationContext, "${longitude}", Toast.LENGTH_SHORT).show()

                val searchedLocation = LatLng(latitude, longitude)
                mMap.addMarker(MarkerOptions().position(searchedLocation).title("Searched Location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(searchedLocation))
            }

            override fun onError(status: Status) {
                Toast.makeText(applicationContext, status.toString(), Toast.LENGTH_SHORT).show()
            }
        })

        binding.applyLocation.setOnClickListener {
            val resultIntent = Intent()

            resultIntent.putExtra("location", location)
            resultIntent.putExtra("latitude", latitude)
            resultIntent.putExtra("longitude", longitude)

            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Ahmedabad and move the camera
        val Ahmedabad = LatLng(latitude, longitude)
        mMap.addMarker(MarkerOptions().position(Ahmedabad).title("Set Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Ahmedabad))
    }
}

/*
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapView_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    */
/**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     *//*

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}*/
