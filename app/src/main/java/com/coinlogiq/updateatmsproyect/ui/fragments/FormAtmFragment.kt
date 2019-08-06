package com.coinlogiq.updateatmsproyect.ui.fragments


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.coinlogiq.updateatmsproyect.R
import com.coinlogiq.updateatmsproyect.ui.extensions.toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.android.synthetic.main.fragment_form_atm.view.*


class FormAtmFragment : Fragment(){

    private lateinit var _view: View

    private lateinit var spinner: Spinner

    var locationRequest: LocationRequest?= null

    private val REQUEST_CODE = 101
    private val permisoFineLocation = Manifest.permission.ACCESS_FINE_LOCATION
    private val permisoCourseLocation = Manifest.permission.ACCESS_COARSE_LOCATION

    var fusedLoctaionClient: FusedLocationProviderClient? = null

    var callback:LocationCallback?= null

    val propietarios = arrayOf("Coinexperu",
            "MarcoMonteroATM",
            "DavidYao",
            "TrokeraTKA",
            "Bitncash",
            "CryptoMarket",
            "CriptoSpeedAtm",
            "CriptoCompany",
            "Trinity",
            "SatoshiMaster",
            "AlzaTrade",
            "CryptoCoinEx",
            "GlobalCoin",
            "ClogiqColombia",
            "conlogiq2")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _view =  inflater.inflate(R.layout.fragment_form_atm, container, false)

        fusedLoctaionClient = FusedLocationProviderClient(activity!!.applicationContext)
        inicializarLocationRequest()

        return _view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spinner = _view.findViewById(R.id.spinner_dueños) as Spinner
        spinner.adapter = ArrayAdapter(activity!!.applicationContext, R.layout.support_simple_spinner_dropdown_item,propietarios)
        spinner.setPrompt("Select Owner")
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val type = p0?.getItemAtPosition((p2)).toString()
                activity!!.toast("Aqui esta $type")
            }
        }

    }

    private fun validarPermisos(): Boolean{

        val hayUbicacionPrecisa =
            ActivityCompat.checkSelfPermission(_view.context,permisoFineLocation) == PackageManager.PERMISSION_GRANTED

        val hayUbucacionOrdinaria =
            ActivityCompat.checkSelfPermission(_view.context,permisoCourseLocation) == PackageManager.PERMISSION_GRANTED

        return hayUbicacionPrecisa && hayUbucacionOrdinaria
    }

    private fun pedirPermisos(){

        val racional = ContextCompat.checkSelfPermission(_view.context,permisoFineLocation)

        if(racional != PackageManager.PERMISSION_GRANTED){
            solicitudPermiso()
        }else{
            solicitudPermiso()
        }
    }

    private fun solicitudPermiso(){
        requestPermissions(arrayOf(permisoFineLocation, permisoCourseLocation), REQUEST_CODE)
    }

    @SuppressLint("MissingPermission")
    private fun obtenerUbicacion(){
        /* fusedLoctaionClient?.lastLocation?.addOnSuccessListener { location ->
            Log.d("Ubicacion", location.latitude.toString())
            if(location != null){
                activity!!.toast(location.latitude.toString() + " - " + location.longitude.toString())
            }
        }*/

        callback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                for(ubicacion in locationResult?.locations!!){
                    _view.textViewLatitudAtm.text = ubicacion.latitude.toString()
                    _view.textViewLongitudAtm.text = ubicacion.longitude.toString()
                }
            }
        }
        fusedLoctaionClient?.requestLocationUpdates(locationRequest, callback, null)
    }

    private fun inicializarLocationRequest(){
        locationRequest = LocationRequest()
        locationRequest?.interval = 10000
        locationRequest?.fastestInterval = 5000
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    override fun onDestroyView() {

        super.onDestroyView()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            REQUEST_CODE -> {
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //obtener Ubicacion
                }else{
                    activity!!.toast("No Se otorgaron los permisos necesarios")
                }
            }
        }
    }

    private fun detenerActualizacionUbicacion(){
        fusedLoctaionClient?.removeLocationUpdates(callback)
    }

    override fun onStart() {
        super.onStart()
        if(validarPermisos()){
            obtenerUbicacion()
        }else{
            pedirPermisos()
        }
    }

    override fun onPause() {
        super.onPause()
        detenerActualizacionUbicacion()
    }




}
