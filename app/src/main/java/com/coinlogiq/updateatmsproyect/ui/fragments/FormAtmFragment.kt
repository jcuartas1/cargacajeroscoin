package com.coinlogiq.updateatmsproyect.ui.fragments


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
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
import androidx.core.content.FileProvider
import com.coinlogiq.updateatmsproyect.R
import com.coinlogiq.updateatmsproyect.model.form.Form
import com.coinlogiq.updateatmsproyect.model.form.NewFormEvent
import com.coinlogiq.updateatmsproyect.model.logs.Logs
import com.coinlogiq.updateatmsproyect.ui.extensions.toast
import com.coinlogiq.updateatmsproyect.utils.RxBus
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_form_atm.*
import kotlinx.android.synthetic.main.fragment_form_atm.view.*
import kotlinx.android.synthetic.main.fragment_form_atm.view.imgViewCamera
import kotlinx.android.synthetic.main.login_activity.*
import java.io.File
import java.io.IOException
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*
import java.util.EventListener
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class FormAtmFragment : Fragment(){

    /*
    * Variables base de firebase*/

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var formDBRef: CollectionReference
    private lateinit var logsDBRef: CollectionReference

    private var storage:FirebaseStorage? = null
    private  var mStorgeRef:StorageReference? = null

    private var formsSubscription: ListenerRegistration? = null
    private lateinit var formBusListener: Disposable

    private lateinit var _view: View

    private var filePath: Uri? = null

    private lateinit var spinner: Spinner
    private var owner: String = ""

    var locationRequest: LocationRequest?= null

    private val REQUEST_CODE_CAMERA = 1

    private val REQUEST_CODE = 101
    private val permisoFineLocation = Manifest.permission.ACCESS_FINE_LOCATION
    private val permisoCourseLocation = Manifest.permission.ACCESS_COARSE_LOCATION
    private val permisoCamera = Manifest.permission.CAMERA
    private val permisoWriteStorage = Manifest.permission.WRITE_EXTERNAL_STORAGE
    private val permisoReadStorage = Manifest.permission.READ_EXTERNAL_STORAGE

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

        //Funcion para crear la base de datos
        setUpFormB()
        setUpCurrentUser()

        suscribeToNewForms()
        //suscribeToForms()

        setUpbtnGuardar()

        setTomarFoto()

        return _view
    }

    fun setTomarFoto() {
        _view.imgBtnCamera.setOnClickListener {
            //dispararIntentFoto()
            pedirPermisoCamera()
        }
    }

   private fun dispararIntentSelecionarFoto(){
       val intent = Intent(Intent.ACTION_GET_CONTENT)
       intent.setType("image/*")
       startActivityForResult(Intent.createChooser(intent,""),REQUEST_CODE_CAMERA)

   }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_CODE_CAMERA ->{
                if(resultCode == RESULT_OK && data != null
                    && data.data != null){
                    filePath = data.data
                    try{
                        val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver,filePath)
                        _view.imgViewCamera.setImageBitmap(bitmap)

                    }catch (e:IOException){
                        e.printStackTrace()
                    }
                }
            }
        }
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
                owner = type
                activity!!.toast("Aqui esta $owner")
            }
        }

    }

    private fun saveLogs(logs: Logs){
        val newLogs = HashMap<String, Any>()
        newLogs["userId"] = logs.userId
        newLogs["operacion"] = logs.operacion
        newLogs["atmId"] = logs.atmId
        newLogs["observacion"] = logs.observacion
        newLogs["creatLogDate"] = logs.creatLogDate

        logsDBRef.add(newLogs)
            .addOnCompleteListener {
                activity!!.toast("Logs added!")
            }
            .addOnFailureListener {
                activity!!.toast("Logs error, try again!")
            }
    }

    private fun saveform(formulario: Form){
        val newForm = HashMap<String, Any>()
        newForm["atmUbicacion"] = formulario.atmUbicacion
        newForm["atmDireccion"] = formulario.atmDireccion
        newForm["atmDueno"] = formulario.atmDueno
        newForm["atmId"] = formulario.atmId
        newForm["atmLongitud"] = formulario.atmLongitud
        newForm["atmLatitud"] = formulario.atmLatitud
        newForm["imgPath"] = formulario.imgPath
        /*newForm["buy_btc_percent"] = formulario.buy_btc_percent
        newForm["buy_dash_percent"] = formulario.buy_dash_percent
        newForm["buy_ltc_percent"] = formulario.buy_ltc_percent
        newForm["buy_eth_percent"] = formulario.buy_eth_percent
        newForm["sell_btc_percent"] = formulario.sell_btc_percent
        newForm["sell_dash_percent"] = formulario.sell_dash_percent
        newForm["sell_ltc_percent"] = formulario.sell_ltc_percent
        newForm["sell_eth_percent"] = formulario.sell_eth_percent*/
        newForm["creatDate"] = formulario.creatDate


            formDBRef.add(newForm)
                .addOnCompleteListener{
                    activity!!.toast("Form added!")
                }
                .addOnFailureListener {
                    activity!!.toast("Form error, try again!")
                }
     }

    private fun setUpbtnGuardar(){
        _view.btn_guardar.setOnClickListener {
            val textLocal = _view.editTextLocalName.text.toString()
            val textDir = _view.editTextDireccion.text.toString()
            val textIdATM = view!!.editTextidOwner.text.toString()
            if(textLocal.isNotEmpty() && textDir.isNotEmpty()
                && textIdATM.isNotEmpty() && filePath != null){

                formsSubscription = formDBRef.whereEqualTo("atmId", textIdATM)
                    .addSnapshotListener(object : EventListener, com.google.firebase.firestore.EventListener<QuerySnapshot>{
                        override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                            p1?.let {
                                activity!!.toast("Exception!")
                                return
                            }
                            p0?.let {
                                Log.d("suscribe", it.isEmpty.toString())
                                if(it.isEmpty){
                                    val imagesRef = mStorgeRef!!.child("UpLoads/"+ UUID.randomUUID().toString())
                                        imagesRef.putFile(filePath!!)
                                            .addOnSuccessListener {
                                                Handler().postDelayed({
                                                    _view.progress_bar.progress = 0
                                                    _view.progress_bar.visibility = View.GONE
                                                    val form = Form(
                                                        textLocal,
                                                        textDir,
                                                        owner,
                                                        textIdATM.toUpperCase(),
                                                        _view.textViewLongitudAtm.text.toString(),
                                                        _view.textViewLatitudAtm.text.toString(),
                                                        imagesRef.path,
                                                        Date()
                                                    )
                                                    RxBus.publish(NewFormEvent(form))
                                                    Handler().postDelayed({
                                                        limpiarFormulario()
                                                    },1000)

                                                },2000)
                                            }
                                            .addOnFailureListener{
                                                activity!!.toast("PROBLEMA AL SUBIR LA FOTO")
                                            }
                                            .addOnProgressListener { taskSnapshot ->
                                                val proPregress = 100.0 *
                                                        taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                                                _view.progress_bar.visibility = View.VISIBLE
                                                _view.progress_bar.progress = proPregress.toInt()
                                            }
                                }else{
                                    Handler().postDelayed({
                                        activity!!.toast("EL ATM YA ESTA REGISTRADO")
                                    },2000)
                                }
                            }

                        }

                    })
            }else{
                activity!!.toast("Por Favor complete todos los campos")
            }
        }

    }

    private fun limpiarFormulario(){
        _view.editTextLocalName.setText("")
        _view.editTextDireccion.setText("")
        _view.editTextidOwner.setText("")
        _view.imgViewCamera.setImageResource(android.R.drawable.ic_menu_gallery)
    }

    private fun suscribeToNewForms(){
        formBusListener = RxBus.listen(NewFormEvent::class.java).subscribe {
            saveform(it.form)
        }
    }


    private fun setUpFormB(){
        formDBRef  = store.collection("form")
        logsDBRef = store.collection("logs")

        storage = FirebaseStorage.getInstance()
        mStorgeRef = storage!!.reference
    }

    private fun setUpCurrentUser(){
        currentUser = mAuth.currentUser!!
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

    private fun pedirPermisoCamera(){
        val racional = ContextCompat.checkSelfPermission(_view.context,permisoCamera)

        if(racional != PackageManager.PERMISSION_GRANTED){
            solicitudPermisoCamera()
        }else{
            solicitudPermisoCamera()
        }
    }

    private fun solicitudPermisoCamera(){
        requestPermissions(arrayOf(
            permisoCamera,
            permisoReadStorage,
            permisoWriteStorage), REQUEST_CODE_CAMERA)
    }

    private fun solicitudPermiso(){
        requestPermissions(arrayOf(permisoFineLocation,
            permisoCourseLocation), REQUEST_CODE)
    }

    @SuppressLint("MissingPermission")
    private fun obtenerUbicacion(){
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
        formBusListener.dispose()
        formsSubscription?.remove()
        super.onDestroyView()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            REQUEST_CODE -> {
                if(grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //obtener Ubicacion
                    obtenerUbicacion()
                }else{
                    activity!!.toast("No Se otorgaron los permisos necesarios")
                }
            }
            REQUEST_CODE_CAMERA -> {
                if(grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED){
                    //Disparar intent foto
                    //dispararIntentFoto()
                    dispararIntentSelecionarFoto()
                }else{
                    activity!!.toast("No Se otorgaron los permisos necesarios para la camara")
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
