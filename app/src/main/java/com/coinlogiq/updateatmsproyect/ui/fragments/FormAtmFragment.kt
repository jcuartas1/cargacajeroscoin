package com.coinlogiq.updateatmsproyect.ui.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.coinlogiq.updateatmsproyect.R
import com.coinlogiq.updateatmsproyect.ui.extensions.toast


class FormAtmFragment : Fragment(){

    private lateinit var _view: View

    private lateinit var spinner: Spinner

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

    override fun onDestroyView() {

        super.onDestroyView()
    }




}
